package org.lodder.subtools.multisubdownloader.gui.panels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

public class SearchTextInputPanel extends InputPanel {

  private static final long serialVersionUID = 7030171360517948253L;
  private   JComboBox<VideoSearchType> cbxVideoType;
  protected JTextField                 txtInputSeason;
  protected JTextField                 txtInputEpisode;
  protected JTextField                 txtQualityVersion;
  private   JTextField                 txtInputVideoName;

  public SearchTextInputPanel() {
    super();
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
    this.add(getLanguageCbx(), "cell 4 2 2 1,growx");
    this.add(getSearchButton(), "cell 2 4 2 1");
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

  public VideoSearchType getType() {
    return (VideoSearchType) cbxVideoType.getSelectedItem();
  }

  public String getSeason() {
    return txtInputSeason.getText().trim();
  }

  public String getEpisode() {
    return txtInputEpisode.getText().trim();
  }

  public String getQuality() {
    return txtQualityVersion.getText().trim();
  }

  public String getName() {
    return txtInputVideoName.getText().trim();
  }

}
