package org.lodder.subtools.multisubdownloader.gui.workers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.SwingWorker;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.actions.RenameAction;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.FileUtils;
import org.lodder.subtools.sublibrary.util.StreamExtension;

import com.google.common.collect.Streams;

import lombok.Setter;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ FileUtils.class, Files.class, StreamExtension.class })
public class TypedRenameWorker extends SwingWorker<Void, String> implements Cancelable {

    private final UserInteractionHandler userInteractionHandler;
    private final Path dir;
    private final VideoType videoType;
    private final Set<String> extensions;
    private final boolean isRecursive;
    @Setter
    private ReleaseFactory releaseFactory;
    private final RenameAction renameAction;

    public TypedRenameWorker(Path dir, LibrarySettings librarySettings, VideoType videoType,
            boolean isRecursive, Manager manager, UserInteractionHandler userInteractionHandler) {
        this.userInteractionHandler = userInteractionHandler;
        this.extensions = Streams.concat(VideoPatterns.EXTENSIONS.stream(), Stream.of("srt")).collect(Collectors.toUnmodifiableSet());
        this.dir = dir;
        this.videoType = videoType;
        this.isRecursive = isRecursive;
        this.renameAction = new RenameAction(librarySettings, manager, userInteractionHandler);
    }

    @Override
    protected Void doInBackground() throws IOException {
        rename(dir);
        return null;
    }

    private void rename(Path dir) throws IOException {
        dir.list().asThrowingStream(IOException.class).forEach(file -> {
            if (file.isRegularFile() && !file.fileNameContainsIgnoreCase("sample") && extensions.contains(file.getExtension())) {
                Release release = releaseFactory.createRelease(file, userInteractionHandler);
                if (release != null) {
                    publish(release.getFileName());
                    if (release.getVideoType() == videoType) {
                        renameAction.rename(file, release);
                    }
                }
            } else if (isRecursive && file.isDirectory()) {
                rename(file);
            }
        });
    }

    @Override
    protected void process(List<String> data) {
        data.forEach(s -> StatusMessenger.instance.message(Messages.getString("MainWindow.RenamingFile", s)));
    }
}
