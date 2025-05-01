package org.lodder.subtools.sublibrary.data;

import java.io.Serializable;

import manifold.ext.props.rt.api.val;

public interface ReleaseDBIntf extends Serializable {

    @val String name;

    @val int year;
}
