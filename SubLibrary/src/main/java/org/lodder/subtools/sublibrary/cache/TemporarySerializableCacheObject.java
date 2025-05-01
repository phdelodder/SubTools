package org.lodder.subtools.sublibrary.cache;

import java.io.Serial;
import java.io.Serializable;

import lombok.ToString;
import manifold.science.measures.Time;

@ToString
final class TemporarySerializableCacheObject<T extends Serializable> extends TemporaryCacheObject<T> {

    @Serial
    private static final long serialVersionUID = 3426939140266268946L;

    TemporarySerializableCacheObject(Time timeToLive, T value) {
        super(timeToLive, value);
    }
}
