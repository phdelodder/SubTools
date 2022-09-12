package org.lodder.subtools.sublibrary;

import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {

    ALBANIAN("InputPanel.Albanian", "sq"),
    ARABIC("InputPanel.Arabic", "ar"),
    ARMENIAN("InputPanel.Armenian", "hy"),
    AZERBAIJANI("InputPanel.Azerbaijani", "az"),
    BELARUSIAN("InputPanel.Belarusian", "be"),
    BENGALI("InputPanel.Bengali", "bn"),
    BOSNIAN("InputPanel.Bosnian", "bs"),
    BULGARIAN("InputPanel.Bulgarian", "bg"),
    CANTONESE("InputPanel.Cantonese", "yue"),
    CATALAN("InputPanel.Catalan", "ca"),
    CHINESE_SIMPLIFIED("InputPanel.Chinese_simplified", "zh"),
    CHINESE_TRADITIONAL("InputPanel.Chinese_traditional", "zh"),
    CROATIAN("InputPanel.Croatian", "hr"),
    CZECH("InputPanel.Czech", "cs"),
    DANISH("InputPanel.Danish", "da"),
    DUTCH("InputPanel.Dutch", "nl"),
    ENGLISH("InputPanel.English", "en"),
    ESTONIAN("InputPanel.Estonian", "et"),
    EUSKERA("InputPanel.Euskera", "eu"),
    FINNISH("InputPanel.Finnish", "fi"),
    FRENCH("InputPanel.French", "fr"),
    GALICIAN("InputPanel.Galician", "gl"),
    GERMAN("InputPanel.German", "de"),
    GREEK("InputPanel.Greek", "el"),
    HEBREW("InputPanel.Hebrew", "he"),
    HINDI("InputPanel.Hindi", "hi"),
    HUNGARIAN("InputPanel.Hungarian", "hu"),
    ICELANDIC("InputPanel.Icelandic", "is"),
    INDONESIAN("InputPanel.Indonesian", "id"),
    IRISH("InputPanel.Irish", "ga"),
    ITALIAN("InputPanel.Italian", "it"),
    JAPANESE("InputPanel.Japanese", "ja"),
    KANNADA("InputPanel.Kannada", "ka"),
    KLINGON("InputPanel.Klingon", "tlh"),
    KOREAN("InputPanel.Korean", "ko"),
    LATVIAN("InputPanel.Latvian", "lv"),
    LITHUANIAN("InputPanel.Lithuanian", "lt"),
    MACEDONIAN("InputPanel.Macedonian", "mk"),
    MALAY("InputPanel.Malay", "ms"),
    MALAYALAM("InputPanel.Malayalam", "ml"),
    MARATHI("InputPanel.Marathi", "mr"),
    NORWEGIAN("InputPanel.Norwegian", "no"),
    PERSIAN("InputPanel.Persian", "fa"),
    POLISH("InputPanel.Polish", "pl"),
    PORTUGUESE("InputPanel.Portuguese", "pt"),
    ROMANIAN("InputPanel.Romanian", "ro"),
    RUSSIAN("InputPanel.Russian", "ru"),
    SERBIAN("InputPanel.Serbian", "sr"),
    SINHALA("InputPanel.Sinhala", "si"),
    SLOVAK("InputPanel.Slovak", "sk"),
    SLOVENIAN("InputPanel.Slovenian", "sl"),
    SPANISH("InputPanel.Spanish", "es"),
    SWEDISH("InputPanel.Swedish", "se"),
    TAGALOG("InputPanel.Tagalog", "tl"),
    TAMIL("InputPanel.Tamil", "ta"),
    TELUGU("InputPanel.Telugu", "te"),
    THAI("InputPanel.Thai", "th"),
    TURKISH("InputPanel.Turkish", "tr"),
    UKRAINIAN("InputPanel.Ukrainian", "uk"),
    VIETNAMESE("InputPanel.Vietnamese", "vi"),
    WELSH("InputPanel.Welsh", "cy");

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
