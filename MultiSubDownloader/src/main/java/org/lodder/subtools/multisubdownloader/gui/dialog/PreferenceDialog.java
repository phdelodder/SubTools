package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.io.Serial;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.multisubdownloader.framework.event.Event;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.JButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.container.ContainerExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.EpisodeLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.GeneralPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.MovieLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.OptionsPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.PreferencePanelIntf;
import org.lodder.subtools.multisubdownloader.gui.panels.preference.SerieProvidersPanel;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ JComponentExtension.class, ContainerExtension.class, AbstractButtonExtension.class, JButtonExtension.class })
public class PreferenceDialog extends MultiSubDialog {

    @Serial
    private static final long serialVersionUID = -4910124272966075979L;

    private final SettingsControl settingsCtrl;
    private final Emitter eventEmitter;

    private final GeneralPanel pnlGeneral;
    private final EpisodeLibraryPanel pnlEpisodeLibrary;
    private final MovieLibraryPanel pnlMovieLibrary;
    private final OptionsPanel pnlOptions;
    private final SerieProvidersPanel pnlSerieSources;

    public PreferenceDialog(GUI gui, final SettingsControl settingsCtrl, Emitter eventEmitter,
            Manager manager, UserInteractionHandler userInteractionHandler) {
        super(gui, Messages.getString("PreferenceDialog.Title"), true);
        this.settingsCtrl = settingsCtrl;
        this.eventEmitter = eventEmitter;

        setResizable(false);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setBounds(100, 100, 650, 700);
        getContentPane().setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel().addTo(getContentPane(), BorderLayout.CENTER);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
            AtomicInteger selectedIndex = new AtomicInteger();
            tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            tabbedPane.addChangeListener(l -> {
                if (tabbedPane.getSelectedIndex() != selectedIndex.get()) {
                    PreferencePanelIntf sourcePanel = (PreferencePanelIntf) tabbedPane.getComponentAt(selectedIndex.get());
                    if (!sourcePanel.hasValidSettings()) {
                        tabbedPane.setSelectedIndex(selectedIndex.get());
                        JOptionPane.showMessageDialog(this, Messages.getString("PreferenceDialog.invalidInput"), "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        selectedIndex.set(tabbedPane.getSelectedIndex());
                    }
                }
            });
            contentPanel.add(tabbedPane);

            this.pnlGeneral = new GeneralPanel(gui, settingsCtrl);
            tabbedPane.addTab(Messages.getString("PreferenceDialog.TabGeneral"), null, pnlGeneral, null);

            this.pnlEpisodeLibrary =
                    new EpisodeLibraryPanel(settingsCtrl.getSettings().getEpisodeLibrarySettings(), manager, false, userInteractionHandler);
            tabbedPane.addTab(Messages.getString("PreferenceDialog.SerieLibrary"), null, pnlEpisodeLibrary, null);

            this.pnlMovieLibrary =
                    new MovieLibraryPanel(settingsCtrl.getSettings().getMovieLibrarySettings(), manager, false, userInteractionHandler);
            tabbedPane.addTab(Messages.getString("PreferenceDialog.MovieLibrary"), null, pnlMovieLibrary, null);

            this.pnlOptions = new OptionsPanel(settingsCtrl);
            tabbedPane.addTab(Messages.getString("PreferenceDialog.Options"), null, pnlOptions, null);

            this.pnlSerieSources = new SerieProvidersPanel(settingsCtrl);
            tabbedPane.addTab(Messages.getString("PreferenceDialog.SerieSources"), null, pnlSerieSources, null);
        }

        {
            new JPanel().layout(new FlowLayout(FlowLayout.RIGHT)).addTo(getContentPane(), BorderLayout.SOUTH)
                    .addComponent(
                            new JButton(Messages.getString("App.OK"))
                                    .defaultButtonFor(getRootPane())
                                    .withActionListener(this::testAndSaveValues)
                                    .actionCommand(Messages.getString("App.OK")))
                    .addComponent(
                            new JButton(Messages.getString("App.Cancel"))
                                    .withActionListener(() -> setVisible(false))
                                    .actionCommand("Cancel"));
        }
    }

    private void testAndSaveValues() {
        if (pnlGeneral.hasValidSettings() &&
                pnlEpisodeLibrary.hasValidSettings() &&
                pnlMovieLibrary.hasValidSettings() &&
                pnlOptions.hasValidSettings() &&
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
            JOptionPane.showMessageDialog(this, Messages.getString("PreferenceDialog.invalidInput"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
