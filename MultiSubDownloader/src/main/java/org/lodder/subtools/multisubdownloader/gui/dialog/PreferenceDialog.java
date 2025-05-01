package org.lodder.subtools.multisubdownloader.gui.dialog;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.Serial;
import java.util.concurrent.atomic.AtomicInteger;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.multisubdownloader.framework.event.Event;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.EpisodeLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.GeneralPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.MovieLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.OptionsPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.PreferencePanelIntf;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.SerieProvidersPanel;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

public class PreferenceDialog extends MultiSubDialog {

    @Serial private static final long serialVersionUID = -4910124272966075979L;

    private final SettingsControl settingsCtrl;
    private final Emitter eventEmitter;
    private final GeneralPanel pnlGeneral;
    private final EpisodeLibraryPanel pnlEpisodeLibrary;
    private final MovieLibraryPanel pnlMovieLibrary;
    private final OptionsPanel pnlOptions;
    private final SerieProvidersPanel pnlSerieSources;

    public PreferenceDialog(GUI gui, final SettingsControl settingsCtrl, Emitter eventEmitter, Manager manager,
        UserInteractionHandler userInteractionHandler) {
        super(gui, getText("PreferenceDialog.Title"), true);
        this.settingsCtrl = settingsCtrl;
        this.eventEmitter = eventEmitter;

        setResizable(false);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setBounds(100, 100, 650, 700);

        AtomicInteger selectedIdx = new AtomicInteger();
        contentPane
            .layout(new BorderLayout())
            .addComponent(BorderLayout.CENTER, new JLabel()
                .border(new EmptyBorder(5, 5, 5, 5))
                .layout(new BorderLayout(0, 0))
                .addComponent(new JTabbedPane(SwingConstants.TOP)
                    .tabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT)
                    .changeListener(tabbedPane -> {
                        if (tabbedPane.selectedIndex != selectedIdx.get()) {
                            var sourcePanel = (PreferencePanelIntf) tabbedPane.getComponentAt(selectedIdx.get());
                            if (!sourcePanel.hasValidSettings()) {
                                tabbedPane.selectedIndex = selectedIdx.get();
                                JOptionPane.showMessageDialog(this, getText("PreferenceDialog.invalidInput"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            } else {
                                selectedIdx.set(tabbedPane.selectedIndex);
                            }
                        }
                    })
                    .withTab(getText("PreferenceDialog.TabGeneral"),
                        pnlGeneral = new GeneralPanel(gui, settingsCtrl))
                    .withTab(getText("PreferenceDialog.SerieLibrary"),
                        pnlEpisodeLibrary = new EpisodeLibraryPanel(settingsCtrl.settings.episodeLibrarySettings,
                            manager, false, userInteractionHandler))
                    .withTab(getText("PreferenceDialog.MovieLibrary"),
                        pnlMovieLibrary = new MovieLibraryPanel(settingsCtrl.settings.movieLibrarySettings,
                            manager, false, userInteractionHandler))
                    .withTab(getText("PreferenceDialog.Options"),
                        pnlOptions = new OptionsPanel(settingsCtrl))
                    .withTab(getText("PreferenceDialog.SerieSources"),
                        pnlSerieSources = new SerieProvidersPanel(settingsCtrl)))
            )
            .addComponent(BorderLayout.SOUTH, new JPanel()
                .layout(new FlowLayout(FlowLayout.RIGHT))
                .addComponent(new JButton(getText("App.OK"))
                    .defaultButtonFor(getRootPane())
                    .actionListener(this::testAndSaveValues)
                    .actionCommand(getText("App.OK")))
                .addComponent(new JButton(getText("App.Cancel"))
                    .actionListener(() -> setVisible(false))
                    .actionCommand("Cancel")));
    }

    private void testAndSaveValues() {
        if (pnlGeneral.hasValidSettings() && pnlEpisodeLibrary.hasValidSettings() &&
            pnlMovieLibrary.hasValidSettings() && pnlOptions.hasValidSettings() &&
            pnlSerieSources.hasValidSettings()) {
            pnlGeneral.savePreferenceSettings();
            pnlEpisodeLibrary.savePreferenceSettings();
            pnlMovieLibrary.savePreferenceSettings();
            pnlOptions.savePreferenceSettings();
            pnlSerieSources.savePreferenceSettings();
            setVisible(false);
            settingsCtrl.store();
            this.eventEmitter.fire(new Event("providers.settings.change"));
        } else {
            JOptionPane.showMessageDialog(this, getText("PreferenceDialog.invalidInput"), "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
