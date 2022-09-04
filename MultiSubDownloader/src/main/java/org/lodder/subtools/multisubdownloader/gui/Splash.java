package org.lodder.subtools.multisubdownloader.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import net.miginfocom.swing.MigLayout;

public class Splash extends JWindow {

    private static final long serialVersionUID = -7795482367449509520L;
    private JLabel label;
    private JProgressBar progressBar;

    public Splash() {
        initialize_ui();
    }

    public void initialize_ui() {
        setBounds(100, 100, 501, 100);
        getContentPane().setLayout(new MigLayout("", "[][475px,center][]", "[][40px:n]"));

        label = new JLabel("MultiSubDownloader Starting ...");
        getContentPane().add(label, "cell 1 0 2 1,alignx left");

        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        getContentPane().add(progressBar, "cell 1 1,grow");

        Rectangle r = getBounds();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - r.width) / 2;
        int y = (screen.height - r.height) / 2;
        setBounds(x, y, r.width, r.height);

    }

    public void showSplash() {
        setVisible(true);
        toFront();
    }

    public void setProgressMsg(String msg) {
        progressBar.setString(msg);
    }

}
