package org.lodder.subtools.sublibrary.data.imdb.model;

import java.io.Serializable;

import org.lodder.subtools.sublibrary.data.ReleaseDBIntf;

public record ImdbDetails(String title, int year) implements ReleaseDBIntf, Serializable {

    @Override
    public String getName() {
        return title;
    }
}
