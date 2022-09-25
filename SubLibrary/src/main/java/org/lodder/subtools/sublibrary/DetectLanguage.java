package org.lodder.subtools.sublibrary;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.lodder.subtools.sublibrary.util.lazy.LazyThrowingSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObjectFactory;

public class DetectLanguage {

    private static final Logger LOGGER = LoggerFactory.getLogger(DetectLanguage.class);
    private static final LazyThrowingSupplier<LanguageDetector, IOException> DETECTOR =
            new LazyThrowingSupplier<>(() -> LanguageDetectorBuilder.create(NgramExtractors.standard())
                    .shortTextAlgorithm(0)
                    .withProfiles(new LanguageProfileReader().readAllBuiltIn())
                    .build());
    private static final LazySupplier<TextObjectFactory> TEXT_OBJECT_FACTORY =
            new LazySupplier<>(CommonTextObjectFactories::forDetectingOnLargeText);
    private static final double MIN_PROBABILITY = 0.9;

    public static Language execute(File file) {
        return execute(file, null);
    }

    public static Language execute(File file, Language defaultLang) {
        return executeOptional(file).orElse(defaultLang);
    }

    public static Optional<Language> executeOptional(File file) {
        try (Reader reader = new FileReader(file)) {
            return DETECTOR.get().getProbabilities(TEXT_OBJECT_FACTORY.get().create().append(reader)).stream()
                    .filter(lang -> lang.getProbability() >= MIN_PROBABILITY).findFirst()
                    .map(lang -> lang.getLocale().getLanguage()).flatMap(Language::fromValueOptional);
        } catch (IOException e) {
            LOGGER.error("Could not detect language of file " + file);
            return Optional.empty();
        }
    }

}
