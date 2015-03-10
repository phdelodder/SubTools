package org.lodder.subtools.multisubdownloader.subtitleproviders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.lib.control.MovieReleaseControl;
import org.lodder.subtools.multisubdownloader.lib.control.TvReleaseControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Local implements SubtitleProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(Local.class);
  
  private Settings settings;
  private Manager manager;

  public Local(Settings settings, Manager manager) {
    this.settings = settings;
    this.manager = manager;
  }

  @Override
  public String getName() {
    return "Local";
  }

  @Override
  public List<Subtitle> search(Release release, String languageCode) {
    if (release instanceof MovieRelease) {
      return this.search((MovieRelease) release, languageCode);
    } else if (release instanceof TvRelease) {
      return this.search((TvRelease) release, languageCode);
    }
    return new ArrayList<Subtitle>();
  }

  private List<File> getPossibleSubtitles(String filter) {
    List<File> possibleSubtitles = new ArrayList<File>();
    for (File local : settings.getLocalSourcesFolders()) {
      possibleSubtitles.addAll(getAllSubtitlesFiles(local, filter));
    }

    return possibleSubtitles;
  }


  public List<Subtitle> search(TvRelease tvRelease, String languagecode) {
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    ReleaseParser vfp = new ReleaseParser();

    String filter = "";
    if (tvRelease.getOriginalShowName().length() > 0) {
      filter = tvRelease.getOriginalShowName().replaceAll("[^A-Za-z]", "").trim();
    } else {
      filter = tvRelease.getShow().replaceAll("[^A-Za-z]", "").trim();
    }

    for (File fileSub : getPossibleSubtitles(filter)) {
      try {
        Release release = vfp.parse(fileSub);
        if (release.getVideoType() == VideoType.EPISODE) {

          if (((TvRelease) release).getSeason() == tvRelease.getSeason()
              && Utils.containsAll(((TvRelease) release).getEpisodeNumbers(),
                  tvRelease.getEpisodeNumbers())) {
            TvReleaseControl epCtrl = new TvReleaseControl((TvRelease) release, settings, manager);
            epCtrl.process(settings.getMappingSettings().getMappingList());
            if (((TvRelease) release).getTvdbid() == tvRelease.getTvdbid()) {
              String detectedLang = DetectLanguage.execute(fileSub);
              if (detectedLang.equals(languagecode)) {
                LOGGER.debug("Local Sub found, adding [{}]", fileSub.toString());
                listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.LOCAL, fileSub
                    .getName(), fileSub.toString(), "", "", SubtitleMatchType.EVERYTHING,
                    ReleaseParser.extractReleasegroup(fileSub.getName()),
                    fileSub.getAbsolutePath(), false));
              }
            }
          }
        }
      } catch (Exception e) {
        if (LOGGER.isDebugEnabled() || LOGGER.isTraceEnabled()) {
          LOGGER.error(e.getMessage(), e);
        } else {
          LOGGER.error(e.getMessage());
        }
      }
    }

    return listFoundSubtitles;
  }

  public List<Subtitle> search(MovieRelease movieRelease, String languagecode) {
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    ReleaseParser releaseParser = new ReleaseParser();

    String filter = movieRelease.getTitle();

    for (File fileSub : getPossibleSubtitles(filter)) {
      try {
        Release release = releaseParser.parse(fileSub);
        if (release.getVideoType() == VideoType.MOVIE) {
          MovieReleaseControl movieCtrl = new MovieReleaseControl((MovieRelease) release, settings, manager);
          movieCtrl.process(settings.getMappingSettings().getMappingList());
          if (((MovieRelease) release).getImdbid() == movieRelease.getImdbid()) {
            String detectedLang = DetectLanguage.execute(fileSub);
            if (detectedLang.equals(languagecode)) {
              LOGGER.debug("Local Sub found, adding {}", fileSub.toString());
              listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.LOCAL, fileSub.getName(),
                  fileSub.toString(), "", "", SubtitleMatchType.EVERYTHING, ReleaseParser
                      .extractReleasegroup(fileSub.getName()), fileSub.getAbsolutePath(), false));
            }
          }
        }
      } catch (Exception e) {
        if (LOGGER.isDebugEnabled() || LOGGER.isTraceEnabled()) {
          LOGGER.error(e.getMessage(), e);
        } else {
          LOGGER.error(e.getMessage());
        }
      }
    }

    return listFoundSubtitles;
  }

  private List<File> getAllSubtitlesFiles(File dir, String filter) {
    final List<File> filelist = new ArrayList<File>();
    final File[] contents = dir.listFiles();
    if (contents != null) {
      for (final File file : contents) {
        if (file.isFile()) {
          if (file.getName().replaceAll("[^A-Za-z]", "").toLowerCase()
              .contains(filter.toLowerCase())
              && ReleaseParser.extractFileNameExtension(file.getName()).equals("srt")) {
            filelist.add(file);
          }
        } else {
          filelist.addAll(getAllSubtitlesFiles(file, filter));
        }
      }
    }
    return filelist;
  }
}
