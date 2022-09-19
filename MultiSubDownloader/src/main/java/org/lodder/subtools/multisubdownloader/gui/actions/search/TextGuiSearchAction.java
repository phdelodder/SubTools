package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.actions.ActionException;
import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchTextInputPanel;
import org.lodder.subtools.multisubdownloader.lib.SubtitleSelection;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

public class TextGuiSearchAction extends GuiSearchAction {

    private SubtitleSelection subtitleSelection;

    public TextGuiSearchAction(GUI mainWindow, Settings settings, SubtitleProviderStore subtitleProviderStore) {
        super();
        this.setGUI(mainWindow);
        this.setSettings(settings);
        this.setProviderStore(subtitleProviderStore);
    }

    public void setSubtitleSelection(SubtitleSelection subtitleSelection) {
        this.subtitleSelection = subtitleSelection;
    }

    @Override
    protected List<Release> createReleases() throws ActionException {
        String name = getInputPanel().getReleaseName();
        VideoSearchType type = getInputPanel().getType();

        VideoTableModel model = (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();
        model.clearTable();

        // TODO: Redefine what a "release" is.
        Release release;
        if (VideoSearchType.EPISODE.equals(type)) {
            release = TvRelease.builder()
                    .name(name)
                    .season(getInputPanel().getSeason())
                    .episode(getInputPanel().getEpisode())
                    .quality(getInputPanel().getQuality())
                    .build();
        } else if (VideoSearchType.MOVIE.equals(type)) {
            release = MovieRelease.builder()
                    .name(name)
                    .quality(getInputPanel().getQuality())
                    .build();
        } else {
            release = releaseFactory.createRelease(new File(name));
        }

        List<Release> releases = new ArrayList<>();
        if (release != null) {
            releases.add(release);
        }

        return releases;
    }

    @Override
    public void onFound(Release release, List<Subtitle> subtitles) {
        VideoTableModel model = (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();

        if (filtering != null) {
            subtitles = filtering.getFiltered(subtitles, release);
        }

        release.getMatchingSubs().addAll(subtitles);

        // use automatic selection to reduce the selection for the user
        if (subtitleSelection != null) {
            subtitles = subtitleSelection.getAutomaticSelection(subtitles);
        }

        for (Subtitle subtitle : subtitles) {
            model.addRow(subtitle);
        }

        /* Let GuiSearchAction also make some decisions */
        super.onFound(release, subtitles);
    }

    @Override
    protected void validate() throws SearchSetupException {
        if (getInputPanel().getReleaseName().isEmpty()) {
            throw new SearchSetupException("Geen Movie/Episode/Release opgegeven");
        }

        super.validate();
    }

    private SearchTextInputPanel getInputPanel() {
        return (SearchTextInputPanel) this.searchPanel.getInputPanel();
    }
}
