package org.lodder.subtools.sublibrary.cache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.util.FileUtils;
import org.lodder.subtools.sublibrary.util.lazy.LazyBiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ Files.class })
public abstract class DiskCache<K, V> extends Cache<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskCache.class);
    public static final Object LOCK = new Object();
    private final Long timeToLiveSeconds;
    private final Set<K> doublesToRemove = new HashSet<>();
    private final Map<K, CacheObject<V>> removedToAdd = new HashMap<>();

    private final LazyBiFunction<DiskCache<K, V>, String, Connection> connection = new LazyBiFunction<>((cache, tableName) -> {
        try {
            synchronized (cache.getCacheMap()) {
                Path path = Path.of(System.getProperty("user.home")).resolve(".MultiSubDownloader");
                if (!path.exists()) {
                    try {
                        Files.createDirectory(path);
                    } catch (IOException e) {
                        throw new RuntimeException("Could not create folder " + path, e);
                    }
                }
                Class.forName("org.hsqldb.jdbcDriver");
                Connection connection = DriverManager.getConnection(
                        "jdbc:hsqldb:file:" + path + "/diskcache.hsqldb;hsqldb.write_delay=false;shutdown=true", "user", "pass");

                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("create table IF NOT EXISTS %s (key %s, cacheobject %s);".formatted(tableName,
                            getDbKeyType() == String.class ? "VARCHAR(32768)" : "OBJECT",
                            getDbValueType() == String.class ? "VARCHAR(32768)" : "OBJECT"));
                }

                boolean errorWhileReadingCacheFile = false;
                try (
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT key, cacheobject FROM %s;".formatted(tableName))) {
                    // Map<K, CacheObject<V>> tempCache = new HashMap<>();
                    Multimap<K, CacheObject<V>> tempCache = MultimapBuilder.hashKeys()
                            .treeSetValues(Comparator.comparingLong((CacheObject<V> value) -> value.getAge()).reversed()).build();
                    synchronized (cache.getCacheMap()) {
                        while (rs.next()) {
                            try {
                                tempCache.put(cache.diskObjectToKey(rs.getObject("key")), cache.diskCacheObjectToValue(rs.getObject("cacheobject")));
                            } catch (SQLException e2) {
                                LOGGER.error("Unable to insert object in disk cache. (%s)".formatted(e2.getMessage()), e2);
                                errorWhileReadingCacheFile = true;
                            }
                        }
                        Map<K, Collection<CacheObject<V>>> map = tempCache.asMap();
                        map.entrySet().stream().filter(entry -> entry.getValue().size() > 1).forEach(entry -> {
                            doublesToRemove.add(entry.getKey());
                            removedToAdd.put(entry.getKey(), entry.getValue().iterator().next());
                        });
                        map.entrySet().stream().sorted(Comparator.comparingLong(entry -> entry.getValue().iterator().next().getAge()))
                                .forEach(entry -> put(entry.getKey(), entry.getValue().iterator().next()));
                    }
                } catch (SQLException e) {
                    LOGGER.error("Unable while insert objects in disk cache!" + e.getMessage(), e);
                }
                if (errorWhileReadingCacheFile) {
                    LOGGER.error("Deleting cache file to fix errors");
                    connection.close();
                    try {
                        FileUtils.delete(path);
                    } catch (IOException e) {
                        LOGGER.error("Error while deleting the cache file, please delete it yourself: %s (%s)".formatted(path, e.getMessage()), e);
                    }
                    connection = DriverManager.getConnection(
                            "jdbc:hsqldb:file:" + path + "/diskcache.hsqldb;hsqldb.write_delay=false;shutdown=true", "user", "pass");
                }
                return connection;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load jdbcdriver for diskcache");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    });
    private final String tableName;

    protected abstract Class<K> getDbKeyType();

    protected abstract Class<V> getDbValueType();

    private Connection getConnection() {
        return connection.apply(this, tableName);
    }

    protected DiskCache(Long timeToLiveSeconds, Integer maxItems, String username, String password, String tableName) {
        super(maxItems);
        if (timeToLiveSeconds != null && timeToLiveSeconds < 1) {
            throw new IllegalStateException("timeToLive should be a positive number");
        }
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.tableName = StringUtils.isBlank(tableName) ? "cacheobjects" : tableName;
        // initialize map in other thread
        new Thread(() -> {
            getConnection();
            doublesToRemove.forEach(this::removeFromDisk);
            removedToAdd.forEach(this::put);
            removedToAdd.keySet().forEach(this::putFromMemoryCache);
        }).start();
    }

    public void cleanup() {
        cleanup(null);
    }

    public void cleanup(Predicate<K> keyFilter) {
        synchronized (getCacheMap()) {
            Iterator<Entry<K, CacheObject<V>>> itr = getCacheMap().entrySet().iterator();
            while (itr.hasNext()) {
                Entry<K, CacheObject<V>> entry = itr.next();
                if ((keyFilter == null || keyFilter.test(entry.getKey())) && entry.getValue().isExpired(timeToLiveSeconds * 1000)) {
                    itr.remove();
                    removeFromDisk(entry.getKey());
                }
            }
            Thread.yield();
        }
    }

    @Override
    public void remove(K key) {
        super.remove(key);
        synchronized (LOCK) {
            removeFromDisk(key);
        }
    }

    private void removeFromDisk(K key) {
        synchronized (LOCK) {
            try (PreparedStatement prep = getConnection().prepareStatement("delete from %s where key = ?".formatted(tableName))) {
                // prep.clearParameters();
                prep.setObject(1, keyToDiskObject(key));
                prep.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error("Unable to delete object from disk cache!", e);
            }
        }
    }

    protected abstract K diskObjectToKey(Object key);

    protected abstract CacheObject<V> diskCacheObjectToValue(Object value);

    protected abstract Object keyToDiskObject(K key);

    protected abstract Object cacheObjectToDiskObject(CacheObject<V> value);

    @Override
    public final void put(K key, V value) {
        synchronized (LOCK) {
            super.put(key, value);
            putFromMemoryCache(key);
        }
    }

    @Override
    public final void put(K key, V value, long timeToLive) {
        synchronized (LOCK) {
            super.put(key, value, timeToLive);
            putFromMemoryCache(key);
        }
    }

    private void putFromMemoryCache(K key) {
        synchronized (LOCK) {
            try (PreparedStatement prep = getConnection().prepareCall("INSERT INTO %s (key,cacheobject) VALUES (?,?)".formatted(tableName))) {
                prep.clearParameters();
                prep.setObject(1, keyToDiskObject(key));
                synchronized (getCacheMap()) {
                    CacheObject<V> cacheObject = getCacheMap().get(key);
                    prep.setObject(2, cacheObjectToDiskObject(cacheObject));
                    prep.execute();
                }
                getConnection().commit();
            } catch (SQLException e) {
                LOGGER.error("Unable to insert object in disk cache!", e);
            }
        }
    }

    public void putWithoutPersist(K key, V value) {
        super.put(key, value);
    }
}
