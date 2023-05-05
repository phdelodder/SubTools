package org.lodder.subtools.sublibrary.data.omdb.model;

import java.io.Serial;
import java.io.Serializable;

import org.lodder.subtools.sublibrary.data.ReleaseDBIntf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OmdbDetails implements ReleaseDBIntf, Serializable {
    @Serial
    private static final long serialVersionUID = 7701770682134890544L;
    private final String title;
    private final int year;

    @Override
    public String getName() {
        return title;
    }
}
