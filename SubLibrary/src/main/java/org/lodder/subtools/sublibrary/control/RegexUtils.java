package org.lodder.subtools.sublibrary.control;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.control.Tags.Tag;

/**
 * <a href="http://rosettacode.org/wiki/Roman_numerals/Decode#Java_2">Source</a>
 */
@UtilityClass
public class RegexUtils {

    public static final List<Character> DELIMITERS = List.of('.', ' ', '-');
    public static final String DELIMITER =
        "[" + Pattern.quote(DELIMITERS.stream().map(String::valueOf).collect(Collectors.joining(""))) + "]";

    interface RegexStart extends RegexTag {
        RegexTag startOfText();
    }

    interface RegexTag extends RegexRegex {
        RegexRegex tag(Tag tag);
    }

    interface RegexRegex {
        RegexNext regex(String regex);

        RegexNext regexOptional(String regex);

        <E extends Enum<E>> RegexNext regex(Class<E> enumClass, Function<? super E, String> toStringMapper);
    }

    interface RegexNext extends RegexTag, RegexEnd {
    }

    interface RegexEnd extends RegexBuilderBuild {
        RegexBuilderBuild endOfText();
    }

    interface RegexBuilderBuild {
        String create();

        List<String> createWithDelimiter();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Regex implements RegexStart, RegexTag, RegexRegex, RegexNext, RegexEnd, RegexBuilderBuild {
        private Tag tag;
        private String regex;
        private String result = "";
        private boolean finalized = false;

        public static RegexStart builder() {
            return new Regex();
        }

        @Override
        public Regex startOfText() {
            return regex("^");
        }

        @Override
        public Regex endOfText() {
            return regex("$");
        }

        @Override
        public Regex tag(Tag tag) {
            if (StringUtils.isNotBlank(this.regex)) {
                next();
            }
            this.tag = tag;
            return this;
        }

        @Override
        public Regex regex(String regex) {
            if (StringUtils.isNotBlank(this.regex)) {
                next();
            }
            this.regex = regex;
            return this;
        }

        @Override
        public Regex regexOptional(String regex) {
            return regex("(" + regex + ")?");
        }

        @Override
        public <E extends Enum<E>> Regex regex(Class<E> enumClass, Function<? super E, String> toStringMapper) {
            return regex(enumClass.getEnumConstants().stream().map(toStringMapper).collect(Collectors.joining("|")));
        }

        private void next() {
            result += createCurrent();
            tag = null;
            regex = null;
        }

        private String createCurrent() {
            return tag == null ? regex : "(?<${tag.value}>$regex)";
        }

        @Override
        public String create() {
            if (!finalized) {
                next();
            }
            finalized = true;
            return result;
        }

        @Override
        public List<String> createWithDelimiter() {
            next();
            return List.of(
                DELIMITER + result + DELIMITER,
                DELIMITER + result + "$",
                "^" + result + DELIMITER,
                "^" + result + "$");
        }
    }

}
