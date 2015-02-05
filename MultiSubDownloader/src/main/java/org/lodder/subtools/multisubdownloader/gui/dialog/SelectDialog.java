package org.lodder.subtools.multisubdownloader.gui.dialog;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.lib.Actions;
import org.lodder.subtools.sublibrary.model.Subtitle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SelectDialog extends MultiSubDialog {

  public enum SelectionType {
    OK(1), ALL(999999), CANCEL(-1);

    private int code;

    private SelectionType(int c) {
      code = c;
    }

    public int getSelectionCode() {
      return code;
    }
  }

  /**
     *
     */
  private static final long serialVersionUID = -4092909537478305235L;
  private final JPanel contentPanel = new JPanel();
  private JPanel pnlSelect;
  private SelectionType answer = SelectionType.CANCEL;
  private List<Subtitle> lSubs;
  private String filename;


  /**
   * Create the dialog.
   */
  public SelectDialog(JFrame frame, List<Subtitle> lSubs, String filename) {
    super(frame, Messages.getString("SelectDialog.SelectCorrectSubtitle"), true);
    this.lSubs = lSubs;
    this.filename = filename;
    initialize();
    loadSelection();
    pack();
    setVisible(true);
  }

  private void loadSelection() {
    ButtonGroup group = new ButtonGroup();
    for (Subtitle subtitle : lSubs) {
      JRadioButton option = new JRadioButton(Actions.buildDisplayLine(subtitle));
      option.setName(Messages.getString("SelectDialog.Option"));
      group.add(option);
      pnlSelect.add(option);
    }
  }

  private void initialize() {
    setBounds(100, 100, 500, 150);
    setResizable(false);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("", "[grow]", "[][::300px,grow]"));
    {
      JLabel lblNewLabel =
          new JLabel(Messages.getString("SelectDialog.SelectCorrectSubtitleThisRelease") + filename);
      contentPanel.add(lblNewLabel, "cell 0 0");
    }
    {
      JScrollPane scrollPane = new JScrollPane();
      contentPanel.add(scrollPane, "cell 0 1,grow");
      {
        pnlSelect = new JPanel();
        scrollPane.setViewportView(pnlSelect);
        pnlSelect.setLayout(new BoxLayout(pnlSelect, BoxLayout.PAGE_AXIS));
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton(Messages.getString("SelectDialog.OK"));
        okButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            answer = SelectionType.OK;
            setVisible(false);
          }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton allButton = new JButton(Messages.getString("SelectDialog.Everything"));
        allButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            answer = SelectionType.ALL;
            setVisible(false);
          }
        });
        allButton.setActionCommand("Alles");
        if (lSubs.size() == 1) allButton.setEnabled(false);
        buttonPane.add(allButton);
      }
      {
        JButton cancelButton = new JButton(Messages.getString("SelectDialog.Cancel"));
        cancelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            answer = SelectionType.CANCEL;
            setVisible(false);
          }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }
  }

  public int getSelection() {
    if (answer == SelectionType.OK) {
      for (int i = 0; i < pnlSelect.getComponentCount(); i++) {
        if (pnlSelect.getComponent(i) instanceof JRadioButton) {
          JRadioButton option = (JRadioButton) pnlSelect.getComponent(i);
          if (option.isSelected()) return i;
        }
      }
    }
    return -1;
  }

  public SelectionType getAnswer() {
    return answer;
  }

}
