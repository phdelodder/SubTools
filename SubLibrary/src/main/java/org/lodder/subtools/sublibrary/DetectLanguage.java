package org.lodder.subtools.sublibrary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;

public class DetectLanguage {

    private static final Logger LOGGER = LoggerFactory.getLogger(DetectLanguage.class);
    private static final LanguageDetector DETECTOR = LanguageDetectorBuilder
            // .fromLanguages(Language.DUTCH, Language.ENGLISH)
            .fromAllLanguages()
            .withMinimumRelativeDistance(0.9)
            .build();

    public static Language execute(File file) {
        return execute(file, null);
    }

    public static Language execute(File file, Language defaultLang) {
        return DETECTOR.computeLanguageConfidenceValues(readText(file)).entrySet().stream().map(Entry::getKey).findFirst()
                .map(com.github.pemistahl.lingua.api.Language::name).map(Language::fromValueOptional).map(optional -> optional.orElse(defaultLang))
                .orElse(defaultLang);
    }

    public static Optional<Language> executeOptional(File file) {
        return DETECTOR.computeLanguageConfidenceValues(readText(file)).entrySet().stream().map(Entry::getKey).findFirst()
                .map(com.github.pemistahl.lingua.api.Language::name).map(Language::fromValueOptional).map(optional -> optional.orElse(null))
                .filter(Objects::nonNull);
    }

    private static String readText(File file) {
        String text = "";
        try {
            text = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found to detect language", e);
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return text;
    }

}
