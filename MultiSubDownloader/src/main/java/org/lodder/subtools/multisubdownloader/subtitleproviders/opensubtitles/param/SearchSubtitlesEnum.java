package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SearchSubtitlesEnum {

    // exclude, include (default: exclude)
    AI_TRANSLATED("ai_translated"),

    // For Tvshows
    EPISODE_NUMBER("episode_number"),

    // exclude, include, only (default: include)
    FOREIGN_PARTS_ONLY("foreign_parts_only"),

    // include, exclude, only. (default: include)
    HEARING_IMPAIRED("hearing_impaired"),

    // ID of the movie or episode
    ID("id"),

    // IMDB ID of the movie or episode
    IMDB_ID("imdb_id"),

    // Language code(s), coma separated (en,fr)
    LANGUAGES("languages"),

    // exclude, include (default: exclude)
    MACHINE_TRANSLATED("machine_translated"),

    // Moviehash of the movie
    MOVIEHASH("moviehash"),

    // >= 16 characters
    // <= 16 characters
    // Match pattern: ^[a-f0-9]{16}$
    // include, only (default: include)
    MOVIEHASH_MATCH("moviehash_match"),

    // Order of the returned results, accept any of above fields
    ORDER_BY("order_by"),

    // Order direction of the returned results (asc,desc)
    ORDER_DIRECTION("order_direction"),

    // Results page to display
    PAGE("page"),

    // For Tvshows
    PARENT_FEATURE_ID("parent_feature_id"),

    // For Tvshows
    PARENT_IMDB_ID("parent_imdb_id"),

    // For Tvshows
    PARENT_TMDB_ID("parent_tmdb_id"),

    // file name or text search
    QUERY("query"),

    // For Tvshows
    SEASON_NUMBER("season_number"),

    // TMDB ID of the movie or episode
    TMDB_ID("tmdb_id"),

    // include, only (default: include)
    TRUSTED_SOURCES("trusted_sources"),

    // movie, episode or all, (default: all)
    TYPE("type"),

    // To be used alone - for user uploads listing
    USER_ID("user_id"),

    // Filter by movie/episode year
    YEAR("year");

    private final String paramName;
}
