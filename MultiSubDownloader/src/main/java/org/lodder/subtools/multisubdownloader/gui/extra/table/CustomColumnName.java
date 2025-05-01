package org.lodder.subtools.multisubdownloader.gui.extra.table;

import manifold.ext.props.rt.api.val;

public interface CustomColumnName {

    @val String columnName;
    @val boolean editable;
    @val Class<?> clazz;
}
