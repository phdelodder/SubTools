package org.lodder.subtools.sublibrary.data.omdb.model;

import java.io.Serial;

import org.lodder.subtools.sublibrary.data.ReleaseDBIntf;

public record OmdbDetails(String title, int year) implements ReleaseDBIntf {
    @Serial
    private static final long serialVersionUID = 7701770682134890544L;

    @Override
    public String getName() {
        return title;
    }

    @Override public int getYear() {
        return year;
    }
}
