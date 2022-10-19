package org.lodder.subtools.sublibrary.cache;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.plexus.util.FileUtils;
import org.lodder.subtools.sublibrary.util.lazy.LazyBiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DiskCache<K, V> extends InMemoryCache<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskCache.class);
    public static final Object LOCK = new Object();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private final LazyBiFunction<DiskCache, String, Connection> connection = new LazyBiFunction<>((cache, tableName) -> {
        try {
            synchronized (cache.getCacheMap()) {
                File path = new File(System.getProperty("user.home"), ".MultiSubDownloader");
                if (!path.exists() && !path.mkdir()) {
                    throw new RuntimeException("Could not create folder " + path);
                }
                Class.forName("org.hsqldb.jdbcDriver");
                Connection connection = DriverManager.getConnection(
                        "jdbc:hsqldb:file:" + path.toString() + "/diskcache.hsqldb;hsqldb.write_delay=false;shutdown=true", "user", "pass");

                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("create table IF NOT EXISTS %s (key %s, cacheobject %s);".formatted(tableName,
                            getDbKeyType() == String.class ? "VARCHAR(32768)" : "OBJECT",
                            getDbValueType() == String.class ? "VARCHAR(32768)" : "OBJECT"));
                }

                boolean errorWhileReadingCacheFile = false;
                try (Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT key, cacheobject FROM %s;".formatted(tableName));) {
                    synchronized (cache.getCacheMap()) {
                        while (rs.next()) {
                            try {
                                cache.put(cache.diskObjectToKey(rs.getObject("key")), cache.diskCacheObjectToValue(rs.getObject("cacheobject")));
                            } catch (SQLException e2) {
                                LOGGER.error("Unable to insert object in disk cache. (%s)".formatted(e2.getMessage()), e2);
                                errorWhileReadingCacheFile = true;
                            }
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.error("Unable while insert objects in disk cache!" + e.getMessage(), e);
                }
                if (errorWhileReadingCacheFile) {
                    LOGGER.error("Deleting cache file to fix errors");
                    connection.close();
                    try {
                        FileUtils.deleteDirectory(path);
                    } catch (IOException e) {
                        LOGGER.error("Error while deleting the cache file, please delete it yourself: %s (%s)".formatted(path, e.getMessage()), e);
                    }
                    connection = DriverManager.getConnection(
                            "jdbc:hsqldb:file:" + path.toString() + "/diskcache.hsqldb;hsqldb.write_delay=false;shutdown=true", "user", "pass");
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

    protected abstract Class getDbKeyType();

    protected abstract Class getDbValueType();

    private Connection getConnection() {
        return connection.apply(this, tableName);
    }

    protected DiskCache(Long timeToLive, Long timerInterval, Integer maxItems, String username, String password, String tableName) {
        super(timeToLive, timerInterval, maxItems);
        this.tableName = StringUtils.isBlank(tableName) ? "cacheobjects" : tableName;
        // initialize map in other thread
        new Thread(() -> getConnection()).start();
        if (timeToLive != null && timeToLive > 0) {
            cleanup();
        }
    }

    @Override
    public void cleanup() {
        synchronized (getCacheMap()) {
            Iterator<Entry<K, CacheObject<V>>> itr = getCacheMap().entrySet().iterator();
            while (itr.hasNext()) {
                Entry<K, CacheObject<V>> entry = itr.next();
                if (!(entry.getValue() instanceof TemporaryCacheObject) && entry.getValue().isExpired(getTimeToLive())) {
                    itr.remove();
                }
                remove(entry.getKey());
            }
            Thread.yield();
        }
    }

    protected abstract K diskObjectToKey(Object key);

    protected abstract CacheObject<V> diskCacheObjectToValue(Object value);

    @Override
    public final void remove(K key) {
        super.remove(key);
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

    protected abstract Object keyToDiskObject(K key);

    protected abstract Object cacheObjectToDiskObject(CacheObject<V> value);

    @Override
    public final void put(K key, V value) {
        super.put(key, value);
        putFromMemoryCache(key, value);
    }

    @Override
    public final void put(K key, V value, long timeToLive) {
        super.put(key, value, timeToLive);
        putFromMemoryCache(key, value);
    }

    private final void putFromMemoryCache(K key, V value) {
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

    @Override
    public void deleteEntries(Predicate<K> keyFilter) {
        synchronized (LOCK) {
            List<Pair<K, V>> entries = super.getEntries(keyFilter);
            entries.forEach(pair -> remove(pair.getKey()));
        }
    }
}
