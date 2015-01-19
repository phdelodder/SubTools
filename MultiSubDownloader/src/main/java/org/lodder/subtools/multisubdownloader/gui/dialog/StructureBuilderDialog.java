package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import org.lodder.subtools.multisubdownloader.lib.control.VideoFileFactory;
import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.VideoType;

import net.miginfocom.swing.MigLayout;

public class StructureBuilderDialog extends MutliSubDialog implements DocumentListener {

  /**
     * 
     */
  private static final long serialVersionUID = -5174968778375028124L;
  private final JPanel contentPanel = new JPanel();
  private JTextField txtStructure;
  private VideoType videoType;
  private LibrarySettings librarySettings;
  private StrucutureType structureType;
  private JLabel lblPreview;
  private TvRelease ep;
  private MovieRelease mo;
  private String oldStructure;

  public enum StrucutureType {
    FILE, FOLDER
  }

  public StructureBuilderDialog(JFrame frame, String title, boolean modal, VideoType videoType,
      StrucutureType structureType, LibrarySettings librarySettings) {
    super(frame, title, modal);
    this.videoType = videoType;
    this.librarySettings = librarySettings;
    this.structureType = structureType;
    initializeUi();
    generateVideoFiles();
    mo = new MovieRelease();
  }

  private void generateVideoFiles() {
    // Used as an preview
    try {
      if (videoType == VideoType.EPISODE) {
        ep =
            (TvRelease) VideoFileFactory.get(
                // new File(File.separator + "Castle.2009.S04E10.720p.HDTV.X264-DIMENSION.mkv"),
                new File(File.separator + "Terra.Nova.S01E01E02.720p.HDTV.x264-ORENJI.mkv"),
                new File(File.separator), new Settings(), "");
      } else if (videoType == VideoType.MOVIE) {
        mo =
            (MovieRelease) VideoFileFactory.get(new File(File.separator
                + "Final.Destination.5.720p.Bluray.x264-TWiZTED"), new File(File.separator),
                new Settings(), "");
      }
    } catch (ControlFactoryException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (VideoControlException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (VideoFileParseException e) {
      Logger.instance.error(Logger.stack2String(e));
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
    tagPanel.add(new JLabel("Beschikbare tags (klik erop om ze in te voegen)."),
        "cell 0 0 2 1,alignx left,aligny top");
    if (videoType == VideoType.EPISODE) {
      // add tv show tags
      buildLabelTable(EPISODE_TAGS, 4);

    } else if (videoType == VideoType.MOVIE) {
      // add movie tags
      buildLabelTable(MOVIE_TAGS, 4);
    }

    contentPanel.add(tagPanel, "cell 0 0 2 1,grow");
    JLabel lblNewLabel = new JLabel("Structure");
    contentPanel.add(lblNewLabel, "cell 0 1,alignx left");
    txtStructure = new JTextField();
    contentPanel.add(txtStructure, "cell 1 1,growx");
    txtStructure.setColumns(10);
    txtStructure.getDocument().addDocumentListener(this);
    JLabel lblNewLabel_1 = new JLabel("Preview");
    contentPanel.add(lblNewLabel_1, "cell 0 2");
    lblPreview = new JLabel("");
    contentPanel.add(lblPreview, "cell 1 2");
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose(); // this is needed to dispose the dialog and return the control to the window
      }
    });
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        txtStructure.setText(oldStructure);
        dispose(); // this is needed to dispose the dialog and return the control to the window
      }
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
    if (structureType == StrucutureType.FILE) {
      librarySettings.setLibraryFilenameStructure(txtStructure.getText());
      FilenameLibraryBuilder filenameLibraryBuilder = new FilenameLibraryBuilder(librarySettings);
      lblPreview.setText(filenameLibraryBuilder.buildFileName(getGenerateVideoFile()));
    } else if (structureType == StrucutureType.FOLDER) {
      librarySettings.setLibraryFolderStructure(txtStructure.getText());
      PathLibraryBuilder pathLibraryBuilder = new PathLibraryBuilder(librarySettings);
      lblPreview.setText(pathLibraryBuilder.buildPath(getGenerateVideoFile()).getAbsolutePath());
    }
  }

  private Release getGenerateVideoFile() {
    if (videoType == VideoType.EPISODE) {
      return ep;
    } else if (videoType == VideoType.MOVIE) {
      return mo;
    }
    return null;
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
      // TODO Auto-generated method stub
      pos = txtStructure.getCaretPosition();
      txtStructureLength = txtStructure.getText().length();
      clickedLabel = (JLabel) e.getComponent();
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
          put("%SHOW NAME%", "Naam van de tv serie.");
          put("%TITLE%", "Titel van de aflevering.");
          put("%EE%", "Nummer van de aflevering (met 0).");
          put("%EEX%", "Nummer van de aflevering (met 0) voor multi episode.");
          put("%E%", "Nummer van de aflevering (zonder 0).");
          put("%EX%", "Nummer van de aflevering (zonder 0) voor multi episode.");
          put("%SS%", "Nummer van het seizoen (met 0).");
          put("%S%", "Nummer van het seizoen (zonder 0).");
          put("%QUALITY%", "Kwaliteit van het filmbestand, bijv. HDTV 720p.");
          put("%DESCRIPTION%",
              "Overige info uit de bestandsnaam van het filmbestand, bijv. de release group.");
          put("%SEPARATOR%",
              "Systeemonafhankelijk scheidingsteken voor nieuwe directory, bijv. \\.");
        }
      });

  private static final Map<String, String> MOVIE_TAGS = Collections
      .unmodifiableMap(new HashMap<String, String>() {
        /**
                 * 
                 */
        private static final long serialVersionUID = 5943868685951628245L;

        {
          put("%MOVIE NAME%", "Naam van de film.");
          put("%YEAR%", "Jaartal wanneer de film is uitgekomen.");
          put("%QUALITY%", "Kwaliteit van het filmbestand, bijv. BluRay 720p.");
          put("%DESCRIPTION%",
              "Overige info uit de bestandsnaam van het filmbestand, bijv. de release group.");
          put("%SEPARATOR%",
              "Systeemonafhankelijk scheidingsteken voor nieuwe directory, bijv. \\.");

        }
      });
  private JPanel tagPanel;

}
