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

  private final File path;
  private Connection conn;

  public DiskCache(long crunchifyTimeToLive, long crunchifyTimerInterval, int maxItems, String username, String password) {
    super(crunchifyTimeToLive, crunchifyTimerInterval, maxItems);
    path = new File(System.getProperty("user.home"), ".MultiSubDownloader");
    if (!path.exists()) {
      if (!path.mkdir()) {
        throw new RuntimeException("Could not create folder " + path);
      }
    }
    try {
      Class.forName("org.hsqldb.jdbcDriver");
      conn =
          DriverManager.getConnection("jdbc:hsqldb:file:" + path.toString()
              + "/diskcache.hsqldb;hsqldb.write_delay=false;shutdown=true", username, password);

      PreparedStatement prep =
          conn.prepareStatement("create table IF NOT EXISTS cacheobjects (key LONGVARCHAR not null, value LONGVARCHAR);");
      prep.execute();
      prep.close();
      fillCacheMap();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Unable to load jdbcdriver for diskcache");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private void fillCacheMap() {
    try (Statement stmt = conn.createStatement()) {
      ResultSet rs = stmt.executeQuery("SELECT key, value FROM cacheobjects");
      while (rs.next()) {
        super.put((K) rs.getString("key"), (T) rs.getString("value"));
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
      prep.setString(1, (String) key);
      prep.execute();
    } catch (SQLException e) {
      Logger.instance.error("Unable to delete object from disk cache!");
    }
  }

  public void put(K key, T value) {
    super.put(key, value);
    try (PreparedStatement prep =
        conn.prepareCall("INSERT INTO cacheobjects (key,value) VALUES (?,?)")) {
      prep.clearParameters();
      prep.setString(1, (String) key);
      prep.setString(2, (String) value);
      prep.execute();
    } catch (SQLException e) {
      Logger.instance.error("Unable to insert object in disk cache!");
    }
  }
}
