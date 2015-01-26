package org.lodder.subtools.multisubdownloader.lib.control;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.lib.JAddic7edAdapter;
import org.lodder.subtools.multisubdownloader.lib.JOpenSubAdapter;
import org.lodder.subtools.multisubdownloader.lib.JPodnapisiAdapter;
import org.lodder.subtools.multisubdownloader.lib.JSubsMaxAdapter;
import org.lodder.subtools.multisubdownloader.lib.JTVsubtitlesAdapter;
import org.lodder.subtools.multisubdownloader.lib.PrivateRepo;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filtering.Filter;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.ScoreCalculator;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.SortWeight;
import org.lodder.subtools.multisubdownloader.settings.model.SearchSubtitlePriority;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.util.Utils;

public class SubtitleControl {

  private final JOpenSubAdapter jOpenSubAdapter;
  private final JPodnapisiAdapter jPodnapisiAdapter;
  private final JAddic7edAdapter jAddic7edAdapter;
  private final JTVsubtitlesAdapter jTVSubtitlesAdapter;
  private final JSubsMaxAdapter jSubsMaxAdapter;
  private final PrivateRepo privateRepo;
  private final Settings settings;
  private final Filter filter;

  public SubtitleControl(Settings settings) {
    this.settings = settings;
    jOpenSubAdapter = new JOpenSubAdapter();
    jPodnapisiAdapter = new JPodnapisiAdapter();
    jAddic7edAdapter =
        new JAddic7edAdapter(settings.isLoginAddic7edEnabled(),
            settings.getLoginAddic7edUsername(), settings.getLoginAddic7edPassword());
    jTVSubtitlesAdapter = new JTVsubtitlesAdapter();
    privateRepo = PrivateRepo.getPrivateRepo();
    jSubsMaxAdapter = new JSubsMaxAdapter();
    filter = new Filter(settings);
  }

  public List<Subtitle> getSubtitles(TvRelease tvRelease, String... languagecode) {
    Logger.instance.trace("SubtitleControl", "getSubtitles", "Episode");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();

    ScoreCalculator calculator = createScoreCalculator(tvRelease);

    for (SearchSubtitlePriority searchSubtitlePriority : settings.getListSearchSubtitlePriority()) {
      List<Subtitle> listSourceSubtitles = new ArrayList<Subtitle>();
      switch (searchSubtitlePriority.getSubtitleSource()) {
        case ADDIC7ED:
          if (settings.isSerieSourceAddic7ed())
            listSourceSubtitles.addAll(jAddic7edAdapter.searchSubtitles(tvRelease, languagecode));
          break;
        case LOCAL:
          if (settings.isSerieSourceLocal())
            listSourceSubtitles.addAll(addLocalLibrary(tvRelease, languagecode[0]));
          break;
        case OPENSUBTITLES:
          if (settings.isSerieSourceOpensubtitles())
            listSourceSubtitles.addAll(jOpenSubAdapter.searchSubtitles(tvRelease, languagecode));
          break;
        case PODNAPISI:
          if (settings.isSerieSourcePodnapisi())
            listSourceSubtitles.addAll(jPodnapisiAdapter
                .searchSubtitles(tvRelease, languagecode[0]));
          break;
        case PRIVATEREPO:
          if (settings.isSerieSourcePrivateRepo()) {
            try {
              listSourceSubtitles.addAll(privateRepo.searchSubtitles(tvRelease, languagecode[0]));
            } catch (UnsupportedEncodingException e) {
              Logger.instance.error(Logger.stack2String(e));
            }
          }
          break;
        case TVSUBTITLES:
          if (settings.isSerieSourceTvSubtitles())
            listSourceSubtitles.addAll(jTVSubtitlesAdapter.searchSubtitles(tvRelease,
                languagecode[0]));
          break;
        case SUBSMAX:
          if (settings.isSerieSourceSubsMax())
            listSourceSubtitles.addAll(jSubsMaxAdapter.searchSubtitles(tvRelease, languagecode[0]));
          break;
        default:
          break;
      }

      if (listSourceSubtitles.size() > 0) {
        calculateScore(listSourceSubtitles, calculator);
        // After each search source, check if matching subtitles have been found! Only works if
        // exact or keyword is checked!
        if (settings.isOptionSubtitleExactMatch() || settings.isOptionSubtitleKeywordMatch()) {
          List<Subtitle> listResultFiltered =
              filter.getFiltered(listSourceSubtitles, tvRelease, false);
          if (listResultFiltered.size() > 0) return listResultFiltered;
        }
        listFoundSubtitles.addAll(listSourceSubtitles);
      }
    }

    return filter.getFiltered(listFoundSubtitles, tvRelease, true);
  }

  public List<Subtitle> getSubtitles(MovieRelease movieRelease, String... languagecode) {
    Logger.instance.trace("SubtitleControl", "getSubtitles", "Movie");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    ScoreCalculator calculator = createScoreCalculator(movieRelease);
    listFoundSubtitles.addAll(privateRepo.searchSubtitles(movieRelease, languagecode[0]));
    listFoundSubtitles.addAll(jOpenSubAdapter.searchSubtitles(movieRelease, languagecode));
    listFoundSubtitles.addAll(jPodnapisiAdapter.searchSubtitles(movieRelease, languagecode[0]));
    calculateScore(listFoundSubtitles, calculator);
    return filter.getFiltered(listFoundSubtitles, movieRelease, true);
  }

  private List<Subtitle> addLocalLibrary(TvRelease tvRelease, String languagecode) {
    Logger.instance.trace("SubtitleControl", "addLocalLibrary", "");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    List<File> possibleSubtitles = new ArrayList<File>();
    ReleaseParser vfp = new ReleaseParser();

    String filter = "";
    if (tvRelease.getOriginalShowName().length() > 0) {
      filter = tvRelease.getOriginalShowName().replaceAll("[^A-Za-z]", "").trim();
    } else {
      filter = tvRelease.getShow().replaceAll("[^A-Za-z]", "").trim();
    }

    for (File local : settings.getLocalSourcesFolders()) {
      possibleSubtitles.addAll(getAllSubtitlesFiles(local, filter));
    }

    for (File fileSub : possibleSubtitles) {
      try {
        Release release = vfp.parse(fileSub, new File(fileSub.getPath()));
        if (release.getVideoType() == VideoType.EPISODE) {

          if (((TvRelease) release).getSeason() == tvRelease.getSeason()
              && Utils.containsAll(((TvRelease) release).getEpisodeNumbers(),
                  tvRelease.getEpisodeNumbers())) {
            TvReleaseControl epCtrl = new TvReleaseControl((TvRelease) release, settings);
            epCtrl.process(settings.getMappingSettings().getMappingList());
            if (((TvRelease) release).getTvdbid() == tvRelease.getTvdbid()) {
              String detectedLang = DetectLanguage.execute(fileSub);
              if (detectedLang.equals(languagecode)) {
                Logger.instance.debug("Local Sub found, adding " + fileSub.toString());
                listFoundSubtitles
                    .add(new Subtitle(Subtitle.SubtitleSource.LOCAL, fileSub.getName(), fileSub
                        .toString(), "", "", SubtitleMatchType.EVERYTHING, ReleaseParser
                        .extractTeam(fileSub.getName()), fileSub.getAbsolutePath(), false));
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

  protected ScoreCalculator createScoreCalculator(Release release) {
    SortWeight weights = new SortWeight(release, this.settings.getSortWeights());
    return new ScoreCalculator(weights);
  }

  protected void calculateScore(List<Subtitle> subtitles, ScoreCalculator calculator) {
    Logger.instance.trace("SubtitleControl", "calculateScore", "");
    for (Subtitle subtitle : subtitles) {
      int score = calculator.calculate(subtitle);
      Logger.instance.debug("Subtitle '" + subtitle.getFilename() + "' has a score of " + score);
      subtitle.setScore(score);
    }
  }

}
