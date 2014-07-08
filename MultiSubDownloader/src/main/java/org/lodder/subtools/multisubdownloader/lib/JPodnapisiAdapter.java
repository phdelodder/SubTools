package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.control.VideoFileParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.subtitlesource.opensubtitles.OpenSubtitlesHasher;
import org.lodder.subtools.sublibrary.subtitlesource.podnapisi.JPodnapisiApi;
import org.lodder.subtools.sublibrary.subtitlesource.podnapisi.model.PodnapisiSubtitleDescriptor;

public class JPodnapisiAdapter implements JSubAdapter {

  private static JPodnapisiApi jpapi;

  public JPodnapisiAdapter() {
    try {
      if (jpapi == null) jpapi = new JPodnapisiApi("JBierSubDownloader");
    } catch (Exception e) {
      Logger.instance.error("API PODNAPISI INIT: " + e.getCause());
    }
  }

  @Override
  public List<Subtitle> searchSubtitles(MovieFile movieFile, String... sublanguageid) {
    List<PodnapisiSubtitleDescriptor> lSubtitles = new ArrayList<PodnapisiSubtitleDescriptor>();
    if (!movieFile.getFilename().equals("")) {
      File file = new File(movieFile.getPath(), movieFile.getFilename());
      if (file.exists())
        try {
          lSubtitles =
              jpapi.searchSubtitles(new String[] {OpenSubtitlesHasher.computeHash(file)},
                  sublanguageid[0]);
        } catch (Exception e) {
          Logger.instance.error("API PODNAPISI searchSubtitles using file hash: " + e);
        }
    }
    if (lSubtitles.size() == 0) {
      try {
        lSubtitles.addAll(jpapi.searchSubtitles(movieFile.getTitle(), movieFile.getYear(), 0, 0,
            sublanguageid[0]));
      } catch (Exception e) {
        Logger.instance.error("API PODNAPISI searchSubtitles using title: " + e);
      }
    }
    return buildListSubtitles(sublanguageid[0], lSubtitles);
  }

  @Override
  public List<Subtitle> searchSubtitles(EpisodeFile episodeFile, String... sublanguageid) {
    List<PodnapisiSubtitleDescriptor> lSubtitles = new ArrayList<PodnapisiSubtitleDescriptor>();

    try {
      String showName = "";
      if (episodeFile.getOriginalShowName().length() > 0) {
        showName = episodeFile.getOriginalShowName();
      } else {
        showName = episodeFile.getShow();
      }

      if (showName.length() > 0) {
        for (int episode : episodeFile.getEpisodeNumbers()) {
          lSubtitles =
              jpapi
                  .searchSubtitles(showName, 0, episodeFile.getSeason(), episode, sublanguageid[0]);
        }
      }
    } catch (Exception e) {
      Logger.instance.error("API PODNAPISI searchSubtitles: " + e);
    }
    return buildListSubtitles(sublanguageid[0], lSubtitles);
  }

  private List<Subtitle> buildListSubtitles(String sublanguageid,
      List<PodnapisiSubtitleDescriptor> lSubtitles) {
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    for (PodnapisiSubtitleDescriptor ossd : lSubtitles) {
      if (!ossd.getReleaseString().equals("")) {
        final String downloadlink = getDownloadLink(ossd.getSubtitleId());
        if (downloadlink != null) {
          for (String release : ossd.getReleaseString().split(" ")) {
            listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.PODNAPISI, release,
                downloadlink, sublanguageid, "", SubtitleMatchType.EVERYTHING, VideoFileParser
                    .extractTeam(release), ossd.getUploaderName(), ossd.getFlagsString().equals(
                    "nhu")));
          }
        }
      }
    }
    return listFoundSubtitles;
  }

  private String getDownloadLink(String subtitleId) {
    try {
      return jpapi.downloadUrl(subtitleId);
    } catch (Exception e) {
      Logger.instance.error("API PODNAPISI getdownloadlink: " + e);
    }
    return null;
  }
}
