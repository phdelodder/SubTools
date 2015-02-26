package org.lodder.subtools.multisubdownloader.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.lodder.subtools.multisubdownloader.gui.extra.LogTextArea;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;

public class LoggingPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1578326761175927376L;
  private LogTextArea txtLogging;

  public LoggingPanel(){
    this.setLayout(new MigLayout("", "[698px,grow][]", "[][70px,grow]"));

    final JScrollPane scrollPane_1 = new JScrollPane();
    this.add(new JLabel("Logging"), "cell 0 0,alignx right,gaptop 5");
    this.add(new JSeparator(), "cell 0 0,growx,gaptop 5");

    final JComboBox<Level> cbxLogLevel = new JComboBox<Level>();
    cbxLogLevel.setModel(new DefaultComboBoxModel<Level>(Level.values()));
    cbxLogLevel.setSelectedItem(Logger.instance.getLogLevel());
    cbxLogLevel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        Logger.instance.setLogLevel((Level) cbxLogLevel.getSelectedItem());
      }
    });
    this.add(cbxLogLevel, "cell 1 0,alignx right");
    this.add(scrollPane_1, "cell 0 1 2 1,grow");

    txtLogging = new LogTextArea();
    Logger.instance.addListener(txtLogging);
    scrollPane_1.setViewportView(txtLogging);
    txtLogging.setEditable(false);
    txtLogging.setAutoScroll(true);
  }
  
  public void setLogText(String str1){
    this.txtLogging.setText(str1);
    repaint();
  }
  
}
