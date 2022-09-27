package org.lodder.subtools.sublibrary;

import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {

    ALBANIAN("InputPanel.Language.Albanian", "sq"),
    ARABIC("InputPanel.Language.Arabic", "ar"),
    ARMENIAN("InputPanel.Language.Armenian", "hy"),
    AZERBAIJANI("InputPanel.Language.Azerbaijani", "az"),
    BELARUSIAN("InputPanel.Language.Belarusian", "be"),
    BENGALI("InputPanel.Language.Bengali", "bn"),
    BOSNIAN("InputPanel.Language.Bosnian", "bs"),
    BULGARIAN("InputPanel.Language.Bulgarian", "bg"),
    CANTONESE("InputPanel.Language.Cantonese", "yue"),
    CATALAN("InputPanel.Language.Catalan", "ca"),
    CHINESE_SIMPLIFIED("InputPanel.Language.Chinese_simplified", "zh"),
    CHINESE_TRADITIONAL("InputPanel.Language.Chinese_traditional", "zh"),
    CROATIAN("InputPanel.Language.Croatian", "hr"),
    CZECH("InputPanel.Language.Czech", "cs"),
    DANISH("InputPanel.Language.Danish", "da"),
    DUTCH("InputPanel.Language.Dutch", "nl"),
    ENGLISH("InputPanel.Language.English", "en"),
    ESTONIAN("InputPanel.Language.Estonian", "et"),
    EUSKERA("InputPanel.Language.Euskera", "eu"),
    FINNISH("InputPanel.Language.Finnish", "fi"),
    FRENCH("InputPanel.Language.French", "fr"),
    GALICIAN("InputPanel.Language.Galician", "gl"),
    GERMAN("InputPanel.Language.German", "de"),
    GREEK("InputPanel.Language.Greek", "el"),
    HEBREW("InputPanel.Language.Hebrew", "he"),
    HINDI("InputPanel.Language.Hindi", "hi"),
    HUNGARIAN("InputPanel.Language.Hungarian", "hu"),
    ICELANDIC("InputPanel.Language.Icelandic", "is"),
    INDONESIAN("InputPanel.Language.Indonesian", "id"),
    IRISH("InputPanel.Language.Irish", "ga"),
    ITALIAN("InputPanel.Language.Italian", "it"),
    JAPANESE("InputPanel.Language.Japanese", "ja"),
    KANNADA("InputPanel.Language.Kannada", "ka"),
    KLINGON("InputPanel.Language.Klingon", "tlh"),
    KOREAN("InputPanel.Language.Korean", "ko"),
    LATVIAN("InputPanel.Language.Latvian", "lv"),
    LITHUANIAN("InputPanel.Language.Lithuanian", "lt"),
    MACEDONIAN("InputPanel.Language.Macedonian", "mk"),
    MALAY("InputPanel.Language.Malay", "ms"),
    MALAYALAM("InputPanel.Language.Malayalam", "ml"),
    MARATHI("InputPanel.Language.Marathi", "mr"),
    NORWEGIAN("InputPanel.Language.Norwegian", "no"),
    PERSIAN("InputPanel.Language.Persian", "fa"),
    POLISH("InputPanel.Language.Polish", "pl"),
    PORTUGUESE("InputPanel.Language.Portuguese", "pt"),
    ROMANIAN("InputPanel.Language.Romanian", "ro"),
    RUSSIAN("InputPanel.Language.Russian", "ru"),
    SERBIAN("InputPanel.Language.Serbian", "sr"),
    SINHALA("InputPanel.Language.Sinhala", "si"),
    SLOVAK("InputPanel.Language.Slovak", "sk"),
    SLOVENIAN("InputPanel.Language.Slovenian", "sl"),
    SPANISH("InputPanel.Language.Spanish", "es"),
    SWEDISH("InputPanel.Language.Swedish", "se"),
    TAGALOG("InputPanel.Language.Tagalog", "tl"),
    TAMIL("InputPanel.Language.Tamil", "ta"),
    TELUGU("InputPanel.Language.Telugu", "te"),
    THAI("InputPanel.Language.Thai", "th"),
    TURKISH("InputPanel.Language.Turkish", "tr"),
    UKRAINIAN("InputPanel.Language.Ukrainian", "uk"),
    VIETNAMESE("InputPanel.Language.Vietnamese", "vi"),
    WELSH("InputPanel.Language.Welsh", "cy");

    private final String msgCode;
    private final String langCode;

    public static Language fromValue(String value) {
        return Arrays.stream(Language.values()).filter(lang -> lang.name().equalsIgnoreCase(value)).findAny().orElseThrow();
    }

    public static Optional<Language> fromValueOptional(String value) {
        return Arrays.stream(Language.values()).filter(lang -> lang.name().equalsIgnoreCase(value)).findAny();
    }

    public static Optional<Language> fromIdOptional(String languageId) {
        return Arrays.stream(Language.values()).filter(lang -> lang.getLangCode().equalsIgnoreCase(languageId)).findAny();
    }

    public String getName() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
