package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.actions.ActionException;
import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchTextInputPanel;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

public class TextGuiSearchAction extends GuiSearchAction<SearchTextInputPanel> {

    public interface TextGuiSearchActionBuilderSubtitleProviderStore {
        TextGuiSearchActionBuilderGUI subtitleProviderStore(SubtitleProviderStore subtitleProviderStore);
    }

    public interface TextGuiSearchActionBuilderGUI {
        TextGuiSearchActionBuilderSearchPanel mainwindow(GUI mainwindow);
    }

    public interface TextGuiSearchActionBuilderSearchPanel {
        TextGuiSearchActionBuilderReleaseFactory searchPanel(SearchPanel<SearchTextInputPanel> searchPanel);
    }

    public interface TextGuiSearchActionBuilderReleaseFactory {
        TextGuiSearchActionBuilderBuild releaseFactory(ReleaseFactory releaseFactory);
    }

    public interface TextGuiSearchActionBuilderBuild {
        TextGuiSearchAction build() throws SearchSetupException;
    }

    public static TextGuiSearchActionBuilderSubtitleProviderStore createWithSettings(Settings settings) {
        return new TextGuiSearchActionBuilder(settings);
    }

    @RequiredArgsConstructor
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class TextGuiSearchActionBuilder
            implements TextGuiSearchActionBuilderBuild, TextGuiSearchActionBuilderReleaseFactory,
            TextGuiSearchActionBuilderSearchPanel, TextGuiSearchActionBuilderGUI, TextGuiSearchActionBuilderSubtitleProviderStore {
        private final Settings settings;
        private SubtitleProviderStore subtitleProviderStore;
        private GUI mainwindow;
        private SearchPanel<SearchTextInputPanel> searchPanel;
        private ReleaseFactory releaseFactory;

        @Override
        public TextGuiSearchAction build() throws SearchSetupException {
            return new TextGuiSearchAction(settings, subtitleProviderStore, mainwindow, searchPanel, releaseFactory);
        }
    }

    private TextGuiSearchAction(Settings settings, SubtitleProviderStore subtitleProviderStore, GUI mainwindow,
            SearchPanel<SearchTextInputPanel> searchPanel, ReleaseFactory releaseFactory) throws SearchSetupException {
        super(settings, subtitleProviderStore, mainwindow, searchPanel, releaseFactory);
    }

    @Override
    protected void validate() throws SearchSetupException {
        if (getInputPanel().getReleaseName().isEmpty()) {
            throw new SearchSetupException(Messages.getString("App.NoReleaseEntered"));
        }
    }

    @Override
    protected List<Release> createReleases() throws ActionException {
        String name = getInputPanel().getReleaseName();
        VideoSearchType type = getInputPanel().getType();

        VideoTableModel model = (VideoTableModel) this.getSearchPanel().getResultPanel().getTable().getModel();
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
            release = getReleaseFactory().createRelease(new File(name), getUserInteractionHandler());
        }

        List<Release> releases = new ArrayList<>();
        if (release != null) {
            releases.add(release);
        }

        return releases;
    }

    @Override
    public void onFound(Release release, List<Subtitle> subtitles) {
        List<Subtitle> subtitlesFiltered = subtitles;
        VideoTableModel model = (VideoTableModel) this.getSearchPanel().getResultPanel().getTable().getModel();

        if (getFiltering() != null) {
            subtitlesFiltered = getFiltering().getFiltered(subtitlesFiltered, release);
        }
        subtitlesFiltered.forEach(release::addMatchingSub);

        // use automatic selection to reduce the selection for the user
        subtitlesFiltered = getUserInteractionHandler().getAutomaticSelection(subtitlesFiltered);

        subtitlesFiltered.forEach(model::addRow);

        /* Let GuiSearchAction also make some decisions */
        super.onFound(release, subtitlesFiltered);
    }
}
