package org.lodder.subtools.multisubdownloader.lib.control;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.lib.control.subtitles.Filtering;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.LocalFileRepo;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.ScoreCalculator;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.SortWeight;
import org.lodder.subtools.multisubdownloader.settings.model.SearchSubtitlePriority;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JAddic7edAdapter;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JOpenSubAdapter;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JPodnapisiAdapter;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JSubsMaxAdapter;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JTVsubtitlesAdapter;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;

public class SubtitleControl {

  private final JOpenSubAdapter jOpenSubAdapter;
  private final JPodnapisiAdapter jPodnapisiAdapter;
  private final JAddic7edAdapter jAddic7edAdapter;
  private final JTVsubtitlesAdapter jTVSubtitlesAdapter;
  private final JSubsMaxAdapter jSubsMaxAdapter;
  private final Settings settings;
  private final Filtering filtering;
  private final LocalFileRepo localFileRepo;

  public SubtitleControl(Settings settings) {
    this.settings = settings;
    jOpenSubAdapter = new JOpenSubAdapter();
    jPodnapisiAdapter = new JPodnapisiAdapter();
    jAddic7edAdapter =
        new JAddic7edAdapter(settings.isLoginAddic7edEnabled(),
            settings.getLoginAddic7edUsername(), settings.getLoginAddic7edPassword());
    jTVSubtitlesAdapter = new JTVsubtitlesAdapter();
    jSubsMaxAdapter = new JSubsMaxAdapter();
    filtering = new Filtering(settings);
    localFileRepo= new LocalFileRepo(settings);
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
            listSourceSubtitles.addAll(localFileRepo.search(tvRelease, languagecode[0]));
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
        // After each search source, check if matching subtitles have been found! Only works if
        // exact or keyword is checked!
        if (settings.isOptionSubtitleExactMatch() || settings.isOptionSubtitleKeywordMatch()) {
          List<Subtitle> listResultFiltered =
              filtering.getFiltered(listSourceSubtitles, tvRelease);
          if (listResultFiltered.size() > 0) {
            calculateScore(listSourceSubtitles, calculator);
            return listResultFiltered;
          }
        }
        listFoundSubtitles.addAll(listSourceSubtitles);
      }
    }

    calculateScore(listFoundSubtitles, calculator);
    return filtering.getFiltered(listFoundSubtitles, tvRelease);
  }

  public List<Subtitle> getSubtitles(MovieRelease movieRelease, String... languagecode) {
    Logger.instance.trace("SubtitleControl", "getSubtitles", "Movie");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    ScoreCalculator calculator = createScoreCalculator(movieRelease);
    listFoundSubtitles.addAll(jOpenSubAdapter.searchSubtitles(movieRelease, languagecode));
    listFoundSubtitles.addAll(jPodnapisiAdapter.searchSubtitles(movieRelease, languagecode[0]));
    listFoundSubtitles.addAll(localFileRepo.search(movieRelease, languagecode[0]));
    calculateScore(listFoundSubtitles, calculator);
    return filtering.getFiltered(listFoundSubtitles, movieRelease);
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
