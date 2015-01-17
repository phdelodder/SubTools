package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.control.VideoFileParser;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.VideoFile;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.privateRepo.PrivateRepoIndex;
import org.lodder.subtools.sublibrary.util.FilenameContainsFilter;
import org.lodder.subtools.sublibrary.util.FilenameExtensionFilter;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.NamedMatcher;
import org.lodder.subtools.sublibrary.util.NamedPattern;
import org.lodder.subtools.sublibrary.util.StringUtils;
import org.lodder.subtools.sublibrary.util.http.DropBoxClient;
import org.lodder.subtools.sublibrary.util.http.HttpClient;

public class Actions {

  private final Settings settings;
  private final boolean usingCMD;
  private final SubtitleSelection subSelection;

  public Actions(Settings settings, final boolean usingCMD) {
    this.settings = settings;
    this.usingCMD = usingCMD;
    
    if (usingCMD)
      subSelection = new SubtitleSelectionCLI(settings);
    else
      subSelection = new SubtitleSelectionGUI(settings);
  }

  public int determineWhatSubtitleDownload(final VideoFile videoFile,
      final boolean subtitleSelectionDialog) {

    if (videoFile.getMatchingSubs().size() > 0) {
      Logger.instance.debug("determineWhatSubtitleDownload for videoFile: "
          + videoFile.getFilename() + " # found subs: " + videoFile.getMatchingSubs().size());
      if (settings.isOptionsAlwaysConfirm()) {
        return subSelection.getUserInput(videoFile);
      } else if (videoFile.getMatchingSubs().size() == 1
          && videoFile.getMatchingSubs().get(0).getSubtitleMatchType() == SubtitleMatchType.EXACT) {
        Logger.instance.debug("determineWhatSubtitleDownload: Exact Match");
        return 0;
      } else if (settings.isOptionsAutomaticDownloadSelection()) {
        Logger.instance.debug("determineWhatSubtitleDownload: Automatic Download Selection");
        int selected = subSelection.getAutomatic(videoFile);
        if (selected >= 0) return selected;
      } else if (videoFile.getMatchingSubs().size() > 1) {
        Logger.instance.debug("determineWhatSubtitleDownload: Multiple subs detected");
        if (subtitleSelectionDialog) {
          Logger.instance.debug("determineWhatSubtitleDownload: Select subtitle with dialog");
          return subSelection.getUserInput(videoFile);
        } else {
          Logger.instance.log("Multiple subs detected for: " + videoFile.getFilename()
              + " Unhandleable for CMD! switch to GUI"
              + " or use '--selection' as switch in de CMD");

        }
      } else if (videoFile.getMatchingSubs().size() == 1) {
        Logger.instance.debug("determineWhatSubtitleDownload: only one sub taking it!!!!");
        return 0;
      }
    }
    Logger.instance.debug("determineWhatSubtitleDownload: No subs found for: "
        + videoFile.getFilename());
    return -1;
  }
  
  public static String buildDisplayLine(Subtitle subtitle) {
    String hearingImpaired = "";
    if (subtitle.isHearingImpaired()) {
      hearingImpaired = " Hearing Impaired";
    }
    String uploader = "";
    if (subtitle.getSubtitleSource() != SubtitleSource.PRIVATEREPO) {
      if (!subtitle.getUploader().isEmpty())
        uploader = " (Uploader: " + subtitle.getUploader() + ") ";
    }
    return subtitle.getFilename() + hearingImpaired + uploader + " (Source: "
        + subtitle.getSubtitleSource() + ") ";
  }

  public List<File> getFileListing(File dir, boolean recursieve, String languagecode,
      boolean forceSubtitleOverwrite) {
    Logger.instance.trace("Actions", "getFileListing", "dir: " + dir + " recursieve: " + recursieve
        + " languagecode: " + languagecode + " forceSubtitleOverwrite: " + forceSubtitleOverwrite);
    final List<File> filelist = new ArrayList<File>();
    final File[] contents = dir.listFiles();
    if (contents != null) {
      for (final File file : contents) {
        if (file.isFile()) {
          if (isValidVideoFile(file)
              && (!fileHasSubtitles(file, languagecode) || forceSubtitleOverwrite)
              && isNotExcluded(file)) {
            filelist.add(file);
          }
        } else if (recursieve) {
          if (settings.getExcludeList().size() == 0) {
            filelist.addAll(getFileListing(file, recursieve, languagecode, forceSubtitleOverwrite));
          } else {
            Boolean status = true;
            for (int j = 0; j < settings.getExcludeList().size(); j++) {
              if (settings.getExcludeList().get(j).getType() == SettingsExcludeType.FOLDER) {
                File excludeFile = new File(settings.getExcludeList().get(j).getDescription());
                if (excludeFile.equals(file)) {
                  Logger.instance.trace("Actions", "getFileListing", "Skipping: " + file);
                  status = false;
                  j = settings.getExcludeList().size();
                }
              }
            }
            if (status) {
              filelist
                  .addAll(getFileListing(file, recursieve, languagecode, forceSubtitleOverwrite));
            }
          }
        }
      }
    }
    return filelist;
  }

  private boolean isNotExcluded(File file) {
    for (int j = 0; j < settings.getExcludeList().size(); j++) {
      if (settings.getExcludeList().get(j).getType() == SettingsExcludeType.REGEX) {
        NamedPattern np =
            NamedPattern.compile(
                settings.getExcludeList().get(j).getDescription().replace("*", ".*") + ".*$",
                Pattern.CASE_INSENSITIVE);
        NamedMatcher namedMatcher = np.matcher(file.getName());
        if (namedMatcher.find()) {
          Logger.instance.trace("Actions", "isNotExcluded", "Skipping: " + file);
          return false;
        }
      }
    }
    for (int j = 0; j < settings.getExcludeList().size(); j++) {
      if (settings.getExcludeList().get(j).getType() == SettingsExcludeType.FILE) {
        File excludeFile = new File(settings.getExcludeList().get(j).getDescription());
        if (excludeFile.equals(file)) {
          Logger.instance.trace("Actions", "isNotExcluded", "Skipping: " + file);
          return false;
        }
      }
    }
    return true;
  }

  public boolean isValidVideoFile(File file) {
    final String filename = file.getName();
    final int mid = filename.lastIndexOf(".");
    final String ext = filename.substring(mid + 1, filename.length());
    if (filename.contains("sample")) return false;
    for (String allowedExtension : VideoPatterns.EXTENSIONS) {
      if (ext.equalsIgnoreCase(allowedExtension)) return true;
    }
    return false;
  }

  /**
   * @param videoFile
   * @param subtitle
   * @param librarySettings
   * @param version
   * @throws Exception
   */
  public static void download(VideoFile videoFile, Subtitle subtitle,
      LibrarySettings librarySettings, int version) throws Exception {
    Logger.instance.trace("Actions", "download",
        "LibraryAction" + librarySettings.getLibraryAction());
    PathLibraryBuilder pathLibraryBuilder = new PathLibraryBuilder(librarySettings);
    final File path = pathLibraryBuilder.buildPath(videoFile);
    if (!path.exists()) {
      Logger.instance.debug("Download creating folder: " + path.getAbsolutePath());
      if (!path.mkdirs()) {
        throw new Exception("Download unable to create folder: " + path.getAbsolutePath());
      }
    }

    FilenameLibraryBuilder filenameLibraryBuilder = new FilenameLibraryBuilder(librarySettings);
    final String videoFileName = filenameLibraryBuilder.buildFileName(videoFile);
    final String subFileName =
        filenameLibraryBuilder.buildSubFileName(videoFile, subtitle, videoFileName, version);
    final File subFile = new File(path, subFileName);

    boolean success;

    if (subtitle.getSubtitleSource() == SubtitleSource.PRIVATEREPO) {
      success =
          DropBoxClient.getDropBoxClient().doDownloadFile(subtitle.getDownloadlink(), subFile);
    } else {
      if (HttpClient.isUrl(subtitle.getDownloadlink())) {
        success =
            HttpClient.getHttpClient().doDownloadFile(new URL(subtitle.getDownloadlink()), subFile);
        Logger.instance.debug("doDownload file was: " + success);
      } else {
        Files.copy(new File(subtitle.getDownloadlink()), subFile);
        success = true;
      }
      if (VideoFileParser.getQualityKeyword(videoFile.getFilename()).split(" ").length > 1) {
        String dropBoxName = "";
        if (subtitle.getSubtitleSource() == SubtitleSource.LOCAL) {
          dropBoxName =
              PrivateRepoIndex.getFullFilename(
                  FilenameLibraryBuilder.changeExtension(videoFile.getFilename(), ".srt"), "?",
                  subtitle.getSubtitleSource().toString());
        } else {
          dropBoxName =
              PrivateRepoIndex.getFullFilename(
                  FilenameLibraryBuilder.changeExtension(videoFile.getFilename(), ".srt"),
                  subtitle.getUploader(), subtitle.getSubtitleSource().toString());
        }
        DropBoxClient.getDropBoxClient().put(subFile, dropBoxName, subtitle.getLanguagecode());
      }
    }

    if (success) {
      if (!librarySettings.getLibraryAction().equals(LibraryActionType.NOTHING)) {
        final File oldLocationFile = new File(videoFile.getPath(), videoFile.getFilename());
        if (oldLocationFile.exists()) {
          final File newLocationFile = new File(path, videoFileName);
          Logger.instance.log("Moving/Renaming " + videoFileName + " to folder " + path.getPath()
              + " , this might take a while... ");
          Files.move(oldLocationFile, newLocationFile);
          if (!librarySettings.getLibraryOtherFileAction().equals(
              LibraryOtherFileActionType.NOTHING)) {
            cleanUpFiles(librarySettings, videoFile, path, videoFileName);
          }
          if (librarySettings.isLibraryRemoveEmptyFolders()
              && videoFile.getPath().listFiles().length == 0) {
            videoFile.getPath().delete();
          }
        }
      }
      if (librarySettings.isLibraryBackupSubtitle()) {
        String langFolder = "";
        if (subtitle.getLanguagecode().equals("nl")) {
          langFolder = "Nederlands";
        } else {
          langFolder = "Engels";
        }
        File backupPath =
            new File(librarySettings.getLibraryBackupSubtitlePath() + File.separator + langFolder
                + File.separator);

        if (!backupPath.exists()) {
          if (!backupPath.mkdirs()) {
            throw new Exception("Download unable to create folder: " + backupPath.getAbsolutePath());
          }
        }

        if (librarySettings.isLibraryBackupUseWebsiteFileName()) {
          Files.copy(subFile, new File(backupPath, subtitle.getFilename()));
        } else {
          Files.copy(subFile, new File(backupPath, subFileName));
        }
      }
    }
  }

  public static void rename(LibrarySettings librarySettings, File f, VideoFile videoFile) {
    Logger.instance
        .trace("Actions", "rename", "LibraryAction" + librarySettings.getLibraryAction());
    String filename = "";
    if (librarySettings.getLibraryAction().equals(LibraryActionType.RENAME)
        || librarySettings.getLibraryAction().equals(LibraryActionType.MOVEANDRENAME)) {
      FilenameLibraryBuilder filenameLibraryBuilder = new FilenameLibraryBuilder(librarySettings);
      filename = filenameLibraryBuilder.buildFileName(videoFile);
      if (videoFile.getExtension().equals("srt")) {
        String languageCode = "";
        try {
          if (librarySettings.isLibraryIncludeLanguageCode()) {
            languageCode = DetectLanguage.execute(f);
          }
        } catch (final Exception e) {
          Logger.instance.error("Unable to detect language, leaving language code blank");
        }

        filename = filenameLibraryBuilder.buildSubFileName(videoFile, filename, languageCode, 0);
      }
    } else {
      filename = f.getName();
    }
    Logger.instance.trace("Actions", "rename", "filename" + filename);

    PathLibraryBuilder pathLibraryBuilder = new PathLibraryBuilder(librarySettings);
    final File newDir = pathLibraryBuilder.buildPath(videoFile);
    boolean status = true;
    if (!newDir.exists()) {
      Logger.instance.debug("Creating dir: " + newDir.getAbsolutePath());
      status = newDir.mkdirs();
    }

    Logger.instance.trace("Actions", "rename", "newDir" + newDir);

    if (status) {
      final File file = new File(videoFile.getPath(), videoFile.getFilename());

      try {

        if (librarySettings.getLibraryAction().equals(LibraryActionType.MOVE)
            || librarySettings.getLibraryAction().equals(LibraryActionType.MOVEANDRENAME)) {
          Logger.instance.log("Moving " + filename + " to the library folder " + newDir
              + " , this might take a while... ");
          Files.move(file, new File(newDir, filename));
        } else {
          Logger.instance.log("Moving " + filename + " to the library folder "
              + videoFile.getPath() + " , this might take a while... ");
          Files.move(file, new File(videoFile.getPath(), filename));
        }
        if (!librarySettings.getLibraryOtherFileAction().equals(LibraryOtherFileActionType.NOTHING)) {
          cleanUpFiles(librarySettings, videoFile, newDir, filename);
        }
        if (librarySettings.isLibraryRemoveEmptyFolders()
            && videoFile.getPath().listFiles().length == 0) {
          videoFile.getPath().delete();
        }
      } catch (IOException e) {
        Logger.instance.error("Unsuccessfull in moving the file to the libary");
      }

    }
  }

  private static void cleanUpFiles(LibrarySettings librarySettings, VideoFile videoFile, File path,
      String videoFileName) throws IOException {
    Logger.instance.trace("Actions", "cleanUpFiles",
        "LibraryOtherFileAction" + librarySettings.getLibraryOtherFileAction());
    final List<String> fileFilters = new ArrayList<String>();
    fileFilters.add("nfo");
    fileFilters.add("jpg");
    fileFilters.add("sfv");
    fileFilters.add("srr");
    fileFilters.add("srs");
    fileFilters.add("nzb");
    fileFilters.add("torrent");
    fileFilters.add("txt");
    final String[] files =
        videoFile.getPath().list(
            new FilenameExtensionFilter(fileFilters.toArray(new String[fileFilters.size()])));

    final List<String> folderFilters = new ArrayList<String>();
    folderFilters.add("sample");
    folderFilters.add("Sample");
    final String[] folders =
        videoFile.getPath().list(
            new FilenameContainsFilter(folderFilters.toArray(new String[folderFilters.size()])));

    // remove duplicates using set
    final Set<String> list =
        new LinkedHashSet<String>(Arrays.asList(StringUtils.join(files, folders)));

    if (librarySettings.getLibraryOtherFileAction().equals(LibraryOtherFileActionType.REMOVE)) {
      for (String s : list) {
        final File file = new File(videoFile.getPath(), s);
        if (file.isDirectory()) {
          FileUtils.deleteDirectory(file);
        } else {
          file.delete();
        }
      }
    } else if (librarySettings.getLibraryOtherFileAction().equals(LibraryOtherFileActionType.MOVE)) {
      for (String s : list) {
        Files.move(new File(videoFile.getPath(), s), new File(path, s));
      }
    } else if (librarySettings.getLibraryOtherFileAction().equals(
        LibraryOtherFileActionType.MOVEANDRENAME)) {
      for (String s : list) {
        String extension = VideoFileParser.extractFileNameExtension(s);

        File f = new File(videoFile.getPath(), s);

        if (s.contains("sample") && !f.isDirectory()) {
          extension = "sample." + extension;
        }

        if (f.isFile()) {
          final String filename =
              videoFileName.substring(0, videoFileName.lastIndexOf(".")).concat("." + extension);
          Files.move(f, new File(path, filename));
        } else {
          Files.move(f, new File(path, s));
        }
      }
    } else if (librarySettings.getLibraryOtherFileAction()
        .equals(LibraryOtherFileActionType.RENAME)) {
      for (String s : files) {
        String extension = VideoFileParser.extractFileNameExtension(s);

        File f = new File(videoFile.getPath(), s);

        if (s.contains("sample") && !f.isDirectory()) {
          extension = "sample." + extension;
        }

        if (f.isFile()) {
          final String filename =
              videoFileName.substring(0, videoFileName.lastIndexOf(".")).concat("." + extension);
          Files.move(f, new File(videoFile.getPath(), filename));
        } else {
          Files.move(f, new File(path, s));
        }
      }
    }
  }

  public boolean fileHasSubtitles(File file, String languageCode) {
    String subname = "";
    for (String allowedExtension : VideoPatterns.EXTENSIONS) {
      if (file.getName().contains("." + allowedExtension))
        subname = file.getName().replace("." + allowedExtension, ".srt");
    }

    final File f = new File(file.getParentFile(), subname);
    if (f.exists()) {
      return true;
    } else {
      List<String> filters = new ArrayList<String>();
      if (languageCode.equals("nl")) {
        filters.add("nld.srt");
        filters.add("ned.srt");
        filters.add("dutch.srt");
        filters.add("dut.srt");
        filters.add("nl.srt");
        if (!settings.getEpisodeLibrarySettings().getDefaultNlText().equals(""))
          filters.add("." + settings.getEpisodeLibrarySettings().getDefaultNlText().concat(".srt"));
        final String[] contents =
            file.getParentFile().list(
                new FilenameExtensionFilter(filters.toArray(new String[filters.size()])));
        return checkFileListContent(contents, subname.replace(".srt", ""));
      } else if (languageCode.equals("en")) {
        filters.add("eng.srt");
        filters.add("english.srt");
        filters.add("en.srt");
        if (!settings.getEpisodeLibrarySettings().getDefaultEnText().equals(""))
          filters.add("." + settings.getEpisodeLibrarySettings().getDefaultEnText().concat(".srt"));
        final String[] contents =
            file.getParentFile().list(
                new FilenameExtensionFilter(filters.toArray(new String[filters.size()])));
        return checkFileListContent(contents, subname.replace(".srt", ""));
      }
    }
    return false;
  }

  public boolean checkFileListContent(String[] contents, String subname) {
    if (contents.length > 0) {
      for (final String file : contents) {
        if (file.contains(subname)) {
          return true;
        }
      }
    }
    return false;
  }

  public void download(VideoFile videoFile, Subtitle subtitle, int version) throws Exception {
    if (videoFile.getVideoType().equals(VideoType.EPISODE)) {
      download(videoFile, subtitle, settings.getEpisodeLibrarySettings(), version);
    } else if (videoFile.getVideoType().equals(VideoType.MOVIE)) {
      download(videoFile, subtitle, settings.getMovieLibrarySettings(), version);
    }
  }

  public void download(VideoFile videoFile, Subtitle subtitle) throws Exception {
    download(videoFile, subtitle, 0);
  }
}
