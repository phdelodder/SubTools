package org.lodder.subtools.multisubdownloader.settings.model;

import java.awt.*;

import manifold.ext.props.rt.api.val;

public enum PathMatchType {
    FOLDER("/folder.png"),
    REGEX("/regex.gif"),
    FILE("/file.jpg");

    @val Image image;

    PathMatchType(String imagePath) {
        this.image = Toolkit.getDefaultToolkit().getImage(getClass().getResource(imagePath));
    }
}
