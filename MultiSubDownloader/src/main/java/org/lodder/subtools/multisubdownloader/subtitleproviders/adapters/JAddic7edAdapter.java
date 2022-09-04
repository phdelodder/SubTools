package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.JAddic7edApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception.Addic7edException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model.Addic7edSubtitleDescriptor;
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

public class JAddic7edAdapter implements JSubAdapter, SubtitleProvider {

    private static JAddic7edApi jaapi;

    private static final Logger LOGGER = LoggerFactory.getLogger(JAddic7edAdapter.class);

    public JAddic7edAdapter(boolean isLoginEnabled, String username, String password, boolean speedy, Manager manager) {
        try {
            if (jaapi == null) {
                if (isLoginEnabled) {
                    jaapi = new JAddic7edApi(username, password, speedy, manager);
                } else {
                    jaapi = new JAddic7edApi(speedy, manager);
                }
            }
        } catch (Addic7edException e) {
            LOGGER.error("API JAddic7ed INIT: ", e);
        }
    }

    @Override
    public String getName() {
        return "Addic7ed";
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
        List<Addic7edSubtitleDescriptor> lSubtitles = new ArrayList<>();
        List<Subtitle> listFoundSubtitles = new ArrayList<>();
        try {
            String showName = "";

            if (tvRelease.getShow().length() > 0) {
                showName = jaapi.searchSerieName(tvRelease.getShow());
            }

            if ((showName.length() == 0) && (tvRelease.getOriginalShowName().length() > 0)) {
                showName = jaapi.searchSerieName(tvRelease.getOriginalShowName());
            }

            if (showName.length() > 0) {
                lSubtitles.addAll(jaapi.searchSubtitles(showName, tvRelease.getSeason(), tvRelease
                        .getEpisodeNumbers().get(0), tvRelease.getTitle()));
            }

        } catch (Addic7edException e) {
            LOGGER.error("API JAddic7ed searchSubtitles using title ", e);
        }
        for (Addic7edSubtitleDescriptor sub : lSubtitles) {
            if ("Dutch".equals(sub.getLanguage())) {
                sub.setLanguage("nl");
            }
            if ("English".equals(sub.getLanguage())) {
                sub.setLanguage("en");
            }
            if (sublanguageids[0].equals(sub.getLanguage())) {
                listFoundSubtitles.add(new Subtitle(
                        Subtitle.SubtitleSource.ADDIC7ED,
                        StringUtils.removeIllegalFilenameChars(sub.getTitel() + " " + sub.getVersion()), sub.getUrl(), sub.getLanguage(),
                        ReleaseParser.getQualityKeyword(sub.getTitel() + " " + sub.getVersion()),
                        SubtitleMatchType.EVERYTHING,
                        ReleaseParser.extractReleasegroup(sub.getTitel() + " " + sub.getVersion(),
                                FilenameUtils.isExtension(sub.getTitel() + " " + sub.getVersion(), "srt")),
                        sub.getUploader(),
                        sub.isHearingImpaired()));
            }
        }
        return listFoundSubtitles;
    }

    @Override
    public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageids) {
        // TODO Auto-generated method stub
        return null;
    }

}
