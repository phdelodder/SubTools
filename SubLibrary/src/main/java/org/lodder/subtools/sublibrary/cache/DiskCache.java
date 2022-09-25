package org.lodder.subtools.sublibrary.cache;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DiskCache<K, V> extends InMemoryCache<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskCache.class);
    public static final Object LOCK = new Object();
    private static final Connection CONN;
    private final String tableName;

    static {
        try {
            File path = new File(System.getProperty("user.home"), ".MultiSubDownloader");
            if (!path.exists() && !path.mkdir()) {
                throw new RuntimeException("Could not create folder " + path);
            }
            Class.forName("org.hsqldb.jdbcDriver");
            CONN = DriverManager.getConnection(
                    "jdbc:hsqldb:file:" + path.toString() + "/diskcache.hsqldb;hsqldb.write_delay=false;shutdown=true", "user", "pass");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load jdbcdriver for diskcache");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected DiskCache(Long timeToLive, Long timerInterval, Integer maxItems, String username, String password, String tableName) {
        super(timeToLive, timerInterval, maxItems);
        this.tableName = StringUtils.isBlank(tableName) ? "cacheobjects" : tableName;
        try {
            try (Statement stmt = CONN.createStatement()) {
                stmt.execute("create table IF NOT EXISTS %s (key OTHER, cacheobject OTHER);".formatted(this.tableName));
            }
            fillCacheMap();
            if (timeToLive != null && timeToLive > 0) {
                cleanup();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillCacheMap() {
        synchronized (LOCK) {
            try (Statement stmt = CONN.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT key, cacheobject FROM %s;".formatted(tableName));) {
                synchronized (getCacheMap()) {
                    while (rs.next()) {
                        put(diskObjectToKey(rs.getObject("key")), diskCacheObjectToValue(rs.getObject("cacheobject")));
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to insert object in disk cache!", e);
            }
        }
    }

    protected abstract K diskObjectToKey(Object key);

    protected abstract CacheObject<V> diskCacheObjectToValue(Object value);

    @Override
    public final void remove(K key) {
        super.remove(key);
        synchronized (LOCK) {
            try (PreparedStatement prep = CONN.prepareCall("delete from %s where key = ?".formatted(tableName))) {
                prep.clearParameters();
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
        synchronized (LOCK) {
            try (PreparedStatement prep = CONN.prepareCall("INSERT INTO %s (key,cacheobject) VALUES (?,?)".formatted(tableName))) {
                prep.clearParameters();
                prep.setObject(1, keyToDiskObject(key));
                synchronized (getCacheMap()) {
                    CacheObject<V> cacheObject = getCacheMap().get(key);
                    prep.setObject(2, cacheObjectToDiskObject(cacheObject));
                    prep.execute();
                }
                CONN.commit();
            } catch (SQLException e) {
                LOGGER.error("Unable to insert object in disk cache!", e);
            }
        }
    }
}