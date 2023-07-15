package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;

@ExtensionMethod({ JComponentExtension.class })
public class JListWithImages<T> extends JList<JListWithImages.LabelPanel<T>> {

    @Serial
    private static final long serialVersionUID = 342783165266555869L;

    private final Function<T, String> toStringMapper;
    private final boolean distinctValues;

    private JListWithImages(Function<T, String> toStringMapper, boolean distinctValues) {
        this.toStringMapper = toStringMapper == null ? Object::toString : toStringMapper;
        this.distinctValues = distinctValues;
        setCellRenderer(new ImageListCellRenderer());
        setModel(new DefaultListModel<>());
    }

    public static <T> JListWithImages<T> forType(Class<T> type) {
        return createForType(type).build();
    }

    public static <T> JListWithImagesBuilder<T> createForType(Class<T> type) {
        return new JListWithImagesBuilder<>();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class JListWithImagesBuilder<T> {
        private Function<T, String> toStringMapper;
        private boolean distinctValues;

        public JListWithImagesBuilder<T> distinctValues() {
            return distinctValues(true);
        }

        public JListWithImages<T> build() {
            return new JListWithImages<>(toStringMapper, distinctValues);
        }
    }

    public void addItems(Image image, Collection<T> values) {
        values.forEach(value -> addItem(image, value));
    }

    public void addItem(Image image, T value) {
        if (!distinctValues || !contains(value)) {
            ((DefaultListModel<LabelPanel<T>>) getModel()).addElement(new LabelPanel<>(image, value, toStringMapper, SwingConstants.LEFT));
        }
    }

    public void removeSelectedItem() {
        DefaultListModel<LabelPanel<T>> model = (DefaultListModel<LabelPanel<T>>) getModel();
        int selected = getSelectedIndex();
        if (!model.isEmpty() && selected >= 0) {
            model.removeElementAt(selected);
        }
    }

    public T getObject(int index) {
        return getLabelPanel(index).map(LabelPanel::getObject).orElse(null);
    }

    public Image getImage(int index) {
        return getLabelPanel(index).map(LabelPanel::getImage).orElse(null);
    }

    public Stream<LabelPanel<T>> stream() {
        return IntStream.range(0, getModel().getSize()).mapToObj(getModel()::getElementAt);
    }

    public boolean contains(T object) {
        return stream().map(LabelPanel::getObject).anyMatch(obj -> Objects.equal(obj, object));
    }

    private Optional<LabelPanel<T>> getLabelPanel(int index) {
        return Optional.ofNullable(getModel().getElementAt(index));
    }

    @Getter
    public static class LabelPanel<T> extends JPanel {

        private static final long serialVersionUID = 1L;
        private final Label<T> label;

        LabelPanel(Image image, T object, Function<T, String> toStringMapper, int horizontalAlignment) {
            this.label = new Label<>(image, object, toStringMapper, horizontalAlignment);
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(label);
        }

        public T getObject() {
            return label.getObject();
        }

        public Image getImage() {
            return label.getImage();
        }
    }

    @Getter
    private static class Label<T> extends JLabel {
        private static final long serialVersionUID = 1L;
        private final T object;
        private final Image image;

        Label(Image image, T object, Function<T, String> toStringMapper, int horizontalAlignment) {
            super(toStringMapper.apply(object), getImageIcon(image), horizontalAlignment);
            this.object = object;
            this.image = image;
        }

        private static ImageIcon getImageIcon(Image image) {
            return image == null ? new ImageIcon() : resizeIcon(new ImageIcon(image), 20, 20);
        }

        private static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
            return new ImageIcon( icon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
        }
    }
}
