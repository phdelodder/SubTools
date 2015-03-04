package org.lodder.subtools.sublibrary.cache;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.lodder.subtools.sublibrary.logging.Logger;

public class DiskCache<K, T> extends InMemoryCache<K, T> {

  private Connection conn;

  public DiskCache(long crunchifyTimeToLive, long crunchifyTimerInterval, int maxItems,
      String username, String password) {
    super(crunchifyTimeToLive, crunchifyTimerInterval, maxItems);
    File path = new File(System.getProperty("user.home"), ".MultiSubDownloader");
    if (!path.exists()) {
      if (!path.mkdir()) {
        throw new RuntimeException("Could not create folder " + path);
      }
    }
    PreparedStatement prep = null;
    try {
      Class.forName("org.hsqldb.jdbcDriver");
      conn =
          DriverManager.getConnection("jdbc:hsqldb:file:" + path.toString()
              + "/diskcache.hsqldb;hsqldb.write_delay=false;shutdown=true", username, password);

      prep =
          conn.prepareStatement("create table IF NOT EXISTS cacheobjects (key OTHER, cacheobject OTHER);");
      prep.execute();
      prep.close();
      fillCacheMap();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Unable to load jdbcdriver for diskcache");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (prep != null) prep.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void fillCacheMap() {
    try (Statement stmt = conn.createStatement()) {
      ResultSet rs = stmt.executeQuery("SELECT key, cacheobject FROM cacheobjects");
      while (rs.next()) {
        cacheMap.put(rs.getObject("key"), rs.getObject("cacheobject"));
      }
      rs.close();
    } catch (SQLException e) {
      Logger.instance.error("Unable to insert object in disk cache!");
    } finally {

    }
  }

  public void remove(K key) {
    super.remove(key);
    try (PreparedStatement prep = conn.prepareCall("delete from cacheobjects where key = ?")) {
      prep.clearParameters();
      prep.setObject(1, key);
      prep.execute();
    } catch (SQLException e) {
      Logger.instance.error("Unable to delete object from disk cache!");
    }
  }

  public void put(K key, T value) {
    super.put(key, value);
    try (PreparedStatement prep =
        conn.prepareCall("INSERT INTO cacheobjects (key,cacheobject) VALUES (?,?)")) {
      prep.clearParameters();
      prep.setObject(1, key);
      CacheObject<?, ?> cacheObject = (CacheObject<?, ?>) cacheMap.get(key);
      prep.setObject(2, cacheObject);
      prep.execute();
    } catch (SQLException e) {
      Logger.instance.error("Unable to insert object in disk cache!");
    }
  }
}
