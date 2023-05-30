package org.lodder.subtools.multisubdownloader.settings.model;

import java.awt.Image;
import java.awt.Toolkit;

import lombok.Getter;

@Getter
public enum PathMatchType {
    FOLDER("/folder.png"),
    REGEX("/regex.gif"),
    FILE("/file.jpg");

    private final Image image;

    PathMatchType(String imagePath) {
        this.image = Toolkit.getDefaultToolkit().getImage(getClass().getResource(imagePath));
    }
}
