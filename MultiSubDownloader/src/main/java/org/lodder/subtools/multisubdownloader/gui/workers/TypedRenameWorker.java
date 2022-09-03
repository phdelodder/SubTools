package org.lodder.subtools.multisubdownloader.gui.workers;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import org.lodder.subtools.multisubdownloader.actions.RenameAction;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.util.FilenameExtensionFilter;
import org.lodder.subtools.sublibrary.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypedRenameWorker extends SwingWorker<Void, String> implements Cancelable {

    private File dir;
    private VideoType videoType;
    private final FilenameExtensionFilter patterns;
    private boolean isRecursive;
    private ReleaseFactory releaseFactory;
    private RenameAction renameAction;

    private static final Logger LOGGER = LoggerFactory.getLogger(TypedRenameWorker.class);

    public TypedRenameWorker(File dir, LibrarySettings librarySettings, VideoType videoType,
            boolean isRecursive, Manager manager) {
        setParameters(dir, librarySettings, videoType, isRecursive, manager);
        patterns =
                new FilenameExtensionFilter(
                        StringUtils.join(VideoPatterns.EXTENSIONS, new String[] { "srt" }));
    }

    public void setParameters(File dir, LibrarySettings librarySettings, VideoType videoType,
            boolean isRecursive, Manager manager) {
        this.dir = dir;
        this.videoType = videoType;
        this.isRecursive = isRecursive;
        this.renameAction = new RenameAction(librarySettings, manager);
    }

    public void setReleaseFactory(ReleaseFactory releaseFactory) {
        this.releaseFactory = releaseFactory;
    }

    @Override
    protected Void doInBackground() throws Exception {
        rename(dir);
        return null;
    }

    private void rename(File dir) {
        File[] contents = dir.listFiles();
        if (contents == null) {
            return;
        }

        for (final File file : contents) {
            if (file.isFile() && !file.getName().contains("sample")
                    && patterns.accept(file.getAbsoluteFile(), file.getName())) {
                Release release;
                try {
                    release = releaseFactory.createRelease(file);
                    if (release != null) {
                        publish(release.getFilename());
                        if (release.getVideoType() == videoType) {
                            renameAction.rename(file, release);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Series Rename", e);
                }
            } else if (file.isDirectory() && isRecursive) {
                rename(file);
            }
        }
    }

    @Override
    protected void process(List<String> data) {
        for (String s : data) {
            StatusMessenger.instance.message("Bestand hernoemen: " + s);
        }
    }
}
