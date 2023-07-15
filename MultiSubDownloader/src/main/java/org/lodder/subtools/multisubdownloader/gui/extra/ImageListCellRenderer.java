package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageListCellRenderer extends JLabel implements ListCellRenderer<Object> {

    @Serial
    private static final long serialVersionUID = 2640608321583922789L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageListCellRenderer.class);

   /**
    * <a href="http://java.sun.com/javase/6/docs/api/javax/swing/ListCellRenderer.html">Source</a>:
    * <p>
    * Return a component that has been configured to display the specified value.
    * That component's paint method is then called to "render" the cell.
    * If it is necessary to compute the dimensions of a list because the list cells do not have a fixed size,
    * this method is called to generate a component on which getPreferredSize can be invoked.
    * <p>
    * jlist - the jlist we're painting
    * value - the value returned by list.getModel().getElementAt(index).
    * cellIndex - the cell index
    * isSelected - true if the specified cell is currently selected
    * cellHasFocus - true if the cell has focus
    */
   @Override
   public Component getListCellRendererComponent(JList<?> jlist, Object value, int cellIndex,
            boolean isSelected, boolean cellHasFocus) {
        if (value instanceof JPanel) {
            Component component = (Component) value;
            if (isSelected) {
                component.setBackground(jlist.getSelectionBackground());
                component.setForeground(jlist.getSelectionForeground());
            } else {
                component.setBackground(jlist.getBackground());
                component.setForeground(jlist.getForeground());
            }

            return component;
        } else {
            // TODO - I get one String here when the JList is first rendered; proper way to deal with this?
            LOGGER.error("Got something besides a JPanel [{}]", value.getClass().getCanonicalName());
            return new JLabel("???");
        }
    }
}
