package org.lodder.subtools.sublibrary.settings.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TvdbMapping {

    private final String name;
    private final Set<String> alternativeNames = new HashSet<>();

    public TvdbMapping addAlternativename(String name) {
        if (StringUtils.isNotBlank(name)) {
            alternativeNames.add(name);
        }
        return this;
    }

    public boolean matches(String name) {
        return equals(format(this.name), format(name)) || alternativeNames.stream().anyMatch(n -> equals(n, name));
    }

    private boolean equals(String name, String name2) {
        return StringUtils.equals(format(name), format(name2));
    }

    private String format(String name) {
        return name != null ? name.replaceAll("[^A-Za-z]", "") : name;
    }
}
