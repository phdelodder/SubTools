package org.lodder.subtools.multisubdownloader.gui.panels;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import java.io.Serial;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.multisubdownloader.gui.extra.LogTextAppender;

public class LoggingPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = 1578326761175927376L;

    private final JTextArea txtLogging;
    private final Logger ROOT = (Logger) org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    public LoggingPanel() {
        this.setLayout(new MigLayout("", "[698px,grow][]", "[][70px,grow]"));

        JScrollPane scrollPane = new JScrollPane();
        this.add(new JLabel(getText("App.Logging")), "cell 0 0,alignx right,gaptop 5");
        this.add(new JSeparator(), "cell 0 0,growx,gaptop 5");

        Level[] logLevels = { Level.ALL, Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR };
        JComboBox<Level> cbxLogLevel = new JComboBox<>(logLevels)
            .selectedValue(ROOT.getLevel())
            .selectedItemConsumer(ROOT::setLevel);
        this.add(cbxLogLevel, "cell 1 0,alignx right");
        this.add(scrollPane, "cell 0 1 2 1,grow");

        txtLogging = new JTextArea().autoScrolls(true).editable(false);
        scrollPane.setViewportView(txtLogging);

        new LogTextAppender(txtLogging);
    }

    public void setLogText(String str1) {
        this.txtLogging.setText(str1);
        repaint();
    }

}
