package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Setter
@Getter
class ExpiringSerializableCacheObject<T extends Serializable> extends ExpiringCacheObject<T> implements Serializable {

    private static final long serialVersionUID = 8773462650510864103L;

    protected ExpiringSerializableCacheObject(T value) {
        super(value);
    }
}
