package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.SubsceneApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubsceneSubtitleDescriptor;
import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSubsceneAdapter implements JSubAdapter, SubtitleProvider {

    private static SubsceneApi api;

    private static final Logger LOGGER = LoggerFactory.getLogger(JSubsceneAdapter.class);

    public JSubsceneAdapter(Manager manager) {
        if (api == null) {
            api = new SubsceneApi(manager);
        }
    }

    @Override
    public String getName() {
        return "Subscene";
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
    public List<Subtitle> searchSubtitles(TvRelease release, String... sublanguageids) {

        List<SubsceneSubtitleDescriptor> subtilteDescriptors = new ArrayList<>();
        try {
            if (release.getShow().length() > 0) {
                subtilteDescriptors = api.getSubtilteDescriptors(release.getShow(), release.getSeason());
            }
            if (subtilteDescriptors.isEmpty() && release.getOriginalShowName().length() > 0) {
                subtilteDescriptors = api.getSubtilteDescriptors(release.getOriginalShowName(), release.getSeason());
            }
        } catch (SubsceneException e) {
            LOGGER.error("API JSubscene searchSubtitles using title ", e);
        }
        return subtilteDescriptors.stream()
                .peek(sub -> {
                    var l = switch (sub.getLanguage()) {
                        case "Dutch" -> sub.setLanguage("nl");
                        case "English" -> sub.setLanguage("en");
                        default -> null;
                    };
                })
                .filter(sub -> sublanguageids[0].equals(sub.getLanguage()))
                .filter(sub -> sub.getName().contains(getSeasonEpisodeString(release.getSeason(), release.getEpisodeNumbers().get(0))))
                .map(sub -> new Subtitle(
                        Subtitle.SubtitleSource.SUBSCENE,
                        StringUtils.removeIllegalFilenameChars(sub.getName()),
                        sub.getUrlSupplier(),
                        sub.getLanguage(),
                        ReleaseParser.getQualityKeyword(sub.getName()),
                        SubtitleMatchType.EVERYTHING,
                        ReleaseParser.extractReleasegroup(sub.getName(), false),
                        sub.getUploader(),
                        sub.isHearingImpaired()))
                .toList();
    }

    private String getSeasonEpisodeString(int season, int episode) {
        return "S" + org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(season), 2, "0") + "E"
                + org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(episode), 2, "0");
    }

    @Override
    public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageids) {
        // TODO implement this
        return new ArrayList<>();
    }

}
