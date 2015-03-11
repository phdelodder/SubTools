package org.lodder.subtools.multisubdownloader.gui.extra;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;

import org.lodder.subtools.multisubdownloader.gui.extra.table.CustomTable;

public class PopupListener extends MouseAdapter {

  private JPopupMenu popupMenu;

  public PopupListener(JPopupMenu popupMenu) {
    this.popupMenu = popupMenu;
  }

  public void mousePressed(MouseEvent e) {
    showPopup(e);
  }

  public void mouseReleased(MouseEvent e) {
    showPopup(e);
  }

  private synchronized void showPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      final CustomTable t = (CustomTable) e.getComponent();
      if (t == null) return;
      
      final DefaultTableModel model = (DefaultTableModel) t.getModel();
      if (model.getRowCount() > 0) popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
  }
}
