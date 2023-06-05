package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.io.Serial;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.JButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.component.ComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.container.ContainerExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.JTextFieldExtension;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.structure.FolderStructureTag;
import org.lodder.subtools.multisubdownloader.settings.model.structure.MovieStructureTag;
import org.lodder.subtools.multisubdownloader.settings.model.structure.SerieStructureTag;
import org.lodder.subtools.multisubdownloader.settings.model.structure.StructureTag;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JButtonExtension.class, AbstractButtonExtension.class, JComponentExtension.class, ContainerExtension.class,
        ComponentExtension.class, JTextFieldExtension.class })
public class StructureBuilderDialog extends MultiSubDialog implements DocumentListener {

    @Serial
    private static final long serialVersionUID = -5174968778375028124L;
    private final VideoType videoType;
    private final StructureType structureType;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;
    private final Function<String, LibraryBuilder> libraryBuilder;
    private JTextField txtStructure;
    private JLabel lblPreview;
    private TvRelease tvRelease;
    private MovieRelease movieRelease;
    private String oldStructure;
    private JPanel tagPanel;

    public enum StructureType {
        FILE, FOLDER
    }

    public StructureBuilderDialog(JFrame frame, String title, boolean modal, VideoType videoType,
            StructureType structureType, Manager manager, UserInteractionHandler userInteractionHandler,
            Function<String, LibraryBuilder> libraryBuilder) {
        super(frame, title, modal);
        this.videoType = videoType;
        this.structureType = structureType;
        this.manager = manager;
        this.userInteractionHandler = userInteractionHandler;
        this.libraryBuilder = libraryBuilder;
        initializeUi();
        generateVideoFiles();
    }

    private void initializeUi() {
        setBounds(100, 100, 600, 300);
        setMinimumSize(new Dimension(600, 300));
        Container panel = getContentPane().layout(new MigLayout("insets 10, nogrid"));

        new JLabel(Messages.getString("StructureBuilderDialog.AvailableTagsClickToAdd")).addTo(panel, "wrap");

        this.tagPanel = new JPanel(new MigLayout("flowy, wrap 5", "[150px][150px][150px]")).addTo(panel, "grow, wrap");
        {
            if (videoType == VideoType.EPISODE) {
                // add tv show tags
                buildLabelTable(SerieStructureTag.values());
            } else if (videoType == VideoType.MOVIE) {
                // add movie tags
                buildLabelTable(MovieStructureTag.values());
            }
            if (structureType == StructureType.FOLDER) {
                buildLabelTable(FolderStructureTag.values());
            }
        }

        new JLabel(Messages.getString("StructureBuilderDialog.Structure")).addTo(panel);
        this.txtStructure = new JTextField().withColumns(100).addTo(panel, "span, wrap");
        this.txtStructure.getDocument().addDocumentListener(this);

        new JLabel(Messages.getString("StructureBuilderDialog.Preview")).addTo(panel);
        this.lblPreview = new JLabel("").addTo(panel);

        new JPanel(new FlowLayout(FlowLayout.RIGHT)).addTo(panel, BorderLayout.SOUTH)
                .addComponent(
                        new JButton(Messages.getString("App.OK"))
                                .defaultButtonFor(getRootPane())
                                .withActionListener(e -> {
                                    setVisible(false);
                                    dispose(); // this is needed to dispose the dialog and return the control to the window
                                })
                                .withActionCommand("OK"))
                .addComponent(new JButton(Messages.getString("App.Cancel"))
                        .withActionListener(e -> {
                            setVisible(false);
                            txtStructure.setText(oldStructure);
                            dispose(); // this is needed to dispose the dialog and return the control to the window
                        })
                        .withActionCommand("Cancel"));
    }

    private void generateVideoFiles() {
        ReleaseFactory releaseFactory = new ReleaseFactory(new Settings(), manager);
        if (videoType == VideoType.EPISODE) {
            tvRelease = (TvRelease) releaseFactory.createRelease(
                    Path.of("Terra.Nova.S01E01E02.Genesis.720p.HDTV.x264-ORENJI.mkv"),
                    userInteractionHandler);
        } else if (videoType == VideoType.MOVIE) {
            movieRelease = (MovieRelease) releaseFactory.createRelease(Path.of("Final.Destination.5.720p.Bluray.x264-TWiZTED"),
                    userInteractionHandler);
        }
    }

    private void buildLabelTable(StructureTag[] structureTags) {
        Arrays.stream(structureTags).forEach(this::addTag);
    }

    private void addTag(StructureTag structureTag) {
        new JLabel(structureTag.getLabel())
                .withToolTipText(structureTag.getDescription())
                .addTo(tagPanel)
                .withMouseListener(new InsertTag());
    }

    public String showDialog(String structure) {
        oldStructure = structure;
        txtStructure.setText(structure);
        parseText();
        setVisible(true);
        return txtStructure.getText();
    }

    protected void parseText() {
        lblPreview.setText(libraryBuilder.apply(txtStructure.getText()).build(getGeneratedRelease()).toString());
    }

    private Release getGeneratedRelease() {
        return switch (videoType) {
            case EPISODE -> tvRelease;
            case MOVIE -> movieRelease;
        };
    }

    @Override
    public void changedUpdate(DocumentEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void insertUpdate(DocumentEvent arg0) {
        parseText();

    }

    @Override
    public void removeUpdate(DocumentEvent arg0) {
        parseText();

    }

    private class InsertTag implements MouseListener {
        private int pos, txtStructureLength;
        private JLabel clickedLabel;
        private String clickedTag, beforeCaret, afterCaret;

        @Override
        public void mouseClicked(MouseEvent e) {
            pos = txtStructure.getCaretPosition();
            txtStructureLength = txtStructure.getText().length();
            clickedLabel = (JLabel) e.getComponent();
            if (clickedLabel != null) {
                clickedTag = clickedLabel.getText();

                try {
                    beforeCaret = txtStructure.getText(0, pos);
                    afterCaret = txtStructure.getText(pos, txtStructureLength - pos);
                } catch (BadLocationException ble) {
                    beforeCaret = txtStructure.getText();
                    afterCaret = "";
                }

                txtStructure.setText(String.format("%s%s%s", beforeCaret, clickedTag, afterCaret));
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

        }

    }

}
