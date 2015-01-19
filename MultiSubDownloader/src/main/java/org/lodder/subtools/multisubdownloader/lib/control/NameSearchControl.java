package org.lodder.subtools.multisubdownloader.lib.control;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class NameSearchControl {

  private final JTheTVDBAdapter jTheTVDBAdapter;
  private final Settings settings;
  private final SubtitleControl subtitleControl;

  public NameSearchControl(Settings settings) {
    subtitleControl = new SubtitleControl(settings);
    jTheTVDBAdapter = JTheTVDBAdapter.getAdapter();
    this.settings = settings;
  }

  public List<Subtitle> SearchSubtitles(String movie, String languagecode) {
    MovieRelease movieRelease = new MovieRelease();
    movieRelease.setTitle(movie);
    return subtitleControl.getSubtitles(movieRelease, languagecode);
  }

  @SuppressWarnings("serial")
  public List<Subtitle> SearchSubtitles(String serie, int season, final int episode,
      String languagecode) throws Exception {
    List<Subtitle> subs = new ArrayList<Subtitle>();
    TvRelease ep = new TvRelease();
    ep.setShow(serie);

    if (season > 0) {
      ep.setSeason(season);
      if (episode > 0) {
        ep.setEpisodeNumbers(new ArrayList<Integer>() {
          {
            add(episode);
          }
        });
        EpisodeFileControl epfctrl = new EpisodeFileControl(ep, this.settings);
        epfctrl.process(settings.getMappingSettings().getMappingList());
        subs.addAll(searchSubtitle(ep, languagecode));
      } else {
        TheTVDBSerie thetvdbserie = jTheTVDBAdapter.getSerie(ep);
        ep.setTvdbid(Integer.parseInt(thetvdbserie.getId()));
        List<TheTVDBEpisode> episodes =
            jTheTVDBAdapter.getAllEpisodes(ep.getTvdbid(), languagecode);
        for (TheTVDBEpisode tvdbEpisode : episodes) {
          if (tvdbEpisode.getSeasonNumber() == season) {
            subs.addAll(setTVDBAndSearchSubtitle(tvdbEpisode, ep, languagecode));
          }
        }
      }
    } else {
      List<TheTVDBEpisode> episodes = jTheTVDBAdapter.getAllEpisodes(ep.getTvdbid(), languagecode);
      for (TheTVDBEpisode tvdbEpisode : episodes) {
        subs.addAll(setTVDBAndSearchSubtitle(tvdbEpisode, ep, languagecode));
      }
    }
    return subs;
  }

  private List<Subtitle> setTVDBAndSearchSubtitle(TheTVDBEpisode tvdbEpisode, TvRelease ep,
      String languagecode) {
    List<Subtitle> subs = new ArrayList<Subtitle>();

    ep.setSeason(tvdbEpisode.getSeasonNumber());
    List<Integer> episodenumbers = new ArrayList<Integer>();
    episodenumbers.add(tvdbEpisode.getEpisodeNumber());
    ep.setEpisodeNumbers(episodenumbers);

    EpisodeFileControl epfc = new EpisodeFileControl(ep, settings);

    try {
      epfc.process(settings.getMappingSettings().getMappingList());
      subs.addAll(searchSubtitle((TvRelease) epfc.getVideoFile(), languagecode));
    } catch (VideoControlException e) {
      if (Logger.instance.getLogLevel().intValue() < Level.INFO.intValue()) {
        Logger.instance.error(Logger.stack2String(e));
      } else {
        Logger.instance.error(e.getMessage());
      }
    }

    return subs;
  }

  private List<Subtitle> searchSubtitle(TvRelease ep, String languagecode) {
    return subtitleControl.getSubtitles(ep, languagecode);
  }
}
