package org.lodder.subtools.multisubdownloader.gui.jcomponent.jcombobox;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.Serial;
import java.util.Collection;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;
import org.lodder.subtools.multisubdownloader.gui.ToStringListCellRenderer;

public class MyComboBox<E> extends JComboBox<E> {

    private static final Border ERROR_BORDER = new LineBorder(Color.RED, 1);
    private Border defaultBorder;
    private Predicate<E> selectedValueVerifier;

    @Serial
    private static final long serialVersionUID = -8449456978689044914L;

    /**
     * Creates a <code>MyComboBox</code> that takes its items from an
     * existing <code>ComboBoxModel</code>. Since the
     * <code>ComboBoxModel</code> is provided, a combo box created using
     * this constructor does not create a default combo box model and
     * may impact how the insert, remove and add methods behave.
     *
     * @param aModel the <code>ComboBoxModel</code> that provides the
     *        displayed list of items
     * @see DefaultComboBoxModel
     */
    public MyComboBox(ComboBoxModel<E> aModel) {
        super(aModel);
        this.defaultBorder = getBorder();
    }

    /**
     * Creates a <code>MyComboBox</code> that contains the elements
     * in the specified array. By default, the first item in the array
     * (and therefore the data model) becomes selected.
     *
     * @param items an array of objects to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public MyComboBox(E[] items) {
        super(items);
        this.defaultBorder = getBorder();
    }

    /**
     * Creates a <code>MyComboBox</code> that contains the elements
     * in the specified collection. By default, the first item in the
     * collection (and therefore the data model) becomes selected.
     *
     * @param items a collection of objects to insert into the combo box
     * @param elementType the type of elements in the list
     * @see DefaultComboBoxModel
     */
    public MyComboBox(Collection<E> items, Class<E> elementType) {
        super(Iterables.toArray(items, elementType));
        this.defaultBorder = getBorder();
    }

    /**
     * Creates a <code>MyComboBox</code> that contains the elements
     * in the specified Vector. By default, the first item in the vector
     * (and therefore the data model) becomes selected.
     *
     * @param items an array of vectors to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public MyComboBox(Vector<E> items) {
        super(items);
        this.defaultBorder = getBorder();
    }

    /**
     * Creates a <code>MyComboBox</code> with a default data model.
     * The default data model is an empty list of objects.
     * Use <code>addItem</code> to add items. By default, the first item
     * in the data model becomes selected.
     *
     * @see DefaultComboBoxModel
     */
    public MyComboBox() {
        super();
        this.defaultBorder = getBorder();
    }

    public static <E> MyComboBox<E> ofValues(E... values) {
        return new MyComboBox<>(values);
    }

    public MyComboBox<E> withModel(ComboBoxModel<E> model) {
        setModel(model);
        return this;
    }

    public MyComboBox<E> withRenderer(ListCellRenderer<? super E> renderer) {
        setRenderer(renderer);
        return this;
    }

    public MyComboBox<E> withToStringRenderer(Function<E, String> toStringRenderer) {
        return withRenderer(ToStringListCellRenderer.of(getRenderer(), toStringRenderer));
    }

    public MyComboBox<E> withToMessageStringRenderer(Function<E, String> toStringRenderer) {
        return withRenderer(ToStringListCellRenderer.ofMessage(getRenderer(), toStringRenderer));
    }

    public MyComboBox<E> withItemListener(ItemListener itemListener) {
        this.addItemListener(itemListener);
        return this;
    }

    public MyComboBox<E> withItemListener(Runnable itemListener) {
        return withItemListener(arg -> itemListener.run());
    }

    public MyComboBox<E> withSelectedItem(E item) {
        setSelectedItem(item);
        return this;
    }

    public MyComboBox<E> withActionListener(ActionListener actionListener) {
        addActionListener(actionListener);
        return this;
    }

    @SuppressWarnings("unchecked")
    public MyComboBox<E> withEventConsumer(Consumer<MyComboBox<E>> actionListener) {
        addActionListener(event -> actionListener.accept((MyComboBox<E>) (event.getSource())));
        return this;
    }

    @SuppressWarnings("unchecked")
    public MyComboBox<E> withSelectedItemConsumer(Consumer<E> actionListener) {
        addActionListener(event -> actionListener.accept(((MyComboBox<E>) (event.getSource())).getSelectedItem()));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getSelectedItem() {
        return (E) super.getSelectedItem();
    }

    public MyComboBox<E> withSelectedValueVerifier(Predicate<E> valueVerifier) {
        this.selectedValueVerifier = valueVerifier;
        return this;
    }

    @Override
    public void setBorder(Border border) {
        super.setBorder(border);
        defaultBorder = border;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        refreshState();
    }

    public void refreshState() {
        if (!isEnabled()) {
            super.setBorder(defaultBorder);
        } else if (selectedValueVerifier != null && !selectedValueVerifier.test(getSelectedItem())) {
            super.setBorder(ERROR_BORDER);
        }
    }
}
