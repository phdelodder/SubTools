package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.actions.ActionException;
import org.lodder.subtools.multisubdownloader.actions.FileListAction;
import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchFileInputPanel;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class FileGuiSearchAction extends GuiSearchAction {

    private FileListAction filelistAction;

    public FileGuiSearchAction(GUI mainWindow, Settings settings, SubtitleProviderStore subtitleProviderStore) {
        super();
        this.setGUI(mainWindow);
        this.setSettings(settings);
        this.setProviderStore(subtitleProviderStore);
    }

    public void setFileListAction(FileListAction filelistAction) {
        this.filelistAction = filelistAction;
    }

    @Override
    public void onFound(Release release, List<Subtitle> subtitles) {
        VideoTableModel model = (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();

        if (filtering != null) {
            subtitles = filtering.getFiltered(subtitles, release);
        }
        subtitles.forEach(release::addMatchingSub);

        model.addRow(release);
        mainwindow.repaint();

        /* Let GuiSearchAction also make some decisions */
        super.onFound(release, subtitles);
    }

    @Override
    protected List<Release> createReleases() throws ActionException {
        SearchFileInputPanel inputPanel = getInputPanel();
        String filePath = inputPanel.getIncomingPath();
        Language language = inputPanel.getSelectedLanguage();
        boolean recursive = inputPanel.isRecursiveSelected();
        boolean overwriteExistingSubtitles = inputPanel.isForceOverwrite();

        VideoTableModel model = (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();
        model.clearTable();

        /* get a list of videofiles */
        List<File> files = getFiles(filePath, language, recursive, overwriteExistingSubtitles);

        /* create a list of releases from videofiles */
        return createReleases(files);
    }

    private List<Release> createReleases(List<File> files) throws ActionException {
        /* parse every videofile */
        List<Release> releases = new ArrayList<>();

        int total = files.size();
        int index = 0;
        int progress = 0;

        this.indexingProgressListener.progress(progress);

        for (File file : files) {
            index++;
            progress = (int) Math.floor((float) index / total * 100);

            /* Tell progressListener which file we are processing */
            this.indexingProgressListener.progress(file.getName());

            Release r = releaseFactory.createRelease(file);
            if (r != null) {
                releases.add(r);
            }

            /* Update progressListener */
            this.indexingProgressListener.progress(progress);
        }

        return releases;
    }

    private List<File> getFiles(String filePath, Language language, boolean recursive, boolean overwriteExistingSubtitles) {
        /* Get a list of selected directories */
        List<File> dirs = new ArrayList<>();
        if (!filePath.isEmpty()) {
            dirs.add(new File(filePath));
        } else {
            dirs.addAll(this.settings.getDefaultFolders());
        }

        /* Scan directories for videofiles */
        /* Tell Action where to send progressUpdates */
        this.filelistAction.setIndexingProgressListener(this.indexingProgressListener);

        /* Start the getFileListing Action */
        return dirs.stream()
                .flatMap(dir -> this.filelistAction.getFileListing(dir, recursive, language, overwriteExistingSubtitles).stream())
                .collect(Collectors.toList());
    }

    @Override
    protected void validate() throws SearchSetupException {
        if (this.filelistAction == null) {
            throw new SearchSetupException("Actions-object must be set.");
        }

        String path = getInputPanel().getIncomingPath();
        if ("".equals(path) && !this.settings.hasDefaultFolders()) {
            throw new SearchSetupException("Geen map geselecteerd");
        }

        super.validate();
    }

    private SearchFileInputPanel getInputPanel() {
        return (SearchFileInputPanel) this.searchPanel.getInputPanel();
    }

}
