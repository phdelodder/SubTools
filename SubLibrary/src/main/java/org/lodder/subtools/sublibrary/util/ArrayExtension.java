package org.lodder.subtools.sublibrary.util;

import java.util.Arrays;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ArrayExtension {

    public <T> boolean contains(T[] array, T value) {
        return Arrays.asList(array).contains(value);
    }
}
