package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import lombok.RequiredArgsConstructor;
import org.lodder.subtools.multisubdownloader.gui.extra.table.CustomTable;

@RequiredArgsConstructor
public class PopupListener extends MouseAdapter {

    private final JPopupMenu popupMenu;

    @Override
    public void mousePressed(MouseEvent e) {
        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    private synchronized void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()
                && e.getComponent() instanceof CustomTable customTable
                && customTable.getModel().getRowCount() > 0) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
