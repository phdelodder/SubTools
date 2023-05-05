package org.lodder.subtools.multisubdownloader.gui.panels;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.io.Serial;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.ToStringListCellRenderer;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

import net.miginfocom.swing.MigLayout;

public class SearchTextInputPanel extends InputPanel {

    @Serial
    private static final long serialVersionUID = 7030171360517948253L;
    private JComboBox<VideoSearchType> cbxVideoType;
    protected JTextField txtInputSeason;
    protected JTextField txtInputEpisode;
    protected JTextField txtQualityVersion;
    private JTextField txtInputVideoName;

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
        this.add(new JLabel(Messages.getString("MainWindow.QualityVersion")), "cell 1 1,alignx trailing");
        this.add(txtQualityVersion, "cell 2 1,growx");
        this.add(new JLabel(Messages.getString("App.Season")), "cell 3 1,alignx trailing");
        this.add(txtInputSeason, "cell 4 1,alignx left");
        this.add(new JLabel(Messages.getString("App.Episode")), "cell 5 1,alignx trailing");
        this.add(txtInputEpisode, "cell 6 1,growx");
        this.add(new JLabel(Messages.getString("MainWindow.SelectSubtitleLanguage")), "cell 1 2 3 1,alignx trailing");
        this.add(getLanguageCbx(), "cell 4 2 2 1,growx");
        this.add(getSearchButton(), "cell 2 4 2 1");
    }

    private void setupListeners() {
        cbxVideoType.addItemListener(arg0 -> videoTypeChanged());
    }

    private void createComponents() {
        cbxVideoType = new JComboBox<>();
        cbxVideoType.setModel(new DefaultComboBoxModel<>(VideoSearchType.values()));
        cbxVideoType.setRenderer(new ToStringListCellRenderer<>(cbxVideoType.getRenderer(),
                o -> Messages.getString(((VideoSearchType) o).getMsgCode())));

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
        if (VideoSearchType.EPISODE.equals(videoTypeChoice)) {
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
        if (VideoSearchType.RELEASE.equals(videoTypeChoice)) {
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
        if (txtInputVideoName == null) {
            return "";
        }
        return txtInputVideoName.getText().trim();
    }

}
