package org.lodder.subtools.multisubdownloader.settings.model.structure;

import org.lodder.subtools.multisubdownloader.Messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MovieStructureTag implements StructureTag {

    MOVIE_TITLE("%MOVIE TITLE%", Messages.getString("StructureBuilderDialog.MovieName")),
    QUALITY("%QUALITY%", Messages.getString("StructureBuilderDialog.QualityOfRelease")),
    DESCRIPTION("%DESCRIPTION%", Messages.getString("StructureBuilderDialog.Description")),
    YEAR("%YEAR%", Messages.getString("StructureBuilderDialog.MovieYear"));

    @Getter
    private final String label;
    @Getter
    private final String description;

}