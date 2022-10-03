/**
 *
 */
package org.lodder.subtools.sublibrary.settings.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.util.OptionalExtension;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@Getter
@ExtensionMethod({ OptionalExtension.class })
public class TvdbMappings {

    private final Map<Integer, TvdbMapping> serieMappings = new HashMap<>();

    private static final String CACHE_KEY_PREFIX = "TVDB-SerieId-";

    private TvdbMappings() {
        // hide constructor
    }

    public static List<TvdbMapping> getPersistedTvdbMappings(Manager manager) {
        return manager.getValuesBuilder()
                .keyFilter(k -> k.startsWith(CACHE_KEY_PREFIX))
                .cacheType(CacheType.DISK)
                .valueType(TvdbMapping.class)
                .get().stream().map(Pair::getValue).toList();
    }

    public static void removeTvdbMapping(Manager manager, String name) {
        manager.getRemoveCacheValueBuilder()
                .key(CACHE_KEY_PREFIX + name)
                .cacheType(CacheType.DISK)
                .remove();
    }

    public static void persistTvdbMapping(Manager manager, String name, int tvdbId) {
        persistTvdbMapping(manager, new TvdbMapping(tvdbId, name));
    }

    public static void persistTvdbMapping(Manager manager, TvdbMapping tvdbMapping) {
        manager.getStoreValueBuilder()
                .key(CACHE_KEY_PREFIX + tvdbMapping.getName())
                .cacheType(CacheType.DISK)
                .value(tvdbMapping)
                .store();
    }
}
