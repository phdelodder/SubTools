package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MyComboBoxRenderer extends JComboBox<String> implements TableCellRenderer {

  /**
   * http://www.exampledepot.com/egs/javax.swing.table/ComboBox.html
   */
  private static final long serialVersionUID = 5172245903060639082L;


  public MyComboBoxRenderer(String[] items) {
    super(items);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }

    // Select the current value
    setSelectedItem(value);
    return this;
  }

}
