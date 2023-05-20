package org.lodder.subtools.sublibrary.util.prompter;

import java.util.function.Function;

public record ColumnDisplayer<T>(String columnName, Function<T, String> toStringMapper) {}
