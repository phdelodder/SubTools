package org.lodder.subtools.sublibrary.data.OMDB.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OMDBDetails implements Serializable{
    private static final long serialVersionUID = 7701770682134890544L;
    private final String title;
    private final int year;
}
