package org.lodder.subtools.multisubdownloader.gui.extra;

import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;

import javax.swing.*;
import java.awt.*;

public class JListWithImages extends JList<JPanel> {

  /**
     *
     */
  private static final long serialVersionUID = 342783165266555869L;

  public JListWithImages() {
    setCellRenderer(new ImageListCellRenderer());
    setModel(new DefaultListModel<JPanel>());
  }

  public void addItem(SettingsExcludeType type, String text) {
    ImageIcon icon = new ImageIcon();
    if (type == SettingsExcludeType.FOLDER) {
      Image img =
          Toolkit.getDefaultToolkit().getImage(getClass().getResource("/folder.png"));
      icon = resizeIcon(new ImageIcon(img), 20, 20);
    } else if (type == SettingsExcludeType.REGEX) {
      Image img =
          Toolkit.getDefaultToolkit().getImage(getClass().getResource("/regex.gif"));
      icon = resizeIcon(new ImageIcon(img), 20, 20);
    }
    icon.setDescription(type.toString());
    JLabel textLabel = new JLabel(text, icon, JLabel.LEFT);
    JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    textPanel.add(textLabel);
    ((DefaultListModel<JPanel>) getModel()).addElement(textPanel);
  }

  private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
    Image img = icon.getImage();
    Image newimg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
    return new ImageIcon(newimg);
  }

  public String getDescription(int index) {
    JPanel p = getModel().getElementAt(index);
    return ((JLabel) p.getComponent(0)).getText();
  }

  public SettingsExcludeType getType(int index) {
    JPanel p = getModel().getElementAt(index);
    ImageIcon i = (ImageIcon) ((JLabel) p.getComponent(0)).getIcon();
    return SettingsExcludeType.valueOf(i.getDescription());
  }

}
