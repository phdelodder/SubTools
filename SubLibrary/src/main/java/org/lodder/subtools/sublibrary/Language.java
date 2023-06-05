package org.lodder.subtools.sublibrary;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Language {

    ALBANIAN("App.Language.Albanian", "sq", Set.of("alb", "sq", "Albanian")),
    ARABIC("App.Language.Arabic", "ar", Set.of("ara", "Arabic")),
    ARMENIAN("App.Language.Armenian", "hy", Set.of("arm", "hye", "Armenian")),
    AZERBAIJANI("App.Language.Azerbaijani", "az", Set.of("aze", "Azerbaijani")),
    BELARUSIAN("App.Language.Belarusian", "be", Set.of("bel", "Belarusian")),
    BENGALI("App.Language.Bengali", "bn", Set.of("ben", "Bengali")),
    BOSNIAN("App.Language.Bosnian", "bs", Set.of("bos", "Bosnian")),
    BULGARIAN("App.Language.Bulgarian", "bg", Set.of("bul", "Bulgarian")),
    CANTONESE("App.Language.Cantonese", "yue", Set.of("Yuet", "Cantonese")),
    CATALAN("App.Language.Catalan", "ca", Set.of("cat", "Catalan")),
    CHINESE_SIMPLIFIED("App.Language.Chinese_simplified", "zh", Set.of("chi", "zho", "Chinese")),
    CHINESE_TRADITIONAL("App.Language.Chinese_traditional", "zh", Set.of("chi", "zho", "Chinese")),
    CROATIAN("App.Language.Croatian", "hr", Set.of("hrv", "Croatian")),
    CZECH("App.Language.Czech", "cs", Set.of("cze", "ces", "Czech")),
    DANISH("App.Language.Danish", "da", Set.of("dan", "Danish")),
    DUTCH("App.Language.Dutch", "nl", Set.of("dut", "nld", "ned", "Dutch")),
    ENGLISH("App.Language.English", "en", Set.of("eng", "english")),
    ESTONIAN("App.Language.Estonian", "et", Set.of("est", "Estonian")),
    EUSKERA("App.Language.Euskera", "eu", Set.of("Euskera")),
    FINNISH("App.Language.Finnish", "fi", Set.of("Fin", "Finnish")),
    FRENCH("App.Language.French", "fr", Set.of("fre", "fra", "French")),
    GALICIAN("App.Language.Galician", "gl", Set.of("glg", "Galician")),
    GERMAN("App.Language.German", "de", Set.of("ger", "deu", "German")),
    GREEK("App.Language.Greek", "el", Set.of("gre", "ell", "Greek")),
    HEBREW("App.Language.Hebrew", "he", Set.of("heb", "Hebrew")),
    HINDI("App.Language.Hindi", "hi", Set.of("hin", "Hindi")),
    HUNGARIAN("App.Language.Hungarian", "hu", Set.of("hun", "Hungarian")),
    ICELANDIC("App.Language.Icelandic", "is", Set.of("ice", "isl", "Icelandic")),
    INDONESIAN("App.Language.Indonesian", "id", Set.of("ind", "Indonesian")),
    IRISH("App.Language.Irish", "ga", Set.of("gle", "Irish")),
    ITALIAN("App.Language.Italian", "it", Set.of("ita", "Italian")),
    JAPANESE("App.Language.Japanese", "ja", Set.of("jpn", "Japanese")),
    KANNADA("App.Language.Kannada", "ka", Set.of("kan", "Kannada")),
    KLINGON("App.Language.Klingon", "tlh", Set.of("Klingon")),
    KOREAN("App.Language.Korean", "ko", Set.of("kor", "Korean")),
    LATVIAN("App.Language.Latvian", "lv", Set.of("lav", "Latvian")),
    LITHUANIAN("App.Language.Lithuanian", "lt", Set.of("lit", "Lithuanian")),
    MACEDONIAN("App.Language.Macedonian", "mk", Set.of("mac", "mkd", "Macedonian")),
    MALAY("App.Language.Malay", "ms", Set.of("may", "msa", "Malay")),
    MALAYALAM("App.Language.Malayalam", "ml", Set.of("mal", "Malayalam")),
    MARATHI("App.Language.Marathi", "mr", Set.of("mar", "Marathi")),
    NORWEGIAN("App.Language.Norwegian", "no", Set.of("nor", "Norwegian")),
    PERSIAN("App.Language.Persian", "fa", Set.of("per", "fas", "Persian")),
    POLISH("App.Language.Polish", "pl", Set.of("pol", "Polish")),
    PORTUGUESE("App.Language.Portuguese", "pt", Set.of("por", "Portuguese")),
    ROMANIAN("App.Language.Romanian", "ro", Set.of("rum", "ron", "Romanian")),
    RUSSIAN("App.Language.Russian", "ru", Set.of("rus", "Russian")),
    SERBIAN("App.Language.Serbian", "sr", Set.of("srp", "Serbian")),
    SINHALA("App.Language.Sinhala", "si", Set.of("sin", "Sinhalese", "Sinhala")),
    SLOVAK("App.Language.Slovak", "sk", Set.of("slo", "slk", "Slovak")),
    SLOVENIAN("App.Language.Slovenian", "sl", Set.of("slv", "Slovenian")),
    SPANISH("App.Language.Spanish", "es", Set.of("spa", "Spanish")),
    SWEDISH("App.Language.Swedish", "se", Set.of("swe", "Swedish")),
    TAGALOG("App.Language.Tagalog", "tl", Set.of("tgl", "Tagalog")),
    TAMIL("App.Language.Tamil", "ta", Set.of("tam", "Tamil")),
    TELUGU("App.Language.Telugu", "te", Set.of("tel", "Telugu")),
    THAI("App.Language.Thai", "th", Set.of("tha", "Thai")),
    TURKISH("App.Language.Turkish", "tr", Set.of("tur", "Turkish")),
    UKRAINIAN("App.Language.Ukrainian", "uk", Set.of("ukr", "Ukrainian")),
    VIETNAMESE("App.Language.Vietnamese", "vi", Set.of("vie", "Vietnamese")),
    WELSH("App.Language.Welsh", "cy", Set.of("wel", "cym", "Welsh"));

    private final String msgCode;
    private final String langCode;
    private final Set<String> langCodesOther;

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
