package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import java.io.Serial;

public class MyComboBoxEditor extends DefaultCellEditor {
    /**
     * http://www.exampledepot.com/egs/javax.swing.table/ComboBox.html
     */
    @Serial
    private static final long serialVersionUID = 4350388923338945594L;

    public MyComboBoxEditor(String[] items) {
        super(new JComboBox<>(items));
    }
}
