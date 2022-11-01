package org.lodder.subtools.multisubdownloader;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.lodder.subtools.sublibrary.Language;

public class Messages {
    private static final String BUNDLE_NAME = "messages";
    private static final Language DEFAULT_LANGUAGE = Language.ENGLISH;
    private static Language LANGUAGE;
    private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, new Locale(DEFAULT_LANGUAGE.getLangCode()));

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
        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, new Locale(language.getLangCode()));
    }

    public static Language getLanguage() {
        return LANGUAGE;
    }

    public static List<Language> getAvailableLanguages() {
        try {
            List<Language> languages =
                    Arrays.stream(Paths.get(Messages.class.getClassLoader().getResource("messages.properties").toURI()).getParent().toFile()
                            .listFiles((dir, name) -> name.matches(BUNDLE_NAME + "_.*\\.properties")))
                            .map(file -> file.getName().replace(BUNDLE_NAME + "_", "").replace(".properties", "")).map(Language::fromIdOptional)
                            .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            languages.add(DEFAULT_LANGUAGE);
            Collections.sort(languages);
            return languages;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
