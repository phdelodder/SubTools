package org.lodder.subtools.sublibrary.data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.privateRepo.PrivateRepoIndex;
import org.lodder.subtools.sublibrary.privateRepo.model.IndexSubtitle;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.http.DropBoxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateRepo {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrivateRepo.class);

  private List<IndexSubtitle> index = new ArrayList<IndexSubtitle>();
  private String indexUrl = "/Ondertitels/PrivateRepo/index";
  private String indexVersionUrl = "/Ondertitels/PrivateRepo/index.version";
  private static PrivateRepo privateRepo;
  private final File path = new File(System.getProperty("user.home"), ".MultiSubDownloader");
  private final File rVersion = new File(path, "rVersion");
  private final File rIndex = new File(path, "rIndex");

  @SuppressWarnings("unused")
  PrivateRepo() {
    int localVersion = 0;
    String strIndex = "";
    int indexVersion = 0;

    if (!path.exists()) {
      boolean isCreated = path.mkdir();
    }

    try {
      if (rVersion.exists()) localVersion = Integer.parseInt(Files.read(rVersion).trim());
      indexVersion =
          Integer.parseInt(DropBoxClient.getDropBoxClient().getFile(indexVersionUrl).trim());
      if (indexVersion > localVersion || !rIndex.exists()) {
        strIndex = DropBoxClient.getDropBoxClient().getFile(indexUrl);
        Files.write(rIndex, strIndex);
        Files.write(rVersion, Integer.toString(indexVersion).trim());
      } else {
        strIndex = Files.read(rIndex);
      }
    } catch (Exception e) {
      LOGGER.error("Unable to get latest version number", e);
    }
    index = PrivateRepoIndex.getIndex(strIndex);
  }

  public static PrivateRepo getPrivateRepo() {
    if (privateRepo == null) privateRepo = new PrivateRepo();
    return privateRepo;
  }

  public List<Subtitle> searchSubtitles(TvRelease tvRelease, String languageCode)
      throws UnsupportedEncodingException {
    List<Subtitle> results = new ArrayList<Subtitle>();
    for (IndexSubtitle indexSubtitle : index) {
      if (indexSubtitle.getVideoType() == tvRelease.getVideoType()) {
        if (indexSubtitle.getTvdbid() == tvRelease.getTvdbid()) {
          if (indexSubtitle.getSeason() == tvRelease.getSeason()
              && indexSubtitle.getEpisode() == tvRelease.getEpisodeNumbers().get(0)) {
            if (indexSubtitle.getLanguage().equalsIgnoreCase(languageCode)) {
              String location =
                  "/" + indexSubtitle.getName() + "/" + indexSubtitle.getSeason() + "/"
                      + indexSubtitle.getEpisode() + "/" + indexSubtitle.getLanguage() + "/"
                      + PrivateRepoIndex.getFullFilename(indexSubtitle);

              Subtitle tempSub =
                  new Subtitle(Subtitle.SubtitleSource.LOCAL, indexSubtitle.getFilename(),
                      location, indexSubtitle.getLanguage(),
                      ReleaseParser.getQualityKeyword(indexSubtitle.getFilename()),
                      SubtitleMatchType.EVERYTHING, ReleaseParser.extractReleasegroup(
                          indexSubtitle.getFilename(),
                          FilenameUtils.isExtension(indexSubtitle.getFilename(), "srt")), "", false);
              results.add(tempSub);
            }
          }
        }
      }
    }
    return results;
  }

  public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String languageCode) {
    List<Subtitle> results = new ArrayList<Subtitle>();
    for (IndexSubtitle indexSubtitle : index) {
      if (indexSubtitle.getVideoType() == movieRelease.getVideoType()) {
        if (indexSubtitle.getImdbid() == movieRelease.getImdbid()) {
          if (indexSubtitle.getYear() == movieRelease.getYear()) {
            if (indexSubtitle.getLanguage().equalsIgnoreCase(languageCode)) {
              String location =
                  "/movies/" + indexSubtitle.getName() + " " + indexSubtitle.getYear() + "/"
                      + indexSubtitle.getLanguage() + "/"
                      + PrivateRepoIndex.getFullFilename(indexSubtitle);

              Subtitle tempSub =
                  new Subtitle(Subtitle.SubtitleSource.LOCAL, indexSubtitle.getFilename(),
                      location, indexSubtitle.getLanguage(), "", SubtitleMatchType.EVERYTHING,
                      ReleaseParser.extractReleasegroup(indexSubtitle.getFilename(),
                          FilenameUtils.isExtension(indexSubtitle.getFilename(), "srt")), "", false);
              results.add(tempSub);
            }
          }
        }
      }
    }
    return results;
  }
}
