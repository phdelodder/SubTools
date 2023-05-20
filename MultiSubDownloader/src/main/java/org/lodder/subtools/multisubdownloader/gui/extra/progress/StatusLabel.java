package org.lodder.subtools.multisubdownloader.gui.extra.progress;

import javax.swing.JLabel;
import java.io.Serial;

public class StatusLabel extends JLabel implements Messenger {

    @Serial
    private static final long serialVersionUID = -1755598958478129349L;

    public StatusLabel(String string) {
        super(string);
    }

    @Override
    public void message(String message) {
        setText(message);
    }

}
