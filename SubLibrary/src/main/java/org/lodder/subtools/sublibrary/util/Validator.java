package org.lodder.subtools.sublibrary.util;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import java.util.function.Predicate;

public record Validator<T>(Predicate<? super T> predicate, String errorMessage=getText("Prompter.ValueIsNotValid")) {
    public boolean isValid(T value) {
        return predicate.test(value);
    }

    public boolean isInvalid(T value) {
        return !isValid(value);
    }
}