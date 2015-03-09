package org.lodder.subtools.sublibrary.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

public class Files {

  private static final int BUF_SIZE = 0x1000; // 4K

  public static void copy(File from, File to) throws IOException {
    try (FileInputStream input = new FileInputStream(from);
        FileOutputStream output = new FileOutputStream(to)) {
      byte[] buf = new byte[BUF_SIZE];
      while (true) {
        int r;

        r = input.read(buf);
        if (r == -1) {
          break;
        }
        output.write(buf, 0, r);
      }
    } catch (IOException e) {
      throw e;
    }
  }

  public static void move(File from, File to) throws IOException {
    if (!from.renameTo(to)) {
      copy(from, to);
      if (!from.delete()) {
        if (!to.delete()) {
          throw new IOException("Unable to delete " + to);
        }
        throw new IOException("Unable to delete " + from);
      }
    }
  }

  public static String read(File file) throws IOException {
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      return sb.toString();
    }
  }

  public static void write(File file, String content) throws IOException {
    try (FileOutputStream os = new FileOutputStream(file)) {
      byte[] bytesContent = content.getBytes("UTF-8");
      os.write(bytesContent);
    } catch (IOException e) {
      throw e;
    }
  }

  public static void unzip(InputStream inputStream, File outputFile, String extensionFilter)
      throws IOException {
    try (ZipInputStream zis = new ZipInputStream(inputStream)) {
      ZipEntry ze;
      while ((ze = zis.getNextEntry()) != null) {
        if (ze.getName().endsWith(extensionFilter)) {
          byte[] buff = new byte[1024];
          // get file name
          try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            int l = 0;
            // write buffer to file
            while ((l = zis.read(buff)) > 0) {
              fos.write(buff, 0, l);
            }
          } catch (IOException e) {
            throw e;
          }
        }
      }
    } catch (IOException e) {
      throw e;
    }

  }

  public static boolean isZipFile(File file) throws IOException {
    boolean isZip = false;

    if (file.isDirectory()) {
      return isZip;
    }
    if (!file.canRead()) {
      throw new IOException("Cannot read file " + file.getAbsolutePath());
    }
    if (file.length() < 4) {
      return isZip;
    }

    try (FileInputStream fis = new FileInputStream(file)) {
      isZip = isZipFile(fis);
    } catch (IOException e) {
      throw e;
    }

    return isZip;
  }

  public static boolean isZipFile(InputStream inputStream) throws IOException {
    int test = -1;
    try (DataInputStream in = new DataInputStream(new BufferedInputStream(inputStream))) {
      test = in.readInt();
    } catch (IOException e) {
      throw e;
    }

    return test == 0x504b0304;
  }

  /*
   * Determines if a byte array is compressed. The java.util.zip GZip implementaiton does not expose
   * the GZip header so it is difficult to determine if a string is compressed.
   * 
   * @param bytes an array of bytes
   * 
   * @return true if the array is compressed or false otherwise
   * 
   * @throws java.io.IOException if the byte array couldn't be read
   */
  public static boolean isGZipCompressed(byte[] bytes) throws IOException {
    if ((bytes == null) || (bytes.length < 2)) {
      return false;
    } else {
      return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
    }
  }

  public static void deleteEmptyFolders(File aStartingDir) throws FileNotFoundException {
    List<File> emptyFolders = new ArrayList<File>();
    findEmptyFoldersInDir(aStartingDir, emptyFolders);
    List<String> fileNames = new ArrayList<String>();
    for (File f : emptyFolders) {
      String s = f.getAbsolutePath();
      fileNames.add(s);
    }
    for (File f : emptyFolders) {
      boolean isDeleted = f.delete();
      if (isDeleted) {
        System.out.println(f.getPath() + " deleted");
      }
    }
  }

  public static boolean findEmptyFoldersInDir(File folder, List<File> emptyFolders) {
    boolean isEmpty = false;
    File[] filesAndDirs = folder.listFiles();
    List<File> filesDirs = Arrays.asList(filesAndDirs);
    if (filesDirs.size() == 0) {
      isEmpty = true;
    }
    if (filesDirs.size() > 0) {
      boolean allDirsEmpty = true;
      boolean noFiles = true;
      for (File file : filesDirs) {
        if (!file.isFile()) {
          boolean isEmptyChild = findEmptyFoldersInDir(file, emptyFolders);
          if (!isEmptyChild) {
            allDirsEmpty = false;
          }
        }
        if (file.isFile()) {
          noFiles = false;
        }
      }
      if (noFiles && allDirsEmpty) {
        isEmpty = true;
      }
    }
    if (isEmpty) {
      emptyFolders.add(folder);
    }
    return isEmpty;
  }

  public static byte[] decompressGZip(byte[] data) throws IOException {
    byte[] decompressedData = null;
    try (ByteArrayInputStream binput = new ByteArrayInputStream(data);
        GZIPInputStream gzinput = new GZIPInputStream(binput)) {
      decompressedData = IOUtils.toByteArray(gzinput);
    } catch (IOException e) {
      throw e;
    }
    return decompressedData;
  }

}
