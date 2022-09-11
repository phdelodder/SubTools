package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.lodder.subtools.sublibrary.Language;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LanguageId {

    ALL(null, 0),
    ALBANIAN(Language.ALBANIAN, 52),
    ARABIC(Language.ARABIC, 38),
    ARMENIAN(Language.ARMENIAN, 50),
    AZERBAIJANI(Language.AZERBAIJANI, 48),
    BENGALI(Language.BENGALI, 47),
    BOSNIAN(Language.BOSNIAN, 44),
    BULGARIAN(Language.BULGARIAN, 35),
    CANTONESE(Language.CANTONESE, 64),
    CATALAN(Language.CATALAN, 12),
    CHINESE_SIMPLIFIED(Language.CHINESE_SIMPLIFIED, 41),
    CHINESE_TRADITIONAL(Language.CHINESE_TRADITIONAL, 24),
    CROATIAN(Language.CROATIAN, 31),
    CZECH(Language.CZECH, 14),
    DANISH(Language.DANISH, 30),
    DUTCH(Language.DUTCH, 17),
    ENGLISH(Language.ENGLISH, 1),
    ESTONIAN(Language.ESTONIAN, 54),
    EUSKERA(Language.EUSKERA, 13),
    FINNISH(Language.FINNISH, 28),
    FRENCH(Language.FRENCH, 8),
    FRENCH_CANADIAN(Language.FRENCH, 53),
    GALICIAN(Language.GALICIAN, 15),
    GERMAN(Language.GERMAN, 11),
    GREEK(Language.GREEK, 27),
    HEBREW(Language.HEBREW, 23),
    HINDI(Language.HINDI, 55),
    HUNGARIAN(Language.HUNGARIAN, 20),
    ICELANDIC(Language.ICELANDIC, 56),
    INDONESIAN(Language.INDONESIAN, 37),
    ITALIAN(Language.ITALIAN, 7),
    JAPANESE(Language.JAPANESE, 32),
    KANNADA(Language.KANNADA, 66),
    KLINGON(Language.KLINGON, 61),
    KOREAN(Language.KOREAN, 42),
    LATVIAN(Language.LATVIAN, 57),
    LITHUANIAN(Language.LITHUANIAN, 58),
    MACEDONIAN(Language.MACEDONIAN, 49),
    MALAY(Language.MALAY, 40),
    MALAYALAM(Language.MALAYALAM, 67),
    MARATHI(Language.MARATHI, 62),
    NORWEGIAN(Language.NORWEGIAN, 29),
    PERSIAN(Language.PERSIAN, 43),
    POLISH(Language.POLISH, 21),
    PORTUGUESE(Language.PORTUGUESE, 9),
    PORTUGUESE_BRAZILIAN(Language.PORTUGUESE, 10),
    ROMANIAN(Language.ROMANIAN, 26),
    RUSSIAN(Language.RUSSIAN, 19),
    SERBIAN_CYRILLIC(Language.SERBIAN, 39),
    SERBIAN_LATIN(Language.SERBIAN, 36),
    SINHALA(Language.SINHALA, 60),
    SLOVAK(Language.SLOVAK, 25),
    SLOVENIAN(Language.SLOVENIAN, 22),
    SPANISH(Language.SPANISH, 4),
    SPANISH_ARGENTINA(Language.SPANISH, 69),
    SPANISH_LATIN_AMERICA(Language.SPANISH, 6),
    SPANISH_SPAIN(Language.SPANISH, 5),
    SWEDISH(Language.SWEDISH, 18),
    TAGALOG(Language.TAGALOG, 68),
    TAMIL(Language.TAMIL, 59),
    TELUGU(Language.TELUGU, 63),
    THAI(Language.THAI, 46),
    TURKISH(Language.TURKISH, 16),
    UKRAINIAN(Language.UKRAINIAN, 51),
    VIETNAMESE(Language.VIETNAMESE, 45),
    WELSH(Language.WELSH, 65);

    private final Language language;
    private final int id;

    public static List<LanguageId> forLanguage(Language language) {
        return Arrays.stream(LanguageId.values()).filter(langId -> langId.getLanguage() == language).collect(Collectors.toList());
    }
}
