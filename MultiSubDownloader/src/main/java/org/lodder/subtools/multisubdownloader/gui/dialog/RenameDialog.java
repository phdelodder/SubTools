package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.actions.RenameAction;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.JButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.container.ContainerExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.JTextFieldExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldPath;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.EpisodeLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.MovieLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.VideoLibraryPanel;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.FileUtils;
import org.lodder.subtools.sublibrary.util.StreamExtension;

import com.google.common.collect.Streams;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JTextFieldExtension.class, ContainerExtension.class, JButtonExtension.class, AbstractButtonExtension.class,
        JComponentExtension.class })
public class RenameDialog extends MultiSubDialog implements PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;
    private final VideoLibraryPanel pnlLibrary;
    private final MyTextFieldPath txtFolder;
    private final JCheckBox chkRecursive;
    private ProgressDialog progressDialog;

    public RenameDialog(JFrame frame, Settings settings, VideoType videoType, String title, Manager manager,
            UserInteractionHandler userInteractionHandler) {
        super(frame, title, false);
        setResizable(false);
        setBounds(100, 100, 650, 680);
        getContentPane().setLayout(new MigLayout("fill, nogrid", "[]", "[][]20:push[]"));
        TitlePanel.title(Messages.getString("PreferenceDialog.Settings"))
                .padding(0).paddingLeft(20).fillContents(true).addTo(getContentPane(), "span, grow, wrap")
                .addComponent("shrink", new JLabel(Messages.getString("PreferenceDialog.Location")))
                .addComponent("grow", this.txtFolder = MyTextFieldPath.builder().requireValue().build().withColumns(20))
                .addComponent("shrink, wrap", new JButton(Messages.getString("App.Browse"))
                        .withActionListener(() -> MemoryFolderChooser.getInstance()
                                .selectDirectory(getContentPane(), Messages.getString("PreferenceDialog.SelectFolderForRenameReplace"))
                                .ifPresent(txtFolder::setObject)))
                .addComponent("wrap", this.chkRecursive = new JCheckBox(Messages.getString("RenameDialog.RecursiveSearch")));

        if (videoType == VideoType.EPISODE) {
            pnlLibrary = new EpisodeLibraryPanel(settings.getEpisodeLibrarySettings(), manager, true, userInteractionHandler)
                    .addTo(getContentPane(), "grow");
        } else {
            pnlLibrary = new MovieLibraryPanel(settings.getMovieLibrarySettings(), manager, true, userInteractionHandler)
                    .addTo(getContentPane(), "grow");
        }

        new JPanel().layout(new FlowLayout(FlowLayout.RIGHT)).addTo(getContentPane(), BorderLayout.SOUTH)
                .addComponent(
                        new JButton(Messages.getString("RenameDialog.Rename"))
                                .defaultButtonFor(getRootPane())
                                .withActionListener(() -> rename(videoType, settings, manager, userInteractionHandler))
                                .withActionCommand("Rename"))
                .addComponent(
                        new JButton(Messages.getString("App.Cancel"))
                                .withActionListener(() -> setVisible(false))
                                .actionCommand("Cancel"));

    }

    private boolean hasValidSettings() {
        return pnlLibrary.hasValidSettings() && txtFolder.hasValidValue();
    }

    private void rename(VideoType videoType, Settings settings, Manager manager,
            UserInteractionHandler userInteractionHandler) {

        if (!hasValidSettings()) {
            JOptionPane.showMessageDialog(this, Messages.getString("PreferenceDialog.invalidInput"), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        setVisible(false);
        pnlLibrary.savePreferenceSettings();
        TypedRenameWorker renameWorker = new TypedRenameWorker(txtFolder.getObject(), pnlLibrary.getLibrarySettings(), videoType,
                this.chkRecursive.isSelected(), manager, userInteractionHandler);
        renameWorker.addPropertyChangeListener(this);
        renameWorker.setReleaseFactory(new ReleaseFactory(settings, manager));
        progressDialog = new ProgressDialog(renameWorker);
        progressDialog.setVisible(true);
        renameWorker.execute();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getSource() instanceof TypedRenameWorker renameWorker) {
            if (renameWorker.isDone()) {
                progressDialog.setVisible(false);
            } else {
                final int progress = renameWorker.getProgress();
                progressDialog.updateProgress(progress);
                StatusMessenger.instance.message(Messages.getString("RenameDialog.StatusRename"));
            }
        }
    }

    @ExtensionMethod({ FileUtils.class, Files.class, StreamExtension.class })
    private static class TypedRenameWorker extends SwingWorker<Void, String> implements Cancelable {

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
                if (file.isRegularFile()) {
                    if (!file.fileNameContainsIgnoreCase("sample") && extensions.contains(file.getExtension())) {
                        Release release = releaseFactory.createRelease(file, userInteractionHandler);
                        if (release != null) {
                            publish(release.getFileName());
                            if (release.getVideoType() == videoType) {
                                renameAction.rename(file, release);
                            }
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

}
