package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.nio.file.Path;
import java.util.List;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchTextInputPanel;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.control.VideoPatterns.VideoExtensions;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

public final class TextGuiSearchAction extends GuiSearchAction<SearchTextInputPanel> {

    public TextGuiSearchAction(Settings settings, SubtitleProviderStore subtitleProviderStore, GUI mainWindow,
        SearchPanel<SearchTextInputPanel> searchPanel, ReleaseFactory releaseFactory) {
        super(settings, subtitleProviderStore, mainWindow, searchPanel, releaseFactory);
    }

    @Override
    protected void validate() throws SearchSetupException {
        if (getInputPanel().getReleaseName().isEmpty()) {
            throw new SearchSetupException(Messages.getText("App.NoReleaseEntered"));
        }
    }

    @Override
    protected List<Release> createReleases() {
        String name = getInputPanel().getReleaseName();
        VideoSearchType type = getInputPanel().getType();

        VideoTableModel model = (VideoTableModel) this.searchPanel.resultPanel.getTable().getModel();
        model.clearTable();

        // TODO: Redefine what a "release" is.
        Release release = switch (type) {
            case EPISODE ->
                new TvRelease(name:name, season:inputPanel.season, episode:inputPanel.episode, quality:inputPanel.quality);
            case MOVIE -> new MovieRelease(name:name, quality:inputPanel.quality);
            default -> releaseFactory.createRelease(Path.of(
                    name + (VideoExtensions.values().stream().anyMatch(ext -> name.endsWith("." + ext)) ? "" : ".")),
                userInteractionHandler);
        };
        return release != null ? List.of(release) : List.of();
    }

    @Override
    public void onFound(Release release, List<Subtitle> subtitles) {
        VideoTableModel model = (VideoTableModel) this.searchPanel.resultPanel.getTable().getModel();

        List<Subtitle> subtitlesFiltered = filtering != null ?
            subtitles.stream().filter(subtitle -> filtering.useSubtitle(subtitle, release)).toList() : subtitles;
        subtitlesFiltered.forEach(release::addMatchingSub);

        // use automatic selection to reduce the selection for the user
        List<Subtitle> subtitlesFilteredAutomatic = userInteractionHandler.getAutomaticSelection(subtitlesFiltered);
        subtitlesFilteredAutomatic.forEach(model::addRow);

        /* Let GuiSearchAction also make some decisions */
        super.onFound(release, subtitlesFilteredAutomatic);
    }
}
