package org.lodder.subtools.sublibrary;

import java.util.Arrays;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Language {

    ALBANIAN("App.Language.Albanian", "sq"),
    ARABIC("App.Language.Arabic", "ar"),
    ARMENIAN("App.Language.Armenian", "hy"),
    AZERBAIJANI("App.Language.Azerbaijani", "az"),
    BELARUSIAN("App.Language.Belarusian", "be"),
    BENGALI("App.Language.Bengali", "bn"),
    BOSNIAN("App.Language.Bosnian", "bs"),
    BULGARIAN("App.Language.Bulgarian", "bg"),
    CANTONESE("App.Language.Cantonese", "yue"),
    CATALAN("App.Language.Catalan", "ca"),
    CHINESE_SIMPLIFIED("App.Language.Chinese_simplified", "zh"),
    CHINESE_TRADITIONAL("App.Language.Chinese_traditional", "zh"),
    CROATIAN("App.Language.Croatian", "hr"),
    CZECH("App.Language.Czech", "cs"),
    DANISH("App.Language.Danish", "da"),
    DUTCH("App.Language.Dutch", "nl"),
    ENGLISH("App.Language.English", "en"),
    ESTONIAN("App.Language.Estonian", "et"),
    EUSKERA("App.Language.Euskera", "eu"),
    FINNISH("App.Language.Finnish", "fi"),
    FRENCH("App.Language.French", "fr"),
    GALICIAN("App.Language.Galician", "gl"),
    GERMAN("App.Language.German", "de"),
    GREEK("App.Language.Greek", "el"),
    HEBREW("App.Language.Hebrew", "he"),
    HINDI("App.Language.Hindi", "hi"),
    HUNGARIAN("App.Language.Hungarian", "hu"),
    ICELANDIC("App.Language.Icelandic", "is"),
    INDONESIAN("App.Language.Indonesian", "id"),
    IRISH("App.Language.Irish", "ga"),
    ITALIAN("App.Language.Italian", "it"),
    JAPANESE("App.Language.Japanese", "ja"),
    KANNADA("App.Language.Kannada", "ka"),
    KLINGON("App.Language.Klingon", "tlh"),
    KOREAN("App.Language.Korean", "ko"),
    LATVIAN("App.Language.Latvian", "lv"),
    LITHUANIAN("App.Language.Lithuanian", "lt"),
    MACEDONIAN("App.Language.Macedonian", "mk"),
    MALAY("App.Language.Malay", "ms"),
    MALAYALAM("App.Language.Malayalam", "ml"),
    MARATHI("App.Language.Marathi", "mr"),
    NORWEGIAN("App.Language.Norwegian", "no"),
    PERSIAN("App.Language.Persian", "fa"),
    POLISH("App.Language.Polish", "pl"),
    PORTUGUESE("App.Language.Portuguese", "pt"),
    ROMANIAN("App.Language.Romanian", "ro"),
    RUSSIAN("App.Language.Russian", "ru"),
    SERBIAN("App.Language.Serbian", "sr"),
    SINHALA("App.Language.Sinhala", "si"),
    SLOVAK("App.Language.Slovak", "sk"),
    SLOVENIAN("App.Language.Slovenian", "sl"),
    SPANISH("App.Language.Spanish", "es"),
    SWEDISH("App.Language.Swedish", "se"),
    TAGALOG("App.Language.Tagalog", "tl"),
    TAMIL("App.Language.Tamil", "ta"),
    TELUGU("App.Language.Telugu", "te"),
    THAI("App.Language.Thai", "th"),
    TURKISH("App.Language.Turkish", "tr"),
    UKRAINIAN("App.Language.Ukrainian", "uk"),
    VIETNAMESE("App.Language.Vietnamese", "vi"),
    WELSH("App.Language.Welsh", "cy");

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

    public static Language fromId(String languageId) {
        return fromIdOptional(languageId).orElseThrow();
    }

    public String getName() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
