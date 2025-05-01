package org.lodder.subtools.multisubdownloader.gui.actions.search;

import static manifold.ext.props.rt.api.PropOption.*;

import java.util.List;

import manifold.ext.props.rt.api.get;
import manifold.ext.props.rt.api.override;
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
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public abstract sealed class GuiSearchAction<P extends InputPanel> extends SearchAction
    permits FileGuiSearchAction, TextGuiSearchAction {

    @get(Protected) GUI mainWindow;
    @get(Protected) SearchPanel<P> searchPanel;
    @get(Protected) SubtitleFiltering filtering;
    @get(Protected) ReleaseFactory releaseFactory;
    @get(Protected) @override IndexingProgressListener indexingProgressListener;
    @get(Protected) @override SearchProgressListener searchProgressListener;
    @get(Protected) @override UserInteractionHandlerGUI userInteractionHandler;

    GuiSearchAction(Settings settings, SubtitleProviderStore subtitleProviderStore,
        GUI mainWindow, SearchPanel<P> searchPanel, ReleaseFactory releaseFactory) {
        super(settings, subtitleProviderStore);
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
        return this.searchPanel.inputPanel;
    }

    @Override
    protected Language getLanguage() {
        return this.searchPanel.inputPanel.selectedLanguage;
    }

    @Override
    public void onFound(Release release, List<Subtitle> subtitles) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        VideoTableModel model = (VideoTableModel) this.searchPanel.resultPanel.table.model;

        if (model.getRowCount() > 0) {
            searchPanel.resultPanel.enableButtons();
        }

        if (this.searchManager.progress == 100) {
            this.searchProgressListener.completed();
            searchPanel.inputPanel.enableSearchButton();
        }
    }

    public void reset() {
        searchProgressListener.reset();
    }
}
