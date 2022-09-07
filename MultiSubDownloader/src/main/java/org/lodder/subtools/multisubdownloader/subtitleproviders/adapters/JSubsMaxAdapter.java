package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subsmax.JSubsMaxApi;
import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
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
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.SUBSMAX;
    }

    @Override
    public List<Subtitle> search(Release release, String languageCode) {
        if (release instanceof MovieRelease movieRelease) {
            return this.searchSubtitles(movieRelease, languageCode);
        } else if (release instanceof TvRelease tvRelease) {
            return this.searchSubtitles(tvRelease, languageCode);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Subtitle> searchSubtitles(TvRelease tvRelease, String... sublanguageids) {
        String showName = tvRelease.getOriginalShowName().length() > 0 ? tvRelease.getOriginalShowName() : tvRelease.getShow();

        return jsmapi.searchSubtitles(showName, tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0), sublanguageids[0]).stream()
                .map(sub -> Subtitle.downloadSource(sub.getLink())
                        .subtitleSource(getSubtitleSource())
                        .fileName(sub.getFilename())
                        .languageCode(sublanguageids[0])
                        .quality(ReleaseParser.getQualityKeyword(sub.getFilename()))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(sub.getFilename(), FilenameUtils.isExtension(sub.getFilename(), "srt")))
                        .uploader("")
                        .hearingImpaired(false))
                .collect(Collectors.toList());
    }

    @Override
    public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageids) {
        // TODO Auto-generated method stub
        return null;
    }

}
