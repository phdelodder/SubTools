package org.lodder.subtools.multisubdownloader.gui.panels;

import static org.lodder.subtools.multisubdownloader.Messages.*;
import static org.lodder.subtools.sublibrary.model.VideoSearchType.*;

import javax.swing.*;
import java.io.Serial;

import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

public final class SearchTextInputPanel extends InputPanel {

    @Serial
    private static final long serialVersionUID = 7030171360517948253L;

    private JComboBox<VideoSearchType> cbxVideoType;
    private JTextField txtInputVideoName;
    private JTextField txtInputSeason;
    private JTextField txtInputEpisode;
    private JTextField txtQualityVersion;

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
        this.add(new JLabel(getText("MainWindow.QualityVersion")), "cell 1 1,alignx trailing");
        this.add(txtQualityVersion, "cell 2 1,growx");
        this.add(new JLabel(getText("App.Season")), "cell 3 1,alignx trailing");
        this.add(txtInputSeason, "cell 4 1,alignx left");
        this.add(new JLabel(getText("App.Episode")), "cell 5 1,alignx trailing");
        this.add(txtInputEpisode, "cell 6 1,growx");
        this.add(new JLabel(getText("MainWindow.SelectSubtitleLanguage")), "cell 1 2 3 1,alignx trailing");
        this.add(languageCbx, "cell 4 2 2 1,growx");
        this.add(searchButton, "cell 2 4 2 1");
    }

    private void setupListeners() {
        cbxVideoType.addItemListener(_ -> videoTypeChanged());
    }

    private void createComponents() {
        cbxVideoType = new JComboBox<>(values()).toStringRenderer(t -> getText(t.msgCode));
        txtInputVideoName = new JTextField().columns(10);
        txtQualityVersion = new JTextField().columns(10);
        txtInputSeason = new JTextField().columns(10);
        txtInputEpisode = new JTextField().columns(10);
    }

    private void videoTypeChanged() {
        VideoSearchType videoTypeChoice = cbxVideoType.getSelectedValue();
        txtInputSeason.editable(videoTypeChoice == EPISODE).enabled(videoTypeChoice == EPISODE);
        txtInputEpisode.editable(videoTypeChoice == EPISODE).enabled(videoTypeChoice == EPISODE);
        txtQualityVersion.editable(videoTypeChoice == RELEASE).enabled(videoTypeChoice == RELEASE);
    }

    public VideoSearchType getType() {
        return cbxVideoType.getSelectedValue();
    }

    public int getSeason() {
        int season;
        String strSeason = txtInputSeason.getText().trim();
        try {
            season = strSeason.isEmpty() ? 0 : Integer.parseInt(strSeason);
        } catch (NumberFormatException e) {
            season = 0;
        }
        txtInputSeason.setText(String.valueOf(season));

        return season;
    }

    public int getEpisode() {
        int episode;
        String strEpisode = txtInputEpisode.getText().trim();
        try {
            episode = strEpisode.isEmpty() ? 0 : Integer.parseInt(strEpisode);
        } catch (NumberFormatException e) {
            episode = 0;
        }
        txtInputEpisode.setText(String.valueOf(episode));

        return episode;
    }

    public String getQuality() {
        return txtQualityVersion.getText().trim();
    }

    public String getReleaseName() {
        return txtInputVideoName == null ? "" : txtInputVideoName.getText().trim();
    }

}
