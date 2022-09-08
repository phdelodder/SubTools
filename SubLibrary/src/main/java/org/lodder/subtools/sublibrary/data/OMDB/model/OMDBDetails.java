package org.lodder.subtools.sublibrary.data.OMDB.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OMDBDetails {
    private final String title;
    private final int year;
}
