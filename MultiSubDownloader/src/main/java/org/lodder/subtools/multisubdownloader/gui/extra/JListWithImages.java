package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;

public class JListWithImages extends JList<JPanel> {

    private static final long serialVersionUID = 342783165266555869L;

    public JListWithImages() {
        setCellRenderer(new ImageListCellRenderer());
        setModel(new DefaultListModel<JPanel>());
    }

    public void addItem(SettingsExcludeType type, String text) {
        ImageIcon icon = new ImageIcon();
        Image img;

        switch (type) {
            case FOLDER -> {
                img = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/folder.png"));
                icon = resizeIcon(new ImageIcon(img), 20, 20);
            }
            case REGEX -> {
                img = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/regex.gif"));
                icon = resizeIcon(new ImageIcon(img), 20, 20);
            }
            case FILE -> {
            }
            default -> {
            }
        }

        icon.setDescription(type.toString());
        JLabel textLabel = new JLabel(text, icon, SwingConstants.LEFT);
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
        if (p == null) {
            return null;
        }
        return ((JLabel) p.getComponent(0)).getText();
    }

    public SettingsExcludeType getType(int index) {
        JPanel p = getModel().getElementAt(index);
        if (p == null) {
            return null;
        }
        ImageIcon i = (ImageIcon) ((JLabel) p.getComponent(0)).getIcon();
        return SettingsExcludeType.valueOf(i.getDescription());
    }

}
