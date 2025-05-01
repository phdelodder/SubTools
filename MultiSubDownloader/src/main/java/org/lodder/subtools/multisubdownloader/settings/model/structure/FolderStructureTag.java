package org.lodder.subtools.multisubdownloader.settings.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.Messages;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FolderStructureTag implements StructureTag {

    SEPARATOR("%SEPARATOR%", Messages.getText("StructureBuilderDialog.SystemDependentSeparator"));

    @val @override String label;
    @val @override String description;
}
