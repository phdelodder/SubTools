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
import manifold.ext.props.rt.api.val;

public class JListWithImages<T> extends JList<JListWithImages.LabelPanel<T>> {

    @Serial
    private static final long serialVersionUID = 342783165266555869L;

    private final Function<T, String> toStringMapper;
    private final boolean distinctValues;

    public JListWithImages(Function<T, String> toStringMapper=Object::toString, boolean distinctValues=true) {
        this.toStringMapper = toStringMapper;
        this.distinctValues = distinctValues;
        setCellRenderer(new ImageListCellRenderer());
        setModel(new DefaultListModel<>());
    }

    public void addItems(Image image, Collection<T> values) {
        values.forEach(value -> addItem(image, value));
    }

    public void addItem(Image image, T value) {
        if (!distinctValues || !contains(value)) {
            ((DefaultListModel<LabelPanel<T>>) getModel()).addElement(
                new LabelPanel<>(image, value, toStringMapper, SwingConstants.LEFT));
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

    public static class LabelPanel<T> extends JPanel {

        @Serial private static final long serialVersionUID = 1L;
        @val Label<T> label;

        LabelPanel(Image image, T object, Function<T, String> toStringMapper, int horizontalAlignment) {
            this.label = new Label<>(image, object, toStringMapper, horizontalAlignment);
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(label);
        }

        public T getObject() {
            return label.object;
        }

        public Image getImage() {
            return label.image;
        }
    }

    private static class Label<T> extends JLabel {
        @Serial private static final long serialVersionUID = 1L;
        @val T object;
        @val Image image;

        Label(Image image, T object, Function<T, String> toStringMapper, int horizontalAlignment) {
            super(toStringMapper.apply(object), getImageIcon(image), horizontalAlignment);
            this.object = object;
            this.image = image;
        }

        private static ImageIcon getImageIcon(Image image) {
            return image == null ? new ImageIcon() : resizeIcon(new ImageIcon(image), 20, 20);
        }

        private static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
            return new ImageIcon(icon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
        }
    }
}
