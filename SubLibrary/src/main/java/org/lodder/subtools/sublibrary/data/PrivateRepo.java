package org.lodder.subtools.sublibrary.data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.sublibrary.Language;
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

    private List<IndexSubtitle> index = new ArrayList<>();
    private final String indexUrl = "/Ondertitels/PrivateRepo/index";
    private final String indexVersionUrl = "/Ondertitels/PrivateRepo/index.version";
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
            if (rVersion.exists()) {
                localVersion = Integer.parseInt(Files.read(rVersion).trim());
            }
            indexVersion = Integer.parseInt(DropBoxClient.getDropBoxClient().getFile(indexVersionUrl).trim());
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
        if (privateRepo == null) {
            privateRepo = new PrivateRepo();
        }
        return privateRepo;
    }

    public List<Subtitle> searchSubtitles(TvRelease tvRelease, Language language)
            throws UnsupportedEncodingException {
        List<Subtitle> results = new ArrayList<>();
        for (IndexSubtitle indexSubtitle : index) {
            if ((indexSubtitle.getVideoType() == tvRelease.getVideoType()) && (indexSubtitle.getTvdbId() == tvRelease.getTvdbId())) {
                if (indexSubtitle.getSeason() == tvRelease.getSeason()
                        && indexSubtitle.getEpisode() == tvRelease.getEpisodeNumbers().get(0)) {
                    if (indexSubtitle.getLanguage() == language) {
                        File location =
                                new File(indexSubtitle.getName() + "/" + indexSubtitle.getSeason() + "/"
                                        + indexSubtitle.getEpisode() + "/" + indexSubtitle.getLanguage() + "/"
                                        + PrivateRepoIndex.getFullFilename(indexSubtitle));

                        Subtitle tempSub = Subtitle.downloadSource(location)
                                .subtitleSource(Subtitle.SubtitleSource.LOCAL)
                                .fileName(indexSubtitle.getFilename())
                                .language(indexSubtitle.getLanguage())
                                .quality(ReleaseParser.getQualityKeyword(indexSubtitle.getFilename()))
                                .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                                .releaseGroup(ReleaseParser.extractReleasegroup(indexSubtitle.getFilename(),
                                        FilenameUtils.isExtension(indexSubtitle.getFilename(), "srt")))
                                .uploader("")
                                .hearingImpaired(false);
                        results.add(tempSub);
                    }
                }
            }
        }
        return results;
    }

    public List<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language) {
        List<Subtitle> results = new ArrayList<>();
        for (IndexSubtitle indexSubtitle : index) {
            if ((indexSubtitle.getVideoType() == movieRelease.getVideoType()) && (indexSubtitle.getImdbId() == movieRelease.getImdbId())) {
                if (indexSubtitle.getYear() == movieRelease.getYear()) {
                    if (indexSubtitle.getLanguage() == language) {
                        String location =
                                "/movies/" + indexSubtitle.getName() + " " + indexSubtitle.getYear() + "/"
                                        + indexSubtitle.getLanguage() + "/"
                                        + PrivateRepoIndex.getFullFilename(indexSubtitle);
                        results.add(Subtitle.downloadSource(location)
                                .subtitleSource(Subtitle.SubtitleSource.LOCAL)
                                .fileName(indexSubtitle.getFilename())
                                .language(indexSubtitle.getLanguage())
                                .quality("")
                                .subtitleMatchType(SubtitleMatchType.EVERYTHING)
                                .releaseGroup(ReleaseParser.extractReleasegroup(indexSubtitle.getFilename(),
                                        FilenameUtils.isExtension(indexSubtitle.getFilename(), "srt")))
                                .uploader("")
                                .hearingImpaired(false));
                    }
                }
            }
        }
        return results;
    }
}
