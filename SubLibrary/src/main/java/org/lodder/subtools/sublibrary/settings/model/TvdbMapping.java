package org.lodder.subtools.sublibrary.settings.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class TvdbMapping implements Serializable {

    private static final long serialVersionUID = -5860458667584710122L;
    private final int id;
    private final String name;
    private final Set<String> alternativeNames = new HashSet<>();

    public TvdbMapping addAlternativename(String altName) {
        if (StringUtils.isNotBlank(altName) && !StringUtils.equals(name, altName)) {
            alternativeNames.add(altName);
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
