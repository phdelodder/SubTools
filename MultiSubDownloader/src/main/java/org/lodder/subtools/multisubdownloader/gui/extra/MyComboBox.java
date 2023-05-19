package org.lodder.subtools.multisubdownloader.gui.extra;

import java.io.Serial;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class MyComboBox<E> extends JComboBox<E> {

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
    }

    /**
     * Creates a <code>MyComboBox</code> that contains the elements
     * in the specified array. By default the first item in the array
     * (and therefore the data model) becomes selected.
     *
     * @param items an array of objects to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public MyComboBox(E[] items) {
        super(items);
    }

    /**
     * Creates a <code>MyComboBox</code> that contains the elements
     * in the specified Vector. By default the first item in the vector
     * (and therefore the data model) becomes selected.
     *
     * @param items an array of vectors to insert into the combo box
     * @see DefaultComboBoxModel
     */
    public MyComboBox(Vector<E> items) {
        super(items);
    }

    /**
     * Creates a <code>MyComboBox</code> with a default data model.
     * The default data model is an empty list of objects.
     * Use <code>addItem</code> to add items. By default the first item
     * in the data model becomes selected.
     *
     * @see DefaultComboBoxModel
     */
    public MyComboBox() {
        super();
    }

    @Override
    public E getSelectedItem() {
        return (E) super.getSelectedItem();
    }
}
