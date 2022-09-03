package org.lodder.subtools.multisubdownloader.gui.panels;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.lodder.subtools.multisubdownloader.gui.extra.LogTextAppender;

import ch.qos.logback.classic.Level;

public class LoggingPanel extends JPanel {

    private static final long serialVersionUID = 1578326761175927376L;
    private final JTextArea txtLogging;
    private final ch.qos.logback.classic.Logger root =
            (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

    public LoggingPanel() {
        this.setLayout(new MigLayout("", "[698px,grow][]", "[][70px,grow]"));

        final JScrollPane scrollPane_1 = new JScrollPane();
        this.add(new JLabel("Logging"), "cell 0 0,alignx right,gaptop 5");
        this.add(new JSeparator(), "cell 0 0,growx,gaptop 5");

        final JComboBox<Level> cbxLogLevel = new JComboBox<>();
        Level[] logLevels = { Level.ALL, Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR };
        cbxLogLevel.setModel(new DefaultComboBoxModel<>(logLevels));
        cbxLogLevel.setSelectedItem(root.getLevel());
        cbxLogLevel.addActionListener(arg0 -> root.setLevel((Level) cbxLogLevel.getSelectedItem()));
        this.add(cbxLogLevel, "cell 1 0,alignx right");
        this.add(scrollPane_1, "cell 0 1 2 1,grow");

        txtLogging = new JTextArea();
        scrollPane_1.setViewportView(txtLogging);
        txtLogging.setEditable(false);
        txtLogging.setAutoscrolls(true);

        new LogTextAppender(txtLogging);
    }

    public void setLogText(String str1) {
        this.txtLogging.setText(str1);
        repaint();
    }

}
