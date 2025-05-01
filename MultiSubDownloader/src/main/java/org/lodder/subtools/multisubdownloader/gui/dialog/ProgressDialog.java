package org.lodder.subtools.multisubdownloader.gui.dialog;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.Messenger;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;

public class ProgressDialog extends MultiSubDialog implements Messenger {

    @Serial
    private static final long serialVersionUID = -2320149791421648965L;

    private JProgressBar progressBar;
    private JLabel label;

    public ProgressDialog(@Nullable JFrame frame=null, Cancelable sft) {
        super(frame, Messages.getText("ProgressDialog.Title"), false);
        StatusMessenger.instance.addListener(this);
        initializeUi(sft);
        if (frame != null) {
            setDialogLocation(frame);
        }
        repaint();
    }

    private void initializeUi(Cancelable worker) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                worker.cancel(true);
            }
        });
        setBounds(100, 100, 501, 151);

        contentPane
            .layout(new MigLayout("", "[][475px,center][]", "[][40px:n][][]"))
            .addComponent("cell 1 0 2 1,alignx left", label = new JLabel(""))
            .addComponent("cell 1 1,grow", progressBar = new JProgressBar(0, 100).indeterminate((true)))
            .addComponent("cell 1 2 1 2,alignx left", new JButton("Stop!")
                .actionListener(_ -> worker.cancel(true))
            );
    }

    public void setMessage(String message) {
        label.text = message;
        repaint();
    }

    public String getMessage() {
        return label.text;
    }

    @Override
    public void message(String message) {
        this.message = message;
    }

    public void updateProgress(int progress) {
        if (progress == 0) {
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setIndeterminate(false);
            progressBar.setValue(progress);
            progressBar.setString(Integer.toString(progress));
        }
    }
}
