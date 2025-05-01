package org.lodder.subtools.multisubdownloader.gui;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

import net.miginfocom.swing.MigLayout;

public class Splash extends JWindow {

    @Serial
    private static final long serialVersionUID = -7795482367449509520L;
    private final JProgressBar progressBar;

    public Splash() {
        setBounds(100, 100, 501, 100);
        contentPane.setLayout(new MigLayout("", "[][475px,center][]", "[][40px:n]"));

        JLabel label = new JLabel(getText("Splash.starting"));
        contentPane.add(label, "cell 1 0 2 1,alignx left");

        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        contentPane.add(progressBar, "cell 1 1,grow");

        Rectangle r = getBounds();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - r.width) / 2;
        int y = (screen.height - r.height) / 2;
        setBounds(x, y, r.width, r.height);

    }

    public Splash showSplash() {
        setVisible(true);
        toFront();
        return this;
    }

    public void setProgressMsg(String msg) {
        progressBar.setString(msg);
    }

}
