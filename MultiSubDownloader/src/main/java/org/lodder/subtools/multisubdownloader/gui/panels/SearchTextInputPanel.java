package org.lodder.subtools.multisubdownloader.gui.panels;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.lodder.subtools.sublibrary.model.VideoSearchType;

public class SearchTextInputPanel extends JPanel {

  private static final long serialVersionUID = 7030171360517948253L;
  private   JComboBox<VideoSearchType> cbxVideoType;
  protected JTextField                 txtInputSeason;
  protected JTextField                 txtInputEpisode;
  protected JTextField                 txtQualityVersion;
  private   JTextField                 txtInputVideoName;
  private   JComboBox<String>          cbxLanguageText;
  private   JButton                    btnSearchText;
  private final String[] languageSelection = new String[]{"Nederlands", "Engels"};

  public SearchTextInputPanel() {
    this.setLayout(new MigLayout("", "[][][][][][][][grow][]", "[][][][][]"));

    cbxVideoType = new JComboBox<VideoSearchType>();
    cbxVideoType.setModel(new DefaultComboBoxModel<VideoSearchType>(VideoSearchType.values()));
    cbxVideoType.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        VideoSearchType videoTypeChoice = (VideoSearchType) cbxVideoType.getSelectedItem();
        if (videoTypeChoice.equals(VideoSearchType.EPISODE)) {
          txtInputSeason.setEditable(true);
          txtInputSeason.setEnabled(true);
          txtInputEpisode.setEditable(true);
          txtInputEpisode.setEnabled(true);
        } else {
          txtInputSeason.setEditable(false);
          txtInputSeason.setEnabled(false);
          txtInputEpisode.setEditable(false);
          txtInputEpisode.setEnabled(false);
        }
        if (videoTypeChoice.equals(VideoSearchType.RELEASE)) {
          txtQualityVersion.setEditable(false);
          txtQualityVersion.setEnabled(false);
        } else {
          txtQualityVersion.setEditable(true);
          txtQualityVersion.setEnabled(true);
        }

      }
    });
    this.add(cbxVideoType, "cell 1 0,growx");

    txtInputVideoName = new JTextField();
    this.add(txtInputVideoName, "cell 2 0 5 1,growx");
    txtInputVideoName.setColumns(10);

    JLabel lblKwaliteitversie = new JLabel("Kwaliteit\\Versie");
    this.add(lblKwaliteitversie, "cell 1 1,alignx trailing");

    txtQualityVersion = new JTextField();
    this.add(txtQualityVersion, "cell 2 1,growx");
    txtQualityVersion.setColumns(10);

    JLabel lblSeizoen = new JLabel("Seizoen");
    this.add(lblSeizoen, "cell 3 1,alignx trailing");

    txtInputSeason = new JTextField();
    this.add(txtInputSeason, "cell 4 1,alignx left");
    txtInputSeason.setColumns(5);

    JLabel lblAflevering = new JLabel("Aflevering");
    this.add(lblAflevering, "cell 5 1,alignx trailing");

    txtInputEpisode = new JTextField();
    this.add(txtInputEpisode, "cell 6 1,growx");
    txtInputEpisode.setColumns(5);

    JLabel lblSelecteerDeGewenste1 = new JLabel("Selecteer de gewenste ondertitel taal");
    this.add(lblSelecteerDeGewenste1, "cell 1 2 3 1,alignx trailing");

    cbxLanguageText = new JComboBox<String>();
    cbxLanguageText.setModel(new DefaultComboBoxModel<String>(languageSelection));
    this.add(cbxLanguageText, "cell 4 2 2 1,growx");

    btnSearchText = new JButton("Zoeken naar ondertitels");
    this.add(btnSearchText, "cell 2 4 2 1");
  }

  public void setBtnSearchTextAction(ActionListener actionListener) {
    btnSearchText.addActionListener(actionListener);
  }

  public JComboBox<VideoSearchType> getCbxVideoType() {
    return cbxVideoType;
  }

  public JTextField getTxtInputSeason() {
    return txtInputSeason;
  }

  public JTextField getTxtInputEpisode() {
    return txtInputEpisode;
  }

  public JTextField getTxtQualityVersion() {
    return txtQualityVersion;
  }

  public JTextField getTxtInputVideoName() {
    return txtInputVideoName;
  }

  public JComboBox<String> getCbxLanguageText() {
    return cbxLanguageText;
  }

  public JButton getBtnSearchText() {
    return btnSearchText;
  }

  public String getLanguageCodeText() {
    if (cbxLanguageText.getSelectedItem().equals("Nederlands")) {
      return "nl";
    } else if (cbxLanguageText.getSelectedItem().equals("Engels")) {
      return "en";
    }
    return null;
  }

}
