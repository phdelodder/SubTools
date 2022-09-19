/**
 *
 */
package org.lodder.subtools.sublibrary.settings.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;

import com.pivovarit.function.ThrowingSupplier;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@Getter
@ExtensionMethod({ OptionalExtension.class })
public class TvdbMappings {

    private final Map<Integer, TvdbMapping> serieMappings = new HashMap<>();

    public boolean isEmpty() {
        return serieMappings.isEmpty();
    }

    public void setMappings(TvdbMappings tvdbMappings) {
        serieMappings.clear();
        tvdbMappings.forEach(serieMappings::put);
    }

    public void add(int tvdbId, TvdbMapping tvdbMapping) {
        serieMappings.put(tvdbId, tvdbMapping);
    }

    private OptionalInt getSerieId(String name) {
        return StringUtils.isBlank(name) ? OptionalInt.empty()
                : serieMappings.entrySet().stream().filter(e -> e.getValue().matches(name)).mapToInt(Entry::getKey).findAny();
    }

    public void forEach(BiConsumer<Integer, TvdbMapping> consumer) {
        serieMappings.entrySet().stream().forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    public <X extends Exception> OptionalInt setInfo(TvRelease tvRelease, ThrowingSupplier<OptionalInt, X> supplier) throws X {
        return getSerieId(tvRelease.getName())
                .orElseMap(() -> ((OptionalInt) supplier.get()).ifPresentDo(
                        id -> serieMappings.put(id, new TvdbMapping(tvRelease.getName()).addAlternativename(tvRelease.getOriginalShowName()))))
                .ifPresentDo(id -> {
                    tvRelease.setTvdbId(id);
                    tvRelease.setOriginalShowName(serieMappings.get(id).getName());
                });
        // TODO throw exception?
    }
}
