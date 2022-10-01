package org.lodder.subtools.sublibrary.util.prompter;

import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ColumnDisplayer<T> {

    private final String columnName;
    private final Function<T, String> toStringMapper;

}
