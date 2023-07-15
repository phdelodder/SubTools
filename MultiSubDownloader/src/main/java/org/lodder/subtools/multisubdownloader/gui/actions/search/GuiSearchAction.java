package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.util.List;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.UserInteractionHandlerGUI;
import org.lodder.subtools.multisubdownloader.actions.SearchAction;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.InputPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchPanel;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.SubtitleFiltering;
import org.lodder.subtools.multisubdownloader.listeners.IndexingProgressListener;
import org.lodder.subtools.multisubdownloader.listeners.SearchProgressListener;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

@Getter(value = AccessLevel.PROTECTED)
public abstract class GuiSearchAction<P extends InputPanel> extends SearchAction {

    private final @NonNull GUI mainWindow;
    private final @NonNull SearchPanel<P> searchPanel;
    private final SubtitleFiltering filtering;
    private final @NonNull ReleaseFactory releaseFactory;
    private final IndexingProgressListener indexingProgressListener;
    private final SearchProgressListener searchProgressListener;
    private final UserInteractionHandlerGUI userInteractionHandler;

    public GuiSearchAction(Manager manager, Settings settings, SubtitleProviderStore subtitleProviderStore,
            GUI mainWindow, SearchPanel<P> searchPanel, ReleaseFactory releaseFactory) {
        super(manager, settings, subtitleProviderStore);
        this.mainWindow = mainWindow;
        this.searchPanel = searchPanel;
        this.filtering = new SubtitleFiltering(settings);
        this.releaseFactory = releaseFactory;
        /* Create ProgressListeners */
        /*
         * The progressDialogs were re-used after the completed()-call and thus not shown. A
         * reset()-method might get implemented. But for now the GuiSearchAction will get a reference to
         * GUI and creates the listeners.
         */
        this.indexingProgressListener = mainWindow.createFileIndexerProgressDialog(this);
        this.searchProgressListener = mainWindow.createSearchProgressDialog(this);
        this.userInteractionHandler = new UserInteractionHandlerGUI(settings, mainWindow);
    }

    protected P getInputPanel() {
        return this.getSearchPanel().getInputPanel();
    }

    @Override
    protected Language getLanguage() {
        return this.searchPanel.getInputPanel().getSelectedLanguage();
    }

    @Override
    public void onFound(Release release, List<Subtitle> subtitles) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        VideoTableModel model = (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();

        if (model.getRowCount() > 0) {
            searchPanel.getResultPanel().enableButtons();
        }

        if (this.getSearchManager().getProgress() == 100) {
            this.getSearchProgressListener().completed();
            searchPanel.getInputPanel().enableSearchButton();
        }
    }
}
