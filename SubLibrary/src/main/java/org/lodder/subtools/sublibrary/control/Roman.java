package org.lodder.subtools.sublibrary.control;

/**
 *  <a href="http://rosettacode.org/wiki/Roman_numerals/Decode#Java_2">Source</a>
 */

public class Roman {
    private static int decodeSingle(char letter) {
        return switch (letter) {
            case 'M' -> 1000;
            case 'D' -> 500;
            case 'C' -> 100;
            case 'L' -> 50;
            case 'X' -> 10;
            case 'V' -> 5;
            case 'I' -> 1;
            default -> 0;
        };
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
