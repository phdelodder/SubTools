package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subsmax.JSubsMaxApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subsmax.model.SubMaxSubtitleDescriptor;
import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSubsMaxAdapter implements JSubAdapter, SubtitleProvider {

  private static JSubsMaxApi jsmapi;
  private static final Logger LOGGER = LoggerFactory.getLogger(JPodnapisiAdapter.class);

  public JSubsMaxAdapter(Manager manager) {
    try {
      if (jsmapi == null) {
        jsmapi = new JSubsMaxApi(manager);
      }
    } catch (Exception e) {
      LOGGER.error("API JSubsMax INIT", e);
    }
  }

  @Override
  public String getName() {
    return "SubsMax";
  }

  @Override
  public List<Subtitle> search(Release release, String languageCode) {
    if (release instanceof MovieRelease) {
      return this.searchSubtitles((MovieRelease) release, languageCode);
    } else if (release instanceof TvRelease) {
      return this.searchSubtitles((TvRelease) release, languageCode);
    }
    return new ArrayList<Subtitle>();
  }

  @Override
  public List<Subtitle> searchSubtitles(TvRelease tvRelease, String... sublanguageids) {
    String showName = "";
    if (tvRelease.getOriginalShowName().length() > 0) {
      showName = tvRelease.getOriginalShowName();
    } else {
      showName = tvRelease.getShow();
    }

    List<SubMaxSubtitleDescriptor> lSubtitles =
        jsmapi.searchSubtitles(showName, tvRelease.getSeason(), tvRelease.getEpisodeNumbers()
            .get(0), sublanguageids[0]);

    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();

    for (SubMaxSubtitleDescriptor sub : lSubtitles) {
      listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.SUBSMAX, sub.getFilename(), sub
          .getLink(), sublanguageids[0], ReleaseParser.getQualityKeyword(sub.getFilename()),
          SubtitleMatchType.EVERYTHING, ReleaseParser.extractReleasegroup(sub.getFilename(),
              FilenameUtils.isExtension(sub.getFilename(), "srt")), "", false));
    }

    return listFoundSubtitles;
  }

  @Override
  public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageids) {
    // TODO Auto-generated method stub
    return null;
  }

}
