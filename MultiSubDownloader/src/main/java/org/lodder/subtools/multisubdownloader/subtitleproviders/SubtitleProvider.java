package org.lodder.subtools.multisubdownloader.subtitleproviders;

import java.util.Optional;
import java.util.Set;

import manifold.ext.props.rt.api.val;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.slf4j.LoggerFactory;

public interface SubtitleProvider {

    @val Manager manager;
    @val SubtitleSource subtitleSource;
    @val String providerName;
    @val String name = subtitleSource.name;

    Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language);

    Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language);

    /**
     * Starts a search for subtitles
     *
     * @param release The release being searched for
     * @param language The language of the desired subtitles
     * @return The found subtitles
     */
    default Set<Subtitle> search(Release release, Language language) {
        try {
            return switch (release) {
                case MovieRelease movieRelease -> this.searchSubtitles(movieRelease, language);
                case TvRelease tvRelease -> this.searchSubtitles(tvRelease, language);
            };
        } catch (Exception e) {
            LoggerFactory.getLogger(SubtitleProvider.class)
                .error("Error in %s API: %s".formatted(name, e.getMessage()), e);
        }
        return Set.of();
    }

    default void clearCache() {
        manager.getCache(CacheType.DISK, k -> k.startsWith(providerName + "-")).clearExpiredCache();
    }

    <X extends Exception> Optional<SerieMapping> getProviderSerieId(TvRelease tvRelease) throws X;
}
