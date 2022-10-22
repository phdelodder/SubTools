package org.lodder.subtools.sublibrary.data;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class ProviderSerieId implements Serializable {

    private static final long serialVersionUID = -120703658294502220L;
    private final String name;
    private final String id;
}
