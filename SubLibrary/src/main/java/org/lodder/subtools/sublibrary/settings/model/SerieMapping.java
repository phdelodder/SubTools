package org.lodder.subtools.sublibrary.settings.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.UnaryOperator;

import io.gsonfire.annotations.PostDeserialize;
import lombok.ToString;
import manifold.ext.props.rt.api.val;
import manifold.ext.props.rt.api.var;

@ToString
public class SerieMapping implements Serializable {

    @Serial
    private static final long serialVersionUID = 6551798252915028805L;
    private static final UnaryOperator<String> NAME_FORMATTER = n -> n.replaceAll("[^A-Za-z]", "");
    @val String name;
    @val String providerId;
    @val String providerName;
    @val int season;
    @var transient String formattedName;

    public SerieMapping(String name, String providerId, String providerName, int season=0) {
        this.name = name;
        this.providerId = providerId;
        this.providerName = providerName;
        this.season = season;
        postDeserializeLogic();
    }

    @PostDeserialize
    public void postDeserializeLogic() {
        formattedName = name.replaceAll("[^A-Za-z]", "");
    }

    public static String formatName(String name) {
        return NAME_FORMATTER.apply(name);
    }

    public boolean matches(String serieName) {
        String serieNameFormatted = formatName(serieName);
        return formattedName.contains(serieNameFormatted)
               || (serieNameFormatted.contains(formattedName) && formattedName.length() > 3);
    }

    public boolean exactMatch(String serieName) {
        return formattedName.equalsIgnoreCase(formatName(serieName));
    }
}
