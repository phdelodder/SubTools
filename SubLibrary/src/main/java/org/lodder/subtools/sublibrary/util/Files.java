package org.lodder.subtools.sublibrary.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Files {

  private static final int BUF_SIZE = 0x1000; // 4K

  public static void copy(File from, File to) throws IOException {
    FileInputStream input = new FileInputStream(from);
    FileOutputStream output = new FileOutputStream(to);
    byte[] buf = new byte[BUF_SIZE];
    while (true) {
      int r = input.read(buf);
      if (r == -1) {
        break;
      }
      output.write(buf, 0, r);
    }

    input.close();
    output.close();
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
    BufferedReader br = new BufferedReader(new FileReader(file));
    try {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      return sb.toString();
    } finally {
      br.close();
    }
  }
  
  public static void write(File file, String content) throws IOException{
    FileOutputStream os = new FileOutputStream(file);
    byte[] bytesContent = new byte[content.length()];
    bytesContent = content.getBytes();
    os.write(bytesContent);
    os.close();
  }

  public static void unzip(InputStream inputStream, File outputFile, String extensionFilter)
      throws FileNotFoundException, IOException {
    ZipInputStream zis = new ZipInputStream(inputStream);
    ZipEntry ze;
    while ((ze = zis.getNextEntry()) != null) {
      if (ze.getName().endsWith(extensionFilter)) {
        byte[] buff = new byte[1024];
        // get file name
        FileOutputStream fos = new FileOutputStream(outputFile);
        int l = 0;
        // write buffer to file
        while ((l = zis.read(buff)) > 0) {
          fos.write(buff, 0, l);
        }
        fos.close();
      }
    }
    zis.close();
  }

  public static boolean isZipFile(File file) throws IOException {
    if (file.isDirectory()) {
      return false;
    }
    if (!file.canRead()) {
      throw new IOException("Cannot read file " + file.getAbsolutePath());
    }
    if (file.length() < 4) {
      return false;
    }
    return isZipFile(new FileInputStream(file));
  }

  public static boolean isZipFile(InputStream inputStream) throws IOException {
    DataInputStream in = new DataInputStream(new BufferedInputStream(inputStream));
    int test = in.readInt();
    in.close();
    return test == 0x504b0304;
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
      if (noFiles == true && allDirsEmpty == true) {
        isEmpty = true;
      }
    }
    if (isEmpty) {
      emptyFolders.add(folder);
    }
    return isEmpty;
  }

}
