package org.lodder.subtools.multisubdownloader;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.lodder.subtools.sublibrary.Language;

public class Messages {
    private static final String BUNDLE_NAME = "messages";
    private static final Language DEFAULT_LANGUAGE = Language.ENGLISH;
    private static Language LANGUAGE;
    private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.forLanguageTag(DEFAULT_LANGUAGE.getLangCode()));

    private Messages() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getString(String key, Object... replacements) {
        try {
            return RESOURCE_BUNDLE.getString(key).formatted(replacements);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static void setLanguage(Language language) {
        LANGUAGE = language;
        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.forLanguageTag(language.getLangCode()));
    }

    public static Language getLanguage() {
        return LANGUAGE;
    }

    public static List<Language> getAvailableLanguages() {
        return List.of(Language.fromId("nl"), DEFAULT_LANGUAGE);
    }
}
