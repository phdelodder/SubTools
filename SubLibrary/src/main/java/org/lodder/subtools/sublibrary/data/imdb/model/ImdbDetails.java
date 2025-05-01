package org.lodder.subtools.sublibrary.data.imdb.model;

import org.lodder.subtools.sublibrary.data.ReleaseDBIntf;

public record ImdbDetails(String title, int year) implements ReleaseDBIntf {

    @Override
    public String getName() {
        return title;
    }

    @Override
    public int getYear() {
        return year;
    }
}

