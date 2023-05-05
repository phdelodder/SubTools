package org.lodder.subtools.sublibrary.data.imdb.model;

import java.io.Serial;
import java.io.Serializable;

import org.lodder.subtools.sublibrary.data.ReleaseDBIntf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImdbDetails implements ReleaseDBIntf, Serializable {
    @Serial
    private static final long serialVersionUID = 2596873770215746380L;
    private final String title;
    private final int year;

    @Override
    public String getName() {
        return title;
    }

}
