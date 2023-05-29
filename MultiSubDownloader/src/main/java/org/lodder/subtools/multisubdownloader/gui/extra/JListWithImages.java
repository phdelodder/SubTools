package org.lodder.subtools.multisubdownloader.gui.extra;

import java.io.Serial;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;

import com.google.common.base.Objects;

import java.awt.FlowLayout;
import java.awt.Image;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ JComponentExtension.class })
public class JListWithImages<T> extends JList<JListWithImages.LabelPanel<T>> {

    @Serial
    private static final long serialVersionUID = 342783165266555869L;

    private final Function<T, String> toStringMapper;

    public JListWithImages() {
        this(Object::toString);
    }

    public JListWithImages(Function<T, String> toStringMapper) {
        this.toStringMapper = toStringMapper;
        setCellRenderer(new ImageListCellRenderer());
        setModel(new DefaultListModel<>());
    }

    public void addItems(Image image, Collection<T> values) {
        values.forEach(value -> addItem(image, value));
    }

    public void addItem(Image image, T value) {
        ((DefaultListModel<LabelPanel<T>>) getModel()).addElement(new LabelPanel<>(image, value, toStringMapper, SwingConstants.LEFT));
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
            Image img = icon.getImage();
            Image newimg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(newimg);
        }
    }
}
