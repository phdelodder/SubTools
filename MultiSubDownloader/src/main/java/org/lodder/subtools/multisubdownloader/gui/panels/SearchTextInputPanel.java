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

    createComponents();

    setupListeners();

    addComponentsToPanel();
  }

  private void addComponentsToPanel() {
    this.add(cbxVideoType, "cell 1 0,growx");
    this.add(txtInputVideoName, "cell 2 0 5 1,growx");
    this.add(new JLabel("Kwaliteit\\Versie"), "cell 1 1,alignx trailing");
    this.add(txtQualityVersion, "cell 2 1,growx");
    this.add(new JLabel("Seizoen"), "cell 3 1,alignx trailing");
    this.add(txtInputSeason, "cell 4 1,alignx left");
    this.add(new JLabel("Aflevering"), "cell 5 1,alignx trailing");
    this.add(txtInputEpisode, "cell 6 1,growx");
    this.add(new JLabel("Selecteer de gewenste ondertitel taal"), "cell 1 2 3 1,alignx trailing");
    this.add(cbxLanguageText, "cell 4 2 2 1,growx");
    this.add(btnSearchText, "cell 2 4 2 1");
  }

  private void setupListeners() {
    cbxVideoType.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        videoTypeChanged();
      }
    });
  }

  private void createComponents() {
    cbxVideoType = new JComboBox<VideoSearchType>();
    cbxVideoType.setModel(new DefaultComboBoxModel<VideoSearchType>(VideoSearchType.values()));

    txtInputVideoName = new JTextField();
    txtInputVideoName.setColumns(10);

    txtQualityVersion = new JTextField();
    txtQualityVersion.setColumns(10);

    txtInputSeason = new JTextField();
    txtInputSeason.setColumns(5);

    txtInputEpisode = new JTextField();
    txtInputEpisode.setColumns(5);

    cbxLanguageText = new JComboBox<String>();
    cbxLanguageText.setModel(new DefaultComboBoxModel<String>(languageSelection));

    btnSearchText = new JButton("Zoeken naar ondertitels");
  }

  private void videoTypeChanged() {
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
