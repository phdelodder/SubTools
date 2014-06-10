package org.lodder.subtools.multisubdownloader.lib.control;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lodder.subtools.multisubdownloader.lib.JAddic7edAdapter;
import org.lodder.subtools.multisubdownloader.lib.JOpenSubAdapter;
import org.lodder.subtools.multisubdownloader.lib.JPodnapisiAdapter;
import org.lodder.subtools.multisubdownloader.lib.JTVsubtitlesAdapter;
import org.lodder.subtools.multisubdownloader.lib.PrivateRepo;
import org.lodder.subtools.multisubdownloader.settings.model.SearchSubtitlePriority;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.control.VideoFileParser;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.VideoFile;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.util.Utils;

public class SubtitleControl {

  private final JOpenSubAdapter jOpenSubAdapter;
  private final JPodnapisiAdapter jPodnapisiAdapter;
  private final JAddic7edAdapter jAddic7edAdapter;
  private final JTVsubtitlesAdapter jTVSubtitlesAdapter;
  private final PrivateRepo privateRepo;
  private final Settings settings;

  public SubtitleControl(Settings settings) {
    this.settings = settings;
    jOpenSubAdapter = new JOpenSubAdapter();
    jPodnapisiAdapter = new JPodnapisiAdapter();
    jAddic7edAdapter =
        new JAddic7edAdapter(settings.isLoginAddic7edEnabled(),
            settings.getLoginAddic7edUsername(), settings.getLoginAddic7edPassword());
    jTVSubtitlesAdapter = new JTVsubtitlesAdapter();
    privateRepo = PrivateRepo.getPrivateRepo();
  }

  public List<Subtitle> getSubtitles(EpisodeFile episodeFile, String... languagecode) {
    Logger.instance.trace("SubtitleControl", "getSubtitles", "Episode");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();

    for (SearchSubtitlePriority searchSubtitlePriority : settings.getListSearchSubtitlePriority()) {
      switch (searchSubtitlePriority.getSubtitleSource()) {
        case ADDIC7ED:
          if (settings.isSerieSourceAddic7ed())
            listFoundSubtitles.addAll(jAddic7edAdapter.searchSubtitles(episodeFile, languagecode));
          break;
        case LOCAL:
          if (settings.isSerieSourceLocal())
            listFoundSubtitles.addAll(addLocalLibrary(episodeFile, languagecode[0]));
          break;
        case OPENSUBTITLES:
          if (settings.isSerieSourceOpensubtitles())
            listFoundSubtitles.addAll(jOpenSubAdapter.searchSubtitles(episodeFile, languagecode));
          break;
        case PODNAPISI:
          if (settings.isSerieSourcePodnapisi())
            listFoundSubtitles.addAll(jPodnapisiAdapter.searchSubtitles(episodeFile,
                languagecode[0]));
          break;
        case PRIVATEREPO:
          if (settings.isSerieSourcePrivateRepo()) {
            try {
              listFoundSubtitles.addAll(privateRepo.searchSubtitles(episodeFile, languagecode[0]));
            } catch (UnsupportedEncodingException e) {
              Logger.instance.error(Logger.stack2String(e));
            }
          }
          break;
        case TVSUBTITLES:
          if (settings.isSerieSourceTvSubtitles())
            listFoundSubtitles.addAll(jTVSubtitlesAdapter.searchSubtitles(episodeFile,
                languagecode[0]));
          break;
        default:
          break;
      }

      boolean tempOptionNoResultShowItAll = settings.isOptionNoResultShowItAll();
      settings.setOptionNoResultShowItAll(false);
      List<Subtitle> listResultFiltered =
          this.getSubtitlesFiltered(listFoundSubtitles, episodeFile);
      settings.setOptionNoResultShowItAll(tempOptionNoResultShowItAll);
      if (listResultFiltered.size() > 0) {
        return listResultFiltered;
      }
    }

    return this.getSubtitlesFiltered(listFoundSubtitles, episodeFile);
  }

  public List<Subtitle> getSubtitles(MovieFile movieFile, String... languagecode) {
    Logger.instance.trace("SubtitleControl", "getSubtitles", "Movie");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    listFoundSubtitles.addAll(jOpenSubAdapter.searchSubtitles(movieFile, languagecode));
    listFoundSubtitles.addAll(jPodnapisiAdapter.searchSubtitles(movieFile, languagecode[0]));
    return this.getSubtitlesFiltered(listFoundSubtitles, movieFile);
  }

  private List<Subtitle> addLocalLibrary(EpisodeFile episodeFile, String languagecode) {
    Logger.instance.trace("SubtitleControl", "addLocalLibrary", "");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    List<File> possibleSubtitles = new ArrayList<File>();
    VideoFileParser vfp = new VideoFileParser();

    String filter = "";
    if (episodeFile.getOriginalShowName().length() > 0) {
      filter = episodeFile.getOriginalShowName().replaceAll("[^A-Za-z]", "").trim();
    } else {
      filter = episodeFile.getShow().replaceAll("[^A-Za-z]", "").trim();
    }

    for (File local : settings.getLocalSourcesFolders()) {
      possibleSubtitles.addAll(getAllSubtitlesFiles(local, filter));
    }

    for (File fileSub : possibleSubtitles) {
      try {
        VideoFile videoFile = vfp.parse(fileSub, new File(fileSub.getPath()));
        if (videoFile.getVideoType() == VideoType.EPISODE) {

          if (((EpisodeFile) videoFile).getSeason() == episodeFile.getSeason()
              && Utils.containsAll(((EpisodeFile) videoFile).getEpisodeNumbers(),
                  episodeFile.getEpisodeNumbers())) {
            EpisodeFileControl epCtrl = new EpisodeFileControl((EpisodeFile) videoFile, settings);
            epCtrl.process(settings.getMappingSettings().getMappingList());
            if (((EpisodeFile) videoFile).getTvdbid() == episodeFile.getTvdbid()) {
              String detectedLang = DetectLanguage.execute(fileSub);
              if (detectedLang.equals(languagecode)) {
                Logger.instance.debug("Local Sub found, adding " + fileSub.toString());
                listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.LOCAL, fileSub
                    .getName(), fileSub.toString(), "", "", SubtitleMatchType.EVERYTHING,
                    VideoFileParser.extractTeam(fileSub.getName()), fileSub.getAbsolutePath(),
                    false));
              }
            }
          }
        }
      } catch (Exception e) {
        if (Logger.instance.getLogLevel().intValue() < Level.INFO.intValue()) {
          Logger.instance.error(Logger.stack2String(e));
        } else {
          Logger.instance.error(e.getMessage());
        }
      }
    }

    return listFoundSubtitles;
  }

  private List<File> getAllSubtitlesFiles(File dir, String filter) {
    Logger.instance.trace("SubtitleControl", "getAllSubtitlesFiles", "");
    final List<File> filelist = new ArrayList<File>();
    final File[] contents = dir.listFiles();
    if (contents != null) {
      for (final File file : contents) {
        if (file.isFile()) {
          if (file.getName().replaceAll("[^A-Za-z]", "").toLowerCase()
              .contains(filter.toLowerCase())
              && VideoFileParser.extractFileNameExtension(file.getName()).equals("srt")) {
            filelist.add(file);
          }
        } else {
          filelist.addAll(getAllSubtitlesFiles(file, filter));
        }
      }
    }
    return filelist;
  }

  protected List<Subtitle> getSubtitlesFiltered(List<Subtitle> listFoundSubtitles,
      VideoFile videoFile) {
    Logger.instance.trace("SubtitleControl", "getSubtitlesFiltered", "");

    boolean foundExactMatch = false;
    boolean foundKeywordMatch = false;
    List<Subtitle> listFilteredSubtitles;
    listFilteredSubtitles = new ArrayList<Subtitle>();

    if (settings.isOptionSubtitleExcludeHearingImpaired()) {
      Iterator<Subtitle> i = listFoundSubtitles.iterator();
      while (i.hasNext()) {
        Subtitle sub = i.next();
        if (sub.isHearingImpaired()) i.remove();
      }
    }

    String subRequest = " ";
    if (!(videoFile.getFilename() == null)) {
      subRequest = videoFile.getFilename().toLowerCase();
      subRequest = subRequest.replace("." + videoFile.getExtension(), "");
    }

    if (settings.isOptionSubtitleExactMatch()) {
      Pattern p = Pattern.compile(subRequest.replaceAll(" ", "[. ]"), Pattern.CASE_INSENSITIVE);

      for (Subtitle subtitle : listFoundSubtitles) {
        Matcher m = p.matcher(subtitle.getFilename().toLowerCase().replace(".srt", ""));
        if (m.matches()) {
          subtitle.setSubtitleMatchType(SubtitleMatchType.EXACT);
          subtitle.setQuality(VideoFileParser.getQualityKeyword(subtitle.getFilename()));
          Logger.instance.debug("getSubtitlesFiltered: found EXACT match: "
              + subtitle.getFilename());
          addToFoundSubtitleList(listFilteredSubtitles, subtitle);
          foundExactMatch = true;
        }
      }
    }

    if (settings.isOptionSubtitleKeywordMatch()) {
      // check keywords
      String keywordsFile = VideoFileParser.getQualityKeyword(subRequest);

      for (Subtitle subtitle : listFoundSubtitles) {
        boolean checkKeywordMatch = checkKeywordSubtitleMatch(subtitle, keywordsFile);

        if (checkKeywordMatch) {
          subtitle.setSubtitleMatchType(SubtitleMatchType.KEYWORD);
          subtitle.setQuality(VideoFileParser.getQualityKeyword(subtitle.getFilename()));
          Logger.instance.debug("getSubtitlesFiltered: found KEYWORD match: "
              + subtitle.getFilename());
          addToFoundSubtitleList(listFilteredSubtitles, subtitle);
          foundKeywordMatch = true;
        }

        // check team match, use contains since some other info migth be
        // present!
        // Always check for team since some sites only give the team!
        if (!checkKeywordMatch
            && subtitle.getTeam().toLowerCase().contains(videoFile.getTeam().toLowerCase())) {
          subtitle.setSubtitleMatchType(SubtitleMatchType.TEAM);
          Logger.instance.debug("getSubtitlesFiltered: found KEYWORD based TEAM match: "
              + subtitle.getFilename());
          addToFoundSubtitleList(listFilteredSubtitles, subtitle);
          foundKeywordMatch = true;
        }
      }

      if (!foundKeywordMatch && videoFile.getPath() != null && videoFile.getFilename() != null) {
        // check keywords based on filesize if no keywords found in
        // file.
        keywordsFile =
            VideoFileParser.getQualityKeyword(videoFile.getPath().getAbsolutePath()
                + videoFile.getFilename());
        if (keywordsFile.equalsIgnoreCase("")) {
          long size =
              (new File(videoFile.getPath(), videoFile.getFilename())).length() / 1024 / 1024;
          if (size < 400) {
            keywordsFile = "dvdrip xvid hdtv";
          } else if (size < 1200) {
            keywordsFile = "720p hdtv";
          } else if (size < 1600) {
            keywordsFile = "web dl";
          }
        }

        for (Subtitle subtitle : listFoundSubtitles) {
          boolean checkKeywordMatch = checkKeywordSubtitleMatch(subtitle, keywordsFile);

          if (checkKeywordMatch) {
            subtitle.setSubtitleMatchType(SubtitleMatchType.KEYWORD);
            subtitle.setQuality(VideoFileParser.getQualityKeyword(subtitle.getFilename()));
            Logger.instance.debug("getSubtitlesFiltered: found KEYWORD based FILESIZE match: "
                + subtitle.getFilename());
            addToFoundSubtitleList(listFilteredSubtitles, subtitle);
            foundKeywordMatch = true;
          }
        }
      }
    }

    if (!foundKeywordMatch && !foundExactMatch && settings.isOptionNoResultShowItAll()) {
      for (Subtitle subtitle : listFoundSubtitles) {
        subtitle.setSubtitleMatchType(SubtitleMatchType.EVERYTHING);
        subtitle.setQuality(VideoFileParser.getQualityKeyword(subtitle.getFilename()));
        Logger.instance.debug("getSubtitlesFiltered: found EVERYTHING match: "
            + subtitle.getFilename());
        addToFoundSubtitleList(listFilteredSubtitles, subtitle);
      }
    }
    return listFilteredSubtitles;
  }

  private void addToFoundSubtitleList(List<Subtitle> listFilteredSubtitles, Subtitle subtitle) {
    for (Subtitle sub : listFilteredSubtitles) {
      if (sub.getDownloadlink().equals(subtitle.getDownloadlink())) return;
    }
    listFilteredSubtitles.add(subtitle);
  }

  private boolean checkKeywordSubtitleMatch(Subtitle subtitle, String keywordsFile) {
    String keywordsSub = VideoFileParser.getQualityKeyword(subtitle.getFilename());

    boolean foundKeywordMatch = false;
    if (keywordsFile.equalsIgnoreCase(keywordsSub)) {
      foundKeywordMatch = true;
    } else {
      foundKeywordMatch = keywordCheck(keywordsFile, keywordsSub);
    }
    return foundKeywordMatch;
  }

  private boolean keywordCheck(String videoFileName, String subFileName) {
    Logger.instance.trace("SubtitleControl", "keywordCheck", "");
    boolean foundKeywordMatch = false;

    videoFileName = videoFileName.toLowerCase();
    subFileName = subFileName.toLowerCase();

    if (videoFileName.contains("dl") && subFileName.contains("dl")
        && videoFileName.contains("720p") && subFileName.contains("720p")
        && videoFileName.contains("web") && subFileName.contains("web")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("720p") && subFileName.contains("720p")
        && videoFileName.contains("web") && subFileName.contains("web")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("dl") && subFileName.contains("dl")
        && videoFileName.contains("1080p") && subFileName.contains("1080p")
        && videoFileName.contains("web") && subFileName.contains("web")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("1080p") && subFileName.contains("1080p")
        && videoFileName.contains("web") && subFileName.contains("web")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("dl") && subFileName.contains("dl")
        && videoFileName.contains("web") && subFileName.contains("web")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("hdtv") && subFileName.contains("hdtv")
        && videoFileName.contains("720p") && subFileName.contains("720p")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("1080p") && subFileName.contains("1080p")
        && videoFileName.contains("bluray") && subFileName.contains("bluray")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("720p") && subFileName.contains("720p")
        && videoFileName.contains("bluray") && subFileName.contains("bluray")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("x264") && subFileName.contains("x264")
        && videoFileName.contains("bluray") && subFileName.contains("bluray")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("xvid") && subFileName.contains("xvid")
        && videoFileName.contains("dvdrip") && subFileName.contains("dvdrip")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("xvid") && subFileName.contains("xvid")
        && videoFileName.contains("hdtv") && subFileName.contains("hdtv")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("720p") && subFileName.contains("720p")
        && videoFileName.contains("brrip") && subFileName.contains("brrip")
        && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("ts") && subFileName.contains("ts")
        && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("bdrip") && subFileName.contains("bdrip")
        && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("480p") && subFileName.contains("480p")
        && videoFileName.contains("brrip") && subFileName.contains("brrip")
        && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("720p") && subFileName.contains("720p")
        && videoFileName.contains("brrip") && subFileName.contains("brrip")
        && videoFileName.contains("x264") && subFileName.contains("x264")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("dvdscreener") && subFileName.contains("dvdscreener")
        && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("r5") && subFileName.contains("r5")
        && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("cam") && subFileName.contains("cam")
        && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("hdtv") && subFileName.contains("hdtv")
        && videoFileName.contains("x264") && subFileName.contains("x264")) {
      foundKeywordMatch = true;
    } else if (videoFileName.contains("dvdrip") && subFileName.contains("dvdrip")) {
      foundKeywordMatch = true;
    }
    return foundKeywordMatch;
  }
}
