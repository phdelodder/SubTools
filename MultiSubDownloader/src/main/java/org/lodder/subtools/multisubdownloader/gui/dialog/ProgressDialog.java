package org.lodder.subtools.multisubdownloader.gui.dialog;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.Messenger;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;

public class ProgressDialog extends MultiSubDialog implements Messenger {

    @Serial
    private static final long serialVersionUID = -2320149791421648965L;
    @Getter
    private JProgressBar progressBar;
    private JLabel label;
    private final Cancelable worker;

    public ProgressDialog(JFrame frame, Cancelable sft) {
        super(frame, Messages.getString("ProgressDialog.Title"), false);
        worker = sft;
        StatusMessenger.instance.addListener(this);
        initialize_ui();
        setDialogLocation(frame);
        repaint();
    }

    public ProgressDialog(Cancelable sft) {
        super(Messages.getString("ProgressDialog.Title"), false);
        worker = sft;
        StatusMessenger.instance.addListener(this);
        initialize_ui();
        repaint();
    }

    private void initialize_ui() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                worker.cancel(true);
            }
        });
        setBounds(100, 100, 501, 151);
        getContentPane().setLayout(new MigLayout("", "[][475px,center][]", "[][40px:n][][]"));

        label = new JLabel("");
        getContentPane().add(label, "cell 1 0 2 1,alignx left");

        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        getContentPane().add(progressBar, "cell 1 1,grow");

        JButton btnStop = new JButton("Stop!");
        btnStop.addActionListener(arg0 -> worker.cancel(true));
        getContentPane().add(btnStop, "cell 1 2 1 2,alignx left");
    }

    public void setMessage(String message) {
        label.setText(message);
        repaint();
    }

    public String getMessage() {
        return label.getText();
    }

    @Override
    public void message(String message) {
        setMessage(message);
    }

    public void updateProgress(int progress) {
        if (progress == 0) {
            getProgressBar().setIndeterminate(true);
        } else {
            getProgressBar().setIndeterminate(false);
            getProgressBar().setValue(progress);
            getProgressBar().setString(Integer.toString(progress));
        }
    }
}
