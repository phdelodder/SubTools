package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import net.miginfocom.swing.MigLayout;

public class StructureBuilderDialog extends MultiSubDialog implements DocumentListener {

    private static final long serialVersionUID = -5174968778375028124L;
    private final JPanel contentPanel = new JPanel();
    private JTextField txtStructure;
    private final VideoType videoType;
    private final LibrarySettings librarySettings;
    private final StrucutureType structureType;
    private JLabel lblPreview;
    private TvRelease tvRelease;
    private MovieRelease movieRelease;
    private String oldStructure;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    public enum StrucutureType {
        FILE, FOLDER
    }

    public StructureBuilderDialog(JFrame frame, String title, boolean modal, VideoType videoType,
            StrucutureType structureType, LibrarySettings librarySettings, Manager manager, UserInteractionHandler userInteractionHandler) {
        super(frame, title, modal);
        this.videoType = videoType;
        this.librarySettings = librarySettings;
        this.structureType = structureType;
        this.manager = manager;
        this.userInteractionHandler = userInteractionHandler;
        initializeUi();
        generateVideoFiles();
    }

    private void generateVideoFiles() {
        ReleaseFactory releaseFactory = new ReleaseFactory(new Settings(), manager);
        if (videoType == VideoType.EPISODE) {
            tvRelease = (TvRelease) releaseFactory.createRelease(
                    // new File(File.separator + "Castle.2009.S04E10.720p.HDTV.X264-DIMENSION.mkv"),
                    new File(File.separator + "Terra.Nova.S01E01E02.720p.HDTV.x264-ORENJI.mkv"),
                    userInteractionHandler);
        } else if (videoType == VideoType.MOVIE) {
            movieRelease = (MovieRelease) releaseFactory.createRelease(new File(File.separator + "Final.Destination.5.720p.Bluray.x264-TWiZTED"),
                    userInteractionHandler);
        }
    }

    private void initializeUi() {
        setBounds(100, 100, 600, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[][grow]", "[grow][][]"));
        tagPanel = new JPanel();
        tagPanel.setLayout(new MigLayout("", "[150px][150px]", "[15px]"));

        // add header label
        tagPanel.add(new JLabel(Messages.getString("StructureBuilderDialog.AvailableTagsClickToAdd")),
                "cell 0 0 2 1,alignx left,aligny top");
        if (videoType == VideoType.EPISODE) {
            // add tv show tags
            buildLabelTable(EPISODE_TAGS, 4);

        } else if (videoType == VideoType.MOVIE) {
            // add movie tags
            buildLabelTable(MOVIE_TAGS, 4);
        }

        contentPanel.add(tagPanel, "cell 0 0 2 1,grow");
        JLabel lblNewLabel = new JLabel(Messages.getString("StructureBuilderDialog.Structure"));
        contentPanel.add(lblNewLabel, "cell 0 1,alignx left");
        txtStructure = new JTextField();
        contentPanel.add(txtStructure, "cell 1 1,growx");
        txtStructure.setColumns(10);
        txtStructure.getDocument().addDocumentListener(this);
        JLabel lblNewLabel_1 = new JLabel(Messages.getString("StructureBuilderDialog.Preview"));
        contentPanel.add(lblNewLabel_1, "cell 0 2");
        lblPreview = new JLabel("");
        contentPanel.add(lblPreview, "cell 1 2");
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        JButton okButton = new JButton(Messages.getString("StructureBuilderDialog.OK"));
        okButton.addActionListener(e -> {
            setVisible(false);
            dispose(); // this is needed to dispose the dialog and return the control to the window
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton(Messages.getString("StructureBuilderDialog.Cancel"));
        cancelButton.addActionListener(e -> {
            setVisible(false);
            txtStructure.setText(oldStructure);
            dispose(); // this is needed to dispose the dialog and return the control to the window
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);

    }

    // Needs miglayout
    private void buildLabelTable(Map<String, String> map, int maxRows) {
        int row = 0;
        int col = 0;
        for (Entry<String, String> entry : map.entrySet()) {
            JLabel label = new JLabel(entry.getKey());
            label.addMouseListener(new InsertTag());
            label.setToolTipText(entry.getValue());
            row++;
            if (row > maxRows) {
                col++;
                row = 1;
            }
            tagPanel.add(label, "cell " + col + " " + row);
        }
    }

    public String showDialog(String structure) {
        oldStructure = structure;
        txtStructure.setText(structure);
        parseText();
        setVisible(true);
        return txtStructure.getText();
    }

    protected void parseText() {
        Release release = getGenerateRelease();
        if (release == null) {
            return;
        }
        switch (structureType) {
            case FILE -> {
                librarySettings.setLibraryFilenameStructure(txtStructure.getText());
                FilenameLibraryBuilder filenameLibraryBuilder = new FilenameLibraryBuilder(librarySettings, manager, userInteractionHandler);
                lblPreview.setText(filenameLibraryBuilder.build(release));
            }
            case FOLDER -> {
                librarySettings.setLibraryFolderStructure(txtStructure.getText());
                PathLibraryBuilder pathLibraryBuilder = new PathLibraryBuilder(librarySettings, manager, userInteractionHandler);
                lblPreview.setText(pathLibraryBuilder.build(release));
            }
            default -> {
            }
        }
    }

    private Release getGenerateRelease() {
        return switch (videoType) {
            case EPISODE -> tvRelease;
            case MOVIE -> movieRelease;
            default -> null;
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

    private static final Map<String, String> EPISODE_TAGS = Collections
            .unmodifiableMap(new HashMap<String, String>() {
                /**
                         *
                         */
                private static final long serialVersionUID = 3313041588123263612L;

                {
                    put("%SHOW NAME%", Messages.getString("StructureBuilderDialog.NameTvShow"));
                    put("%TITLE%", Messages.getString("StructureBuilderDialog.EpisodeTitle"));
                    put("%EE%", Messages.getString("StructureBuilderDialog.NumberOfEpisodeLeadingZero"));
                    put("%EEX%",
                            Messages.getString("StructureBuilderDialog.NumberOfEpisodeLeadingZeroForMultipe"));
                    put("%E%", Messages.getString("StructureBuilderDialog.NumberOfEpisodeWithoutLeadingZero"));
                    put("%EX%",
                            Messages.getString("StructureBuilderDialog.NumberOfEpisodeLeadingZeroMultiple"));
                    put("%SS%", Messages.getString("StructureBuilderDialog.NumberOfSeasonLeading"));
                    put("%S%", Messages.getString("StructureBuilderDialog.NumberOfSeasonsWithoutLeading"));
                    put("%QUALITY%", Messages.getString("StructureBuilderDialog.QualityOfRelease"));
                    put("%DESCRIPTION%", Messages.getString("StructureBuilderDialog.Description"));
                    put("%SEPARATOR%", Messages.getString("StructureBuilderDialog.SystemdependendSeparator"));
                }
            });

    private static final Map<String, String> MOVIE_TAGS = Collections
            .unmodifiableMap(new HashMap<String, String>() {
                /**
                         *
                         */
                private static final long serialVersionUID = 5943868685951628245L;

                {
                    put("%MOVIE NAME%", Messages.getString("StructureBuilderDialog.MovieName"));
                    put("%YEAR%", Messages.getString("StructureBuilderDialog.MovieYear"));
                    put("%QUALITY%", Messages.getString("StructureBuilderDialog.QualityOfMovie"));
                    put("%DESCRIPTION%", Messages.getString("StructureBuilderDialog.MovieDescription"));
                    put("%SEPARATOR%", Messages.getString("StructureBuilderDialog.SystemdependendSeparator"));

                }
            });
    private JPanel tagPanel;

}
