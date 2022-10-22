package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
class TemporarySerializableCacheObject<T extends Serializable> extends TemporaryCacheObject<T> implements Serializable {

    private static final long serialVersionUID = 3426939140266268946L;

    protected TemporarySerializableCacheObject(long timeToLive, T value) {
        super(timeToLive, value);
    }
}
