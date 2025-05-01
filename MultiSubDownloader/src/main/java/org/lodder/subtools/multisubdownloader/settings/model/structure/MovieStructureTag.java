package org.lodder.subtools.multisubdownloader.settings.model.structure;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.Messages;

public enum MovieStructureTag implements StructureTag {

    MOVIE_TITLE("%MOVIE TITLE%", "StructureBuilderDialog.MovieName"),
    QUALITY("%QUALITY%", "StructureBuilderDialog.QualityOfMovie"),
    YEAR("%YEAR%", "StructureBuilderDialog.MovieYear"),
    RELEASE_GROUP("%RELEASE GROUP%", "StructureBuilderDialog.ReleaseGroup");

    @val @override String label;
    @val @override String description;

    MovieStructureTag(String label, String descriptionMessage) {
        this.label = label;
        this.description = Messages.getText(descriptionMessage);
    }
}
