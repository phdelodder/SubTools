package org.lodder.subtools.multisubdownloader;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import manifold.ext.props.rt.api.var;
import org.lodder.subtools.sublibrary.Language;

public class Messages {
    private static final String BUNDLE_NAME = "resourcebundle.Message";
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ROOT);
    static @var Language language;

    private Messages() {
    }

    public static String getText(String key, Object... replacements) {
        try {
            String text = resourceBundle.getString(key);
            return replacements == null || replacements.isEmpty() ? text : text.formatted(replacements);
        } catch (MissingResourceException e) {
            return "!$key!";
        }
    }

    public static void setLanguage(Language language) {
        Messages.language = language;
        Locale locale = language == Language.ENGLISH ? Locale.ROOT : Locale.forLanguageTag(language.langCode);
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }

    public static List<Language> getAvailableLanguages() {
        return List.of(Language.DUTCH, Language.ENGLISH);
    }
}
