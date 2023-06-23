package org.lodder.subtools.sublibrary.settings.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Function;

import io.gsonfire.annotations.PostDeserialize;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SerieMapping implements Serializable { // implements SerieMappingIntf {

    @Serial
    private static final long serialVersionUID = 6551798252915028805L;
    private static final Function<String, String> NAME_FORMATTER = name -> name.replaceAll("[^A-Za-z]", "");
    private final String name;
    private final String providerId;
    private final String providerName;
    private final int season;
    private transient String formattedName;

    public SerieMapping(String name, int providerId, String providerName) {
        this(name, providerId, providerName, 0);
    }

    public SerieMapping(String name, int providerId, String providerName, int season) {
        this(name, String.valueOf(providerId), providerName, season);
    }

    public SerieMapping(String name, String providerId, String providerName, int season) {
        this.name = name;
        this.providerId = providerId;
        this.providerName = providerName;
        this.season = season;
        postDeserializeLogic();
    }

    @PostDeserialize
    public void postDeserializeLogic() {
        formattedName = getName().replaceAll("[^A-Za-z]", "");
    }

    public static Function<String, String> getNameFormatter() {
        return NAME_FORMATTER;
    }

    public static String formatName(String name) {
        return NAME_FORMATTER.apply(name);
    }

    public boolean matches(String serieName) {
        String serieNameFormatted = formatName(serieName);
        return getFormattedName().contains(serieNameFormatted)
                || (serieNameFormatted.contains(getFormattedName()) && getFormattedName().length() > 3);
    }

    public boolean exactMatch(String serieName) {
        return getFormattedName().equalsIgnoreCase(formatName(serieName));
    }
}
