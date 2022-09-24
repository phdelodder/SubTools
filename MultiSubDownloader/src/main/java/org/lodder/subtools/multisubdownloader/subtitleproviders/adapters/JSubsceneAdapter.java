package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.SubsceneApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubsceneSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSubsceneAdapter implements SubtitleProvider {

    private static SubsceneApi api;

    private static final Logger LOGGER = LoggerFactory.getLogger(JSubsceneAdapter.class);

    public JSubsceneAdapter(Manager manager) {
        if (api == null) {
            api = new SubsceneApi(manager);
        }
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.SUBSCENE;
    }

    @Override
    public Set<Subtitle> searchSubtitles(TvRelease release, Language language) {

        List<SubsceneSubtitleDescriptor> subtilteDescriptors = new ArrayList<>();
        try {
            if (release.getName().length() > 0) {
                subtilteDescriptors = api.getSubtilteDescriptors(release.getName(), release.getSeason(), language);
            }
        } catch (SubsceneException e) {
            LOGGER.error("API Subscene searchSubtitles for serie [%s] and season [%s] (%s)"
                    .formatted(release.getName(), release.getSeason(), e.getMessage()), e);
        }
        try {
            if (subtilteDescriptors.isEmpty() && release.getOriginalShowName().length() > 0) {
                subtilteDescriptors = api.getSubtilteDescriptors(release.getOriginalShowName(), release.getSeason(), language);
            }
        } catch (SubsceneException e) {
            LOGGER.error("API Subscene searchSubtitles for serie [%s] and season [%s] (%s)"
                    .formatted(release.getOriginalShowName(), release.getSeason(), e.getMessage()), e);
        }
        return subtilteDescriptors.stream()
                .filter(sub -> language == sub.getLanguage())
                .filter(sub -> sub.getName().contains(getSeasonEpisodeString(release.getSeason(), release.getEpisodeNumbers().get(0))))
                .map(sub -> Subtitle.downloadSource(sub.getUrlSupplier())
                        .subtitleSource(getSubtitleSource())
                        .fileName(StringUtil.removeIllegalFilenameChars(sub.getName()))
                        .language(sub.getLanguage())
                        .quality(ReleaseParser.getQualityKeyword(sub.getName()))
                        .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                        .releaseGroup(ReleaseParser.extractReleasegroup(sub.getName(), false))
                        .uploader(sub.getUploader())
                        .hearingImpaired(sub.isHearingImpaired()))
                .collect(Collectors.toSet());
    }

    private String getSeasonEpisodeString(int season, int episode) {
        return "S" + org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(season), 2, "0") + "E"
                + org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(episode), 2, "0");
    }

    @Override
    public Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language) {
        // TODO implement this
        return Set.of();
    }

}
