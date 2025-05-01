package org.lodder.subtools.sublibrary.data;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import manifold.ext.props.rt.api.val;

@AllArgsConstructor
@EqualsAndHashCode
public class ProviderSerieId implements Serializable {

    @Serial
    private static final long serialVersionUID = -120703658294502220L;
    @val String name;
    @val String id;
}
