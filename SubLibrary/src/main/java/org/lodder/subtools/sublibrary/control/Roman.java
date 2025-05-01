package org.lodder.subtools.sublibrary.control;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import manifold.ext.props.rt.api.val;

/**
 * <a href="http://rosettacode.org/wiki/Roman_numerals/Decode#Java_2">Source</a>
 */
@UtilityClass
public class Roman {

    @AllArgsConstructor
    public enum RomanNumeral {
        I(1),
        V(5),
        X(10),
        L(50),
        C(100),
        D(500),
        M(1000);

        @val int value;
    }

    private static int decodeSingle(char letter) {
        return RomanNumeral.valueOf(String.valueOf(letter).toUpperCase()).value;
    }

    public static int decode(String roman) {
        int result = 0;
        String uRoman = roman.toUpperCase(); // case-insensitive
        for (int i = 0; i < uRoman.length() - 1; i++) {// loop over all but the last character
            // if this character has a lower value than the next character
            if (decodeSingle(uRoman.charAt(i)) < decodeSingle(uRoman.charAt(i + 1))) {
                // subtract it
                result -= decodeSingle(uRoman.charAt(i));
            } else {
                // add it
                result += decodeSingle(uRoman.charAt(i));
            }
        }
        // decode the last character, which is always added
        result += decodeSingle(uRoman.charAt(uRoman.length() - 1));
        return result;
    }
}
