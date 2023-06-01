package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.io.Serial;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.JButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryBuilder;
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

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JButtonExtension.class, AbstractButtonExtension.class, JComponentExtension.class })
public class StructureBuilderDialog extends MultiSubDialog implements DocumentListener {

    @Serial
    private static final long serialVersionUID = -5174968778375028124L;
    private final VideoType videoType;
    private final StructureType structureType;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;
    private final Function<String, LibraryBuilder> libraryBuilder;
    private final JPanel contentPanel = new JPanel();
    private JTextField txtStructure;
    private JLabel lblPreview;
    private TvRelease tvRelease;
    private MovieRelease movieRelease;
    private String oldStructure;
    private int tagRow = 0;
    private int tagCol = 0;

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
            buildLabelTable(EPISODE_TAGS, 5);

        } else if (videoType == VideoType.MOVIE) {
            // add movie tags
            buildLabelTable(MOVIE_TAGS, 5);
        }
        if (structureType == StructureType.FOLDER) {
            buildLabelTable(FOLDER_TAGS, 5);
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

        new JButton(Messages.getString("App.OK"))
                .defaultButtonFor(getRootPane())
                .withActionListener(e -> {
                    setVisible(false);
                    dispose(); // this is needed to dispose the dialog and return the control to the window
                })
                .withActionCommand("OK")
                .addTo(buttonPane);

        new JButton(Messages.getString("App.Cancel"))
                .withActionListener(e -> {
                    setVisible(false);
                    txtStructure.setText(oldStructure);
                    dispose(); // this is needed to dispose the dialog and return the control to the window
                })
                .withActionCommand("Cancel")
                .addTo(buttonPane);

    }

    private void buildLabelTable(Map<String, String> map, int maxRows) {
        map.forEach(this::addTag);
    }

    private void addTag(String tag, String tooltipText) {
        JLabel label = new JLabel(tag);
        label.addMouseListener(new InsertTag());
        label.setToolTipText(tooltipText);
        tagRow++;
        if (tagRow > 5) {
            tagCol++;
            tagRow = 1;
        }
        tagPanel.add(label, "cell " + tagCol + " " + tagRow);
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

    private static final Map<String, String> EPISODE_TAGS = Collections.unmodifiableMap(new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 3313041588123263612L;
        {
            put("%SHOW NAME%", Messages.getString("StructureBuilderDialog.NameTvShow"));
            put("%TITLE%", Messages.getString("StructureBuilderDialog.EpisodeTitle"));
            put("%EE%", Messages.getString("StructureBuilderDialog.NumberOfEpisodeLeadingZero"));
            put("%EEX%", Messages.getString("StructureBuilderDialog.NumberOfEpisodeLeadingZeroForMultipe"));
            put("%E%", Messages.getString("StructureBuilderDialog.NumberOfEpisodeWithoutLeadingZero"));
            put("%EX%", Messages.getString("StructureBuilderDialog.NumberOfEpisodeLeadingZeroMultiple"));
            put("%SS%", Messages.getString("StructureBuilderDialog.NumberOfSeasonLeading"));
            put("%S%", Messages.getString("StructureBuilderDialog.NumberOfSeasonsWithoutLeading"));
            put("%QUALITY%", Messages.getString("StructureBuilderDialog.QualityOfRelease"));
            put("%DESCRIPTION%", Messages.getString("StructureBuilderDialog.Description"));
            // put("%SEPARATOR%", Messages.getString("StructureBuilderDialog.SystemdependendSeparator"));
        }
    });

    private static final Map<String, String> MOVIE_TAGS = Collections.unmodifiableMap(new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 5943868685951628245L;
        {
            put("%MOVIE TITLE%", Messages.getString("StructureBuilderDialog.MovieName"));
            put("%YEAR%", Messages.getString("StructureBuilderDialog.MovieYear"));
            put("%QUALITY%", Messages.getString("StructureBuilderDialog.QualityOfMovie"));
            put("%DESCRIPTION%", Messages.getString("StructureBuilderDialog.MovieDescription"));
            // put("%SEPARATOR%", Messages.getString("StructureBuilderDialog.SystemdependendSeparator"));
        }
    });

    private static final Map<String, String> FOLDER_TAGS = Collections.unmodifiableMap(new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 5943868685951628245L;
        {
            put("%SEPARATOR%", Messages.getString("StructureBuilderDialog.SystemdependendSeparator"));
        }
    });
    private JPanel tagPanel;

}
