package org.lodder.subtools.multisubdownloader.gui.panels;

import java.io.Serial;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcombobox.MyComboBox;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextcomponent.JTextComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.JTextFieldExtension;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JTextFieldExtension.class, JComponentExtension.class, JTextComponentExtension.class })
public class SearchTextInputPanel extends InputPanel {

    @Serial
    private static final long serialVersionUID = 7030171360517948253L;
    private MyComboBox<VideoSearchType> cbxVideoType;
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
        cbxVideoType = new MyComboBox<>(VideoSearchType.values())
                .withToMessageStringRenderer(VideoSearchType::getMsgCode);

        txtInputVideoName = new JTextField().withColumns(10);

        txtQualityVersion = new JTextField().withColumns(10);

        txtInputSeason = new JTextField().withColumns(10);

        txtInputEpisode = new JTextField().withColumns(10);
    }

    private void videoTypeChanged() {
        VideoSearchType videoTypeChoice = cbxVideoType.getSelectedItem();
        if (VideoSearchType.EPISODE == videoTypeChoice) {
            txtInputSeason.editable(true).withEnabled(true);
            txtInputEpisode.editable(true).withEnabled(true);
        } else {
            txtInputSeason.editable(false).withEnabled(false);
            txtInputEpisode.editable(false).withEnabled(false);
        }
        if (VideoSearchType.RELEASE == videoTypeChoice) {
            txtQualityVersion.editable(false).withEnabled(false);
            txtQualityVersion.editable(false).withEnabled(false);
        } else {
            txtQualityVersion.editable(true).withEnabled(true);
            txtQualityVersion.editable(true).withEnabled(true);
        }
    }

    public VideoSearchType getType() {
        return cbxVideoType.getSelectedItem();
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
