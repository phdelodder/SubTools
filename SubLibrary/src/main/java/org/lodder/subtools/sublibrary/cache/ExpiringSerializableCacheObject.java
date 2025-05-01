package org.lodder.subtools.sublibrary.cache;

import java.io.Serial;
import java.io.Serializable;

import lombok.ToString;

@ToString
final class ExpiringSerializableCacheObject<T extends Serializable> extends ExpiringCacheObject<T> {

    @Serial
    private static final long serialVersionUID = 8773462650510864103L;

    ExpiringSerializableCacheObject(T value) {
        super(value);
    }
}
