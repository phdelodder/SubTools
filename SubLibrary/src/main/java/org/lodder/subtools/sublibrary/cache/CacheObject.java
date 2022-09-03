package org.lodder.subtools.sublibrary.cache;

import java.io.Serializable;

class CacheObject<K, T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3852086993086134232L;
    public long created = System.currentTimeMillis();
    public long lastAccessed = System.currentTimeMillis();
    public T value;

    protected CacheObject(T value) {
        this.value = value;
    }
}
