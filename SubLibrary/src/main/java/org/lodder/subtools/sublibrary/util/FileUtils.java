package org.lodder.subtools.sublibrary.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.ExtensionMethod;
import name.falgout.jeffrey.throwing.ThrowingConsumer;
import name.falgout.jeffrey.throwing.ThrowingFunction;

@ExtensionMethod({ StreamExtension.class })
public class FileUtils {

    public static String getExtension(Path path) {
        return StringUtils.substringAfterLast(path.getFileName().toString(), ".");
    }

    public static boolean hasExtension(Path path, String extension) {
        return extension.equalsIgnoreCase(getExtension(path));
    }

    public static String changeExtension(Path path, String newExtension) {
        return StringUtils.substringBeforeLast(path.getFileName().toString(), ".") + "." + newExtension;
    }

    public static String withoutExtension(Path path) {
        return changeExtension(path, "");
    }

    public static String withoutExtension(String path) {
        return StringUtils.substringBeforeLast(path, ".");
    }

    public static String toAbsolutePathAsString(Path path) {
        return path.toAbsolutePath().toString();
    }

    /**
     * Moves a file or a complete directory tree.
     * <p>
     * This method moves the given {@link Path} to the specified destination. Depending on whether
     * the path is a directory or a regular file, the behavior of the method is as follows:
     * <ul>
     * <li>If the {@link Path} is a directory, it will recursively move all files and subdirectories
     * within the directory hierarchy, starting from and including the given path, to the
     * destination.</li>
     * <li>If the {@link Path} is a regular file, it will be moved to the destination file.</li>
     * </ul>
     * <p>
     * The method also allows you to specify optional move options, such as whether to replace an
     * existing file or directory, using the {@link StandardCopyOption} enum. These options are
     * applied to all files and directories being moved.
     *
     * @param source
     *        the path to be moved
     * @param destinationDir
     *        the destination directory
     * @param copyOptions
     *        optional move options to apply while moving the path
     * @return the destination
     * @throws IOException
     *         if an I/O error occurs while moving the path
     */
    public static Path moveToDir(Path source, Path destinationDir, StandardCopyOption... copyOptions) throws IOException {
        return moveToDirAndRename(source, destinationDir, source.getFileName().toString(), copyOptions);
    }

    /**
     * Moves a file or a complete directory tree.
     * <p>
     * This method moves the given {@link Path} to the specified destination. Depending on whether
     * the path is a directory or a regular file, the behavior of the method is as follows:
     * <ul>
     * <li>If the {@link Path} is a directory, it will recursively move all files and subdirectories
     * within the directory hierarchy, starting from and including the given path, to the
     * destination.</li>
     * <li>If the {@link Path} is a regular file, it will be moved to the destination file.</li>
     * </ul>
     * <p>
     * The moved path is also renamed to the provided new name.
     * The method also allows you to specify optional move options, such as whether to replace an
     * existing file or directory, using the {@link StandardCopyOption} enum. These options are
     * applied to all files and directories being moved.
     *
     * @param source
     *        the path to be moved
     * @param destinationDir
     *        the destination directory
     * @param newFileName
     *        the new file name
     * @param copyOptions
     *        optional move options to apply while moving the path
     * @return the destination
     * @throws IOException
     *         if an I/O error occurs while moving the path
     */
    public static Path moveToDirAndRename(Path source, Path destinationDir, String newFileName, StandardCopyOption... copyOptions)
            throws IOException {
        Files.createDirectories(destinationDir);
        if (Files.isRegularFile(source)) {
            Files.move(source, destinationDir.resolve(newFileName), copyOptions);
        } else {
            try {
                Files.move(source, destinationDir.resolve(newFileName), copyOptions);
            } catch (DirectoryNotEmptyException e) {
                // happens when moving a non-empty folder to another drive
                moveNonEmptyDirectory(source, destinationDir, copyOptions);
            }
        }
        return destinationDir.resolve(newFileName);
    }

    private static Path moveNonEmptyDirectory(Path sourceDir, Path targetDir, StandardCopyOption... copyOptions) throws IOException {
        if (Files.isDirectory(sourceDir)) {
            return moveNonEmptyDirectoryRecursively(sourceDir, targetDir, copyOptions);
        } else {
            return moveToDir(sourceDir, targetDir, copyOptions);
        }
    }

    private static Path moveNonEmptyDirectoryRecursively(Path source, Path target, StandardCopyOption... copyOptions) throws IOException {
        foreachSubfile(source, s -> s.asThrowingStream(IOException.class)
                .forEach(child -> moveNonEmptyDirectory(child, target.resolve(source.getFileName()), copyOptions)));
        Files.delete(source);
        return target;
    }

    /**
     * Deletes a {@link Path}.
     * <p>
     * If the {@link Path} exists, this method will delete it without any possibility of recovery.
     * This method behaves as follows:
     * <ul>
     * <li>If the {@link Path} is a directory, it will recursively delete all files and
     * subdirectories within the directory, starting from and including the given path.</li>
     * <li>If the {@link Path} is a regular file, it will be deleted.</li>
     * </ul>
     *
     * @param path
     *        the path to delete
     * @throws IOException
     *         if an I/O error occurs while deleting the path
     */
    // TODO change name? (nameclash)
    public static void delete(Path path) throws IOException {
        Files.walkFileTree(path, new DeleteDirVisitor());
    }

    /**
     * Copies a file or a complete directory tree.
     * <p>
     * This method copies the given {@link Path} to the specified destination. Depending on whether
     * the path is a directory or a regular file, the behavior of the method is as follows:
     * <ul>
     * <li>If the {@link Path} is a directory, it will recursively copy all files and subdirectories
     * within the directory hierarchy, starting from and including the given path, to the
     * destination.</li>
     * <li>If the {@link Path} is a regular file, it will be copied to the destination file.</li>
     * </ul>
     * <p>
     * The method also allows you to specify optional copy options, such as whether to replace an
     * existing file or directory, using the {@link StandardCopyOption} enum. These options are
     * applied to all files and directories being copied.
     *
     * @param source
     *        the path to be copied
     * @param destinationDir
     *        the destination directory
     * @param copyOptions
     *        optional copy options to apply while copying the path
     * @return the location of the copied path
     * @throws IOException
     *         if an I/O error occurs while deleting the path
     */
    public static Path copyToDir(Path source, Path destinationDir, StandardCopyOption... copyOptions) throws IOException {
        return copyToDirAndRename(source, destinationDir, source.getFileName().toString(), copyOptions);
    }

    /**
     * Copies a file or a complete directory tree.
     * <p>
     * This method copies the given {@link Path} to the specified destination. Depending on whether
     * the path is a directory or a regular file, the behavior of the method is as follows:
     * <ul>
     * <li>If the {@link Path} is a directory, it will recursively copy all files and subdirectories
     * within the directory hierarchy, starting from and including the given path, to the
     * destination.</li>
     * <li>If the {@link Path} is a regular file, it will be copied to the destination file.</li>
     * </ul>
     * <p>
     * The copied path is also renamed to the provided new name.
     * The method also allows you to specify optional copy options, such as whether to replace an
     * existing file or directory, using the {@link StandardCopyOption} enum. These options are
     * applied to all files and directories being copied.
     *
     * @param source
     *        the path to be copied
     * @param destinationDir
     *        the destination directory
     * @param newFileName
     *        the new file name
     * @param copyOptions
     *        optional copy options to apply while copying the path
     * @return the location of the copied path
     * @throws IOException
     *         if an I/O error occurs while deleting the path
     */
    public static Path copyToDirAndRename(Path source, Path destinationDir, String newFileName, StandardCopyOption... copyOptions)
            throws IOException {
        if (Files.isRegularFile(source)) {
            Files.createDirectories(destinationDir);
            Path destinationFile = destinationDir.resolve(newFileName);
            Files.copy(source, destinationFile, copyOptions);
            return destinationFile;
        } else {
            Path destination = destinationDir.resolve(newFileName);
            Files.createDirectories(destination);
            Files.walkFileTree(source, new CopyDirVisitor(source, destination, copyOptions));
            return destination;
        }
    }

    public static String getFileNameAsString(Path path) {
        return path.getFileName().toString();
    }

    public static boolean fileNameContains(Path path, String text) {
        return path.getFileName().toString().contains(text);
    }

    public static boolean fileNameContainsIgnoreCase(Path path, String text) {
        return StringUtils.containsIgnoreCase(path.getFileName().toString(), text);
    }

    public static boolean isEmptyDir(Path path) throws IOException {
        requireDir(path);
        return applySubfiles(path, children -> children.findAny().isEmpty());
    }

    public static <T, X extends Exception> T applySubfiles(Path path, ThrowingFunction<Stream<Path>, T, X> function) throws IOException, X {
        try (Stream<Path> pathStream = Files.list(path)) {
            return function.apply(pathStream);
        }
    }

    public static <X extends Exception> void foreachSubfile(Path path, ThrowingConsumer<Stream<Path>, X> consumer) throws IOException, X {
        try (Stream<Path> pathStream = Files.list(path)) {
            consumer.accept(pathStream);
        }
    }

    public static void requireDir(Path path) {
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("[%s] is not a directory".formatted(path));
        }
    }

    /////////////////////

    public static void unzip(InputStream inputStream, Path outputFile, String extensionFilter) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.getName().endsWith(extensionFilter)) {
                    byte[] buff = new byte[1024];
                    // get file name
                    try (OutputStream fos = Files.newOutputStream(outputFile)) {
                        int l;
                        // write buffer to file
                        while ((l = zis.read(buff)) > 0) {
                            fos.write(buff, 0, l);
                        }
                    }
                }
            }
        }

    }

    public static boolean isZipFile(InputStream inputStream) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(inputStream))) {
            return in.readInt() == 0x504b0304;
        }
    }

    /*
     * Determines if a byte array is compressed. The java.util.zip GZip
     * implementation does not expose the GZip header so it is difficult to
     * determine if a string is compressed.
     *
     * @param bytes an array of bytes
     *
     * @return true if the array is compressed or false otherwise
     *
     * @throws java.io.IOException if the byte array couldn't be read
     */
    public static boolean isGZipCompressed(byte[] bytes) {
        if (bytes == null || bytes.length < 2) {
            return false;
        } else {
            return bytes[0] == (byte) GZIPInputStream.GZIP_MAGIC
                    && bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8);
        }
    }

    public static byte[] decompressGZip(byte[] data) throws IOException {
        try (ByteArrayInputStream binput = new ByteArrayInputStream(data);
                GZIPInputStream gzinput = new GZIPInputStream(binput)) {
            return gzinput.readAllBytes();
        }
    }
}
