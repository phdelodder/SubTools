package org.lodder.subtools.sublibrary.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StreamExtension {

    public static <T, X extends Exception> List<T> toListAdding(Stream<T> stream, T... elementsToAdd) throws X {
        List<T> list = stream.collect(Collectors.toList());
        Arrays.stream(elementsToAdd).forEach(list::add);
        return list;
    }
}
