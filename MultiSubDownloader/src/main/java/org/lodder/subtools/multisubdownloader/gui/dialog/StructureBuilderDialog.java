package org.lodder.subtools.multisubdownloader.gui.dialog;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;
import java.nio.file.Path;
import java.util.function.Function;

import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;
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

public class StructureBuilderDialog extends MultiSubDialog implements DocumentListener {

    @Serial private static final long serialVersionUID = -5174968778375028124L;

    private final VideoType videoType;
    private final StructureType structureType;
    private final UserInteractionHandler userInteractionHandler;
    private final Function<String, ? extends LibraryBuilder> libraryBuilder;

    private JTextField txtStructure;
    private JLabel lblPreview;
    private TvRelease tvRelease;
    private MovieRelease movieRelease;
    private String oldStructure;
    private JPanel tagPanel;

    public enum StructureType {
        FILE, FOLDER
    }

    public StructureBuilderDialog(@Nullable JFrame frame=null, String title, boolean modal, VideoType videoType,
        StructureType structureType, Manager manager, UserInteractionHandler userInteractionHandler,
        Function<String, ? extends LibraryBuilder> filenameLibraryBuilder) {
        super(frame, title, modal);
        this.videoType = videoType;
        this.structureType = structureType;
        this.userInteractionHandler = userInteractionHandler;
        this.libraryBuilder = filenameLibraryBuilder;
        initializeUI();
        generateVideoFiles(manager);
    }

    private void initializeUI() {
        setBounds(100, 100, 600, 300);
        setMinimumSize(new Dimension(600, 300));

        contentPane
            .layout(new MigLayout("insets 10, nogrid"))
            .addComponent("wrap", new JLabel(getText("StructureBuilderDialog.AvailableTagsClickToAdd")))
            .addComponent("grow, wrap",
                tagPanel = new JPanel(new MigLayout("flowy, wrap 5", "[150px][150px][150px]")))
            .addComponent(new JLabel(getText("StructureBuilderDialog.Structure")))
            .addComponent("span, wrap",
                txtStructure = new JTextField()
                    .columns(100)
                    .documentListener(this))
            .addComponent(new JLabel(getText("StructureBuilderDialog.Preview")))
            .addComponent(lblPreview = new JLabel())
            .addComponent(BorderLayout.SOUTH, new JPanel(new FlowLayout(FlowLayout.RIGHT))
                .addComponent(new JButton(getText("App.OK"))
                    .defaultButtonFor(rootPane)
                    .actionListener(_ -> {
                        setVisible(false);
                        dispose(); // this is needed to dispose the dialog and return the control to the window
                    })
                    .actionCommand("OK"))
                .addComponent(new JButton(getText("App.Cancel"))
                    .actionListener(_ -> {
                        setVisible(false);
                        txtStructure.setText(oldStructure);
                        dispose(); // this is needed to dispose the dialog and return the control to the window
                    })
                    .actionCommand("Cancel")));

        switch (videoType) {
            case EPISODE -> buildLabelTable(SerieStructureTag.values());
            case MOVIE -> buildLabelTable(MovieStructureTag.values());
        }
        if (structureType == StructureType.FOLDER) {
            buildLabelTable(FolderStructureTag.values());
        }
    }

    private void generateVideoFiles(Manager manager) {
        ReleaseFactory releaseFactory = new ReleaseFactory(new Settings(), manager);
        switch (videoType) {
            case EPISODE -> tvRelease = (TvRelease) releaseFactory.createRelease(
                Path.of("Terra.Nova.S01E01E02.Genesis.720p.HDTV.x264-ORENJI.mkv"),
                userInteractionHandler, false);
            case MOVIE -> movieRelease = (MovieRelease) releaseFactory.createRelease(
                Path.of("Final.Destination.5.2011.720p.Bluray.x264-TWiZTED.mkv"),
                userInteractionHandler, false);
        }
    }

    private void buildLabelTable(StructureTag[] structureTags) {
        structureTags.forEach(this::addTag);
    }

    private void addTag(StructureTag structureTag) {
        new JLabel(structureTag.label)
            .withToolTipText(structureTag.description)
            .addTo(tagPanel)
            .mouseListener(new InsertTag());
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

        @Override
        public void mouseClicked(MouseEvent e) {
            int pos = txtStructure.getCaretPosition();
            int txtStructureLength = txtStructure.getText().length();
            JLabel clickedLabel = (JLabel) e.getComponent();
            if (clickedLabel != null) {
                String clickedTag = clickedLabel.getText();
                String afterCaret;
                String beforeCaret;
                try {
                    beforeCaret = txtStructure.getText(0, pos);
                    afterCaret = txtStructure.getText(pos, txtStructureLength - pos);
                } catch (BadLocationException ble) {
                    beforeCaret = txtStructure.getText();
                    afterCaret = "";
                }
                txtStructure.setText("%s%s%s".formatted(beforeCaret, clickedTag, afterCaret));
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // do nothing
        }
    }
}
