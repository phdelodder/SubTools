package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.JButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.EpisodeLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.MovieLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.VideoLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.workers.TypedRenameWorker;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ JButtonExtension.class, AbstractButtonExtension.class, JComponentExtension.class })
public class RenameDialog extends MultiSubDialog implements PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;
    private final VideoLibraryPanel pnlLibrary;
    private JTextField txtRenameLocation;
    private final JCheckBox chkRecursive;
    private ProgressDialog progressDialog;

    /**
     * Create the dialog.
     */
    public RenameDialog(JFrame frame, final Settings settings, final VideoType videoType, final Manager manager,
            UserInteractionHandler userInteractionHandler) {
        super(frame, videoType + Messages.getString("RenameDialog.Rename"), false);
        setResizable(false);
        setBounds(100, 100, 650, 680);
        getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            JPanel panel = new JPanel();
            GridBagConstraints gbc_panel = new GridBagConstraints();
            gbc_panel.insets = new Insets(0, 0, 5, 0);
            gbc_panel.fill = GridBagConstraints.BOTH;
            gbc_panel.gridx = 0;
            gbc_panel.gridy = 0;
            contentPanel.add(panel, gbc_panel);
            GridBagLayout gbl_panel = new GridBagLayout();
            gbl_panel.columnWidths = new int[] { 208, 151, 151, 0 };
            gbl_panel.rowHeights = new int[] { 40, 0, 0 };
            gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
            gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
            panel.setLayout(gbl_panel);
            {
                JLabel lblFolderToRenameMove =
                        new JLabel(Messages.getString("RenameDialog.FolderForRename/Move"));
                GridBagConstraints gbc_lblFolderToRenameMove = new GridBagConstraints();
                gbc_lblFolderToRenameMove.fill = GridBagConstraints.BOTH;
                gbc_lblFolderToRenameMove.insets = new Insets(0, 0, 5, 5);
                gbc_lblFolderToRenameMove.gridx = 0;
                gbc_lblFolderToRenameMove.gridy = 0;
                panel.add(lblFolderToRenameMove, gbc_lblFolderToRenameMove);
            }
            {
                JButton btnBrowser = new JButton(Messages.getString("App.Browse"));
                btnBrowser.addActionListener(arg0 -> MemoryFolderChooser.getInstance()
                        .selectDirectory(getContentPane(), Messages.getString("RenameDialog.SelectFolderForRenameReplace"))
                        .map(Path::toAbsolutePath).map(Path::toString).ifPresent(txtRenameLocation::setText));
                {
                    txtRenameLocation = new JTextField();
                    GridBagConstraints gbc_txtRenameLocation = new GridBagConstraints();
                    gbc_txtRenameLocation.fill = GridBagConstraints.HORIZONTAL;
                    gbc_txtRenameLocation.insets = new Insets(0, 0, 5, 5);
                    gbc_txtRenameLocation.gridx = 1;
                    gbc_txtRenameLocation.gridy = 0;
                    panel.add(txtRenameLocation, gbc_txtRenameLocation);
                    txtRenameLocation.setColumns(15);
                }
                GridBagConstraints gbc_btnBrowser = new GridBagConstraints();
                gbc_btnBrowser.insets = new Insets(0, 0, 5, 0);
                gbc_btnBrowser.gridx = 2;
                gbc_btnBrowser.gridy = 0;
                panel.add(btnBrowser, gbc_btnBrowser);
            }
            {
                chkRecursive = new JCheckBox(Messages.getString("RenameDialog.RecursiveSearch"));
                GridBagConstraints gbc_chkRecursive = new GridBagConstraints();
                gbc_chkRecursive.insets = new Insets(0, 0, 0, 5);
                gbc_chkRecursive.gridx = 1;
                gbc_chkRecursive.gridy = 1;
                panel.add(chkRecursive, gbc_chkRecursive);
            }
        }
        {
            if (videoType == VideoType.EPISODE) {
                pnlLibrary = new EpisodeLibraryPanel(settings.getEpisodeLibrarySettings(), manager, true, userInteractionHandler);
            } else {
                pnlLibrary = new MovieLibraryPanel(settings.getMovieLibrarySettings(), manager, true, userInteractionHandler);
            }

            GridBagConstraints gbc_panel = new GridBagConstraints();
            gbc_panel.fill = GridBagConstraints.BOTH;
            gbc_panel.gridx = 0;
            gbc_panel.gridy = 1;
            contentPanel.add(pnlLibrary, gbc_panel);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                new JButton(Messages.getString("RenameDialog.Rename"))
                        .defaultButtonFor(getRootPane())
                        .withActionListener(() -> {
                            if (videoType == VideoType.EPISODE) {
                                rename(Path.of(txtRenameLocation.getText()), Path.of(txtRenameLocation.getText()),
                                        settings, pnlLibrary.getLibrarySettings(), manager, userInteractionHandler);
                            } else {
                                rename(Path.of(txtRenameLocation.getText()), Path.of(txtRenameLocation.getText()),
                                        new Settings(), pnlLibrary.getLibrarySettings(), manager, userInteractionHandler);
                            }
                            setVisible(false);
                        })
                        .withActionCommand("Rename")
                        .addTo(buttonPane);
            }
            {
                new JButton(Messages.getString("App.Cancel"))
                        .withActionListener(() -> setVisible(false))
                        .withActionCommand("Cancel")
                        .addTo(buttonPane);
            }
        }
    }

    protected void rename(Path dir, Path basedir, Settings settings, LibrarySettings librarySettings, Manager manager,
            UserInteractionHandler userInteractionHandler) {
        TypedRenameWorker renameWorker =
                new TypedRenameWorker(dir, librarySettings, VideoType.EPISODE,
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
}
