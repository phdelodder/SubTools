package org.lodder.subtools.multisubdownloader.gui;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.multisubdownloader.Messages;

public class Splash extends JWindow {

    @Serial
    private static final long serialVersionUID = -7795482367449509520L;
    private JProgressBar progressBar;

    public Splash() {
        initialize_ui();
    }

    public void initialize_ui() {
        setBounds(100, 100, 501, 100);
        getContentPane().setLayout(new MigLayout("", "[][475px,center][]", "[][40px:n]"));

        JLabel label = new JLabel(Messages.getString("Splash.starting"));
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
