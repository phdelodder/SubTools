package org.lodder.subtools.multisubdownloader.settings.model.structure;

import org.lodder.subtools.multisubdownloader.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MovieStructureTag implements StructureTag {

    MOVIE_TITLE("%MOVIE TITLE%", Messages.getString("StructureBuilderDialog.MovieName")),
    QUALITY("%QUALITY%", Messages.getString("StructureBuilderDialog.QualityOfMovie")),
    DESCRIPTION("%DESCRIPTION%", Messages.getString("StructureBuilderDialog.MovieDescription")),
    YEAR("%YEAR%", Messages.getString("StructureBuilderDialog.MovieYear"));

    private final String label;
    private final String description;

}
