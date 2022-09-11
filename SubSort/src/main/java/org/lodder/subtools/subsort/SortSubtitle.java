package org.lodder.subtools.subsort;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.io.FileUtils;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.DiskCache;
import org.lodder.subtools.sublibrary.cache.InMemoryCache;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.privateRepo.PrivateRepoIndex;
import org.lodder.subtools.sublibrary.privateRepo.model.IndexSubtitle;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.http.CookieManager;
import org.lodder.subtools.sublibrary.util.http.HttpClient;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.lodder.subtools.subsort.lib.control.VideoFileFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SortSubtitle {

    private static final String BACKING_STORE_AVAIL = "BackingStoreAvail";
    private final Preferences preferences;
    private static Manager manager = new Manager();

    private static final Logger LOGGER = LoggerFactory.getLogger(SortSubtitle.class);

    public SortSubtitle() {
        DiskCache<String, String> diskCache = new DiskCache<>(TimeUnit.SECONDS.convert(5, TimeUnit.DAYS), 100, 500, "user", "pass");
        manager.setDiskCache(diskCache);
        InMemoryCache<String, String> inMemoryCache = new InMemoryCache<>(TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES), 10, 500);
        manager.setInMemoryCache(inMemoryCache);
        HttpClient httpClient = new HttpClient();
        httpClient.setCookieManager(new CookieManager());
        manager.setHttpClient(httpClient);

        if (!backingStoreAvailable()) {
            LOGGER.error("Unable to store preferences, used debug for reason");
        }
        preferences = Preferences.userRoot().node("MultiSubDownloader");
    }

    private static boolean backingStoreAvailable() {
        Preferences prefs = Preferences.userRoot().node("MultiSubDownloader");
        try {
            boolean oldValue = prefs.getBoolean(BACKING_STORE_AVAIL, false);
            prefs.putBoolean(BACKING_STORE_AVAIL, !oldValue);
            prefs.flush();
        } catch (BackingStoreException e) {
            LOGGER.error("", e);
            return false;
        }
        return true;
    }

    public void reBuildIndex(File outputDir) {
        final JTheTVDBAdapter jtvdb = JTheTVDBAdapter.getAdapter(manager);
        List<File> files = getFileListing(outputDir);
        List<IndexSubtitle> index = new ArrayList<>();
        File indexLoc = new File(outputDir, "index");
        int x = 0;
        for (File file : files) {
            try {
                x++;
                LOGGER.info("threathing file {} of {} : {}", x, files.size(), file);

                // Compare if there is no duplicate, if there is duplicate, move to dup folder

                boolean skip = false;
                for (File file2 : files) {
                    try {
                        if (file2.getName().equals(file.getName())) {
                            continue;
                        }

                        boolean compareResults = FileUtils.contentEquals(file, file2);
                        if (compareResults) {
                            LOGGER.info("file {} is the same as, file2 {} deleting .... ", file, file2);
                            file.delete();
                            skip = true;
                            break;
                        }
                    } catch (IOException e) {
                        LOGGER.error("contentEquals", e);
                    }
                }

                if (skip) {
                    continue;
                }

                Release release = VideoFileFactory.get(file, new ArrayList<MappingTvdbScene>(), manager);
                if (release.getVideoType() == VideoType.EPISODE) {
                    TvRelease tvRelease = (TvRelease) release;

                    int tvdbid = 0;
                    // for (MappingTvdbScene mapping : mappingSettingsCtrl.getMappingSettings()
                    // .getMappingList()) {
                    // if (mapping.getSceneName().replaceAll("[^A-Za-z]", "")
                    // .equalsIgnoreCase(tvRelease.getShowName().replaceAll("[^A-Za-z]", ""))) {
                    // tvdbid = mapping.getTvdbId();
                    // break;
                    // }
                    // }

                    TheTVDBSerie thetvdbserie = tvdbid == 0 ? jtvdb.getSerie(tvRelease) : jtvdb.getSerie(tvdbid);

                    if (thetvdbserie != null) {
                        LOGGER.info("Got serie info: {} ", thetvdbserie.getId());
                        final String show = replaceWindowsChars(thetvdbserie.getSerieName());
                        String path = outputDir + File.separator + show + File.separator + tvRelease.getSeason();
                        Optional<Language> language =
                                Optional.ofNullable(Language.fromValueOptional(file.getParent()).orElseGet(() -> DetectLanguage.execute(file)));
                        if (language.isEmpty()) {
                            LOGGER.error("Unable to detect language, leaving language code blank");
                        }
                        for (int i = 0; i < tvRelease.getEpisodeNumbers().size(); i++) {
                            final File pathFolder = new File(path + File.separator + tvRelease.getEpisodeNumbers().get(i)
                                    + File.separator + language.map(lang -> lang.getName() + File.separator).orElse(""));
                            final File to = new File(pathFolder, release.getFilename());
                            if (to.exists()) {
                                index.add(new IndexSubtitle(show, tvRelease.getSeason(),
                                        tvRelease.getEpisodeNumbers().get(i),
                                        PrivateRepoIndex.extractOriginalFilename(release.getFilename()), language.orElse(null),
                                        Integer.parseInt(thetvdbserie.getId()),
                                        PrivateRepoIndex.extractUploader(release.getFilename()),
                                        PrivateRepoIndex.extractOriginalSource(release.getFilename()),
                                        release.getVideoType()));
                            } else {
                                System.out.println("doesn't exists: " + to.toString());
                            }
                        }
                    }
                } else if (release.getVideoType() == VideoType.MOVIE) {
                    MovieRelease movieRelease = (MovieRelease) release;
                    Optional<Language> language = DetectLanguage.executeOptional(file);
                    String filename = removeLanguageCode(release.getFilename(), language.orElse(null));
                    String title = replaceWindowsChars(movieRelease.getTitle());

                    final File pathFolder = new File(outputDir + File.separator + "movies" + File.separator + title + " " + movieRelease.getYear()
                            + File.separator + language.map(lang -> lang.getName() + File.separator).orElse(""));

                    File to = new File(pathFolder, filename);

                    if (to.exists()) {
                        index.add(new IndexSubtitle(title, PrivateRepoIndex.extractOriginalFilename(filename),
                                language.orElse(null), PrivateRepoIndex.extractUploader(filename),
                                PrivateRepoIndex.extractOriginalSource(filename), release.getVideoType(),
                                movieRelease.getImdbId(), movieRelease.getYear()));
                    } else {
                        System.out.println("doesn't exists: " + to.toString());
                    }
                }
            } catch (ControlFactoryException | ReleaseParseException | ReleaseControlException e) {
                LOGGER.error("", e);
            }
        }

        File indexVersionLoc = new File(outputDir, "index.version");
        writeIndex(PrivateRepoIndex.setIndex(index), indexLoc, indexVersionLoc);
    }

    private void writeIndex(String xml, File indexLoc, File indexVersionLoc) {

        try {
            int indexVersion = 0;
            if (indexVersionLoc.exists()) {
                indexVersion = Integer.parseInt(Files.read(indexVersionLoc).trim());
            }

            if (!xml.equals(Files.read(indexLoc))) {
                Files.write(indexLoc, xml);
                indexVersion++;
                Files.write(indexVersionLoc, Integer.toString(indexVersion).trim());
            }

        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    public void run(boolean remove, File inputDir, File outputDir, boolean cleanUp) {
        List<File> files = getFileListing(inputDir);
        List<IndexSubtitle> index = new ArrayList<>();
        File indexLoc = new File(outputDir, "index");
        if (indexLoc.exists()) {
            String strIndex = "";
            try {
                strIndex = Files.read(indexLoc);
                index = PrivateRepoIndex.getIndex(strIndex);
            } catch (IOException e) {
                index = new ArrayList<>();
                LOGGER.error("", e);
            }
        }
        for (File file : files) {
            try {
                Release release = VideoFileFactory.get(file, new ArrayList<MappingTvdbScene>(), manager);
                final JTheTVDBAdapter jtvdb = JTheTVDBAdapter.getAdapter(manager);
                final String quality = ReleaseParser.getQualityKeyword(release.getFilename());
                LOGGER.info(release.getFilename() + " Q: " + quality);
                int num = 1;
                if (quality.split(" ").length == 1) {
                    Console c = System.console();
                    String selectedSubtitle = c.readLine("Sure? Press 1 for ok, press 2 for remove: ");
                    try {
                        num = Integer.parseInt(selectedSubtitle);
                    } catch (Exception e) {
                        num = -1;
                    }
                }
                if (release.getVideoType() == VideoType.EPISODE && !quality.isEmpty() && num == 1) {
                    TvRelease tvRelease = (TvRelease) release;

                    int tvdbid = 0;
                    // for (MappingTvdbScene mapping : mappingSettingsCtrl.getMappingSettings()
                    // .getMappingList()) {
                    // if (mapping.getSceneName().replaceAll("[^A-Za-z]", "")
                    // .equalsIgnoreCase(tvRelease.getShowName().replaceAll("[^A-Za-z]", ""))) {
                    // tvdbid = mapping.getTvdbId();
                    // }
                    // }

                    TheTVDBSerie thetvdbserie = null;
                    if (tvdbid == 0) {
                        thetvdbserie = jtvdb.getSerie(tvRelease);
                    } else {
                        thetvdbserie = jtvdb.getSerie(tvdbid);
                    }

                    if (thetvdbserie != null) {

                        final String show = replaceWindowsChars(thetvdbserie.getSerieName());
                        String path =
                                outputDir + File.separator + show + File.separator + tvRelease.getSeason();
                        Optional<Language> language = DetectLanguage.executeOptional(file);
                        for (int i = 0; i < tvRelease.getEpisodeNumbers().size(); i++) {
                            final File pathFolder = new File(path + File.separator + tvRelease.getEpisodeNumbers().get(i) + File.separator
                                    + language.map(lang -> lang.getName() + File.separator).orElse(""));
                            if (!pathFolder.exists() && !pathFolder.mkdirs()) {
                                throw new IOException("Download unable to create folder: " + pathFolder.getAbsolutePath());
                            }
                            final String filename = removeLanguageCode(release.getFilename(), language.orElse(null));

                            File to = new File(pathFolder, filename);
                            int j = 1;
                            while (to.exists()) {
                                if (textFilesEqual(to, file)) {
                                    LOGGER.info("Duplicate file detected with exact same content, file deleted! [{}]",
                                            to.getName());
                                    boolean isDeleted = file.delete();
                                    if (isDeleted) {
                                        // do nothing
                                    }
                                } else {
                                    LOGGER.info(
                                            "Duplicate file detected but content is different, creating new version! [{}/{}] with [{}]",
                                            release.getPath(), release.getFilename(), to);
                                    // elevate version number
                                    to = new File(pathFolder, filename + "V" + j);
                                    j++;
                                }
                            }
                            if (remove && i == tvRelease.getEpisodeNumbers().size() - 1) {
                                Files.move(file, to);
                            } else {
                                Files.copy(file, to);
                            }
                            index.add(new IndexSubtitle(show, tvRelease.getSeason(),
                                    tvRelease.getEpisodeNumbers().get(i),
                                    PrivateRepoIndex.extractOriginalFilename(filename), language.orElse(null),
                                    Integer.parseInt(thetvdbserie.getId()),
                                    PrivateRepoIndex.extractUploader(filename),
                                    PrivateRepoIndex.extractOriginalSource(filename), release.getVideoType()));
                        }
                    } else {

                        // if (num == 2) {
                        // boolean isDeleted = file.delete();
                        // if (isDeleted) {
                        // // do nothing
                        // }
                        // } else {
                        // LOGGER.info("Skip");
                        // }
                    }
                } else if (release.getVideoType() == VideoType.MOVIE) {
                    MovieRelease movieRelease = (MovieRelease) release;
                    String title = replaceWindowsChars(movieRelease.getTitle());
                    Optional<Language> language = DetectLanguage.executeOptional(file);
                    final File pathFolder = new File(outputDir + File.separator + "movies" + File.separator
                            + title + " " + movieRelease.getYear() + File.separator
                            + language.map(lang -> lang.getName() + File.separator).orElse(""));

                    if (!pathFolder.exists() && !pathFolder.mkdirs()) {
                        throw new IOException("Download unable to create folder: " + pathFolder.getAbsolutePath());
                    }

                    String filename = removeLanguageCode(release.getFilename(), language.orElse(null));
                    File to = new File(pathFolder, filename);

                    int j = 1;
                    while (to.exists()) {
                        if (textFilesEqual(to, file)) {
                            LOGGER.info("Duplicate file detected with exact same content, file deleted! [{}]",
                                    to.getName());
                            boolean isDeleted = file.delete();
                            if (isDeleted) {
                                // do nothing
                            }
                        } else {
                            LOGGER.info(
                                    "Duplicate file detected but content is different, creating new version! [{}/{}] with [{}]",
                                    release.getPath(), release.getFilename(), to);
                            // elevate version number
                            to = new File(pathFolder, filename + "V" + j);
                            j++;
                        }
                    }
                    if (remove) {
                        Files.move(file, to);
                    } else {
                        Files.copy(file, to);
                    }

                    IndexSubtitle indexSubtitle =
                            new IndexSubtitle(title, PrivateRepoIndex.extractOriginalFilename(filename), language.orElse(null),
                                    PrivateRepoIndex.extractUploader(filename),
                                    PrivateRepoIndex.extractOriginalSource(filename), release.getVideoType(),
                                    movieRelease.getImdbId(), movieRelease.getYear());

                    index.add(indexSubtitle);
                }

            } catch (IOException | ControlFactoryException | ReleaseParseException | ReleaseControlException e) {
                LOGGER.error(file.toString(), e);
            }
        }
        if (cleanUp) {
            LOGGER.info("Performing clean up!");
            Files.deleteEmptyFolders(inputDir);
        }

        Iterator<IndexSubtitle> i = index.iterator();
        while (i.hasNext()) {
            IndexSubtitle indexSubtitle = i.next();
            String t = "";
            if (indexSubtitle.getVideoType() == VideoType.EPISODE) {
                t = indexSubtitle.getName() + File.separator + indexSubtitle.getSeason() + File.separator
                        + indexSubtitle.getEpisode() + File.separator + indexSubtitle.getLanguage()
                        + File.separator + PrivateRepoIndex.getFullFilename(indexSubtitle);
            } else if (indexSubtitle.getVideoType() == VideoType.MOVIE) {
                t = "movies" + File.separator + indexSubtitle.getName() + " " + indexSubtitle.getYear()
                        + File.separator + indexSubtitle.getLanguage() + File.separator
                        + PrivateRepoIndex.getFullFilename(indexSubtitle);
            }

            File temp = new File(outputDir, t);
            if (!temp.exists()) {
                LOGGER.info("clean up index " + t);
                i.remove();
            }
        }

        File indexVersionLoc = new File(outputDir, "index.version");
        writeIndex(PrivateRepoIndex.setIndex(index), indexLoc, indexVersionLoc);
    }

    public static ArrayList<MappingTvdbScene> getOnlineMappingCollection() throws Throwable {
        String content = manager.downloadText(
                "https://dl.dropboxusercontent.com/sh/1gz18xwzinfgmbl/wTwbjRsxS3/Mappings.xml?dl=1&token_hash=AAGng0oyYrOp0QA6ANd_VNLjtBqHaJxM0kn5E3RUx21XLQ");
        return read(XMLHelper.getDocument(content));
    }

    public static ArrayList<MappingTvdbScene> read(Document newDoc) throws Throwable {
        ArrayList<MappingTvdbScene> list = new ArrayList<>();
        NodeList nList = newDoc.getElementsByTagName("mapping");

        for (int i = 0; i < nList.getLength(); i++) {
            if (nList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                int tvdbid = XMLHelper.getIntTagValue("tvdbid", (Element) nList.item(i));
                String scene = XMLHelper.getStringTagValue("scene", (Element) nList.item(i));
                MappingTvdbScene item = new MappingTvdbScene(scene, tvdbid);
                list.add(item);
            }
        }
        return list;
    }

    private String removeLanguageCode(String filename, Language language) {
        int lastDot = filename.lastIndexOf(".");
        String ext = filename.substring(lastDot + 1);
        String temp = filename.substring(0, lastDot);
        lastDot = temp.lastIndexOf(".");
        if (lastDot != -1) {
            String baseFileName = temp.substring(0, lastDot);
            temp = temp.substring(lastDot + 1);
            if (language != null) {
                if (language == Language.DUTCH) {
                    if ("nld".equalsIgnoreCase(temp) || "ned".equalsIgnoreCase(temp)) {
                        return baseFileName + "." + ext;
                    }
                } else if (language == Language.ENGLISH && "eng".equalsIgnoreCase(temp)) {
                    return baseFileName + "." + ext;
                }
            }
        }
        return filename;
    }

    private List<File> getFileListing(File dir) {
        final List<File> filelist = new ArrayList<>();
        final File[] contents = dir.listFiles();
        if (contents != null) {
            for (final File file : contents) {
                if (file.isFile()) {
                    if (isValidSubtitleFile(file)) {
                        filelist.add(file);
                    }
                } else {
                    filelist.addAll(getFileListing(file));
                }
            }
        }
        return filelist;
    }

    private boolean isValidSubtitleFile(File file) {
        final String filename = file.getName();
        final int mid = filename.lastIndexOf(".");
        final String ext = filename.substring(mid + 1);

        String allowedExtension = "srt";
        return ext.equalsIgnoreCase(allowedExtension);

    }

    private boolean textFilesEqual(File f1, File f2) {
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        String y = "", z = "";

        try (BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(f1), StandardCharsets.UTF_8));
                BufferedReader bfr1 = new BufferedReader(new InputStreamReader(new FileInputStream(f2), StandardCharsets.UTF_8))) {

            while ((y = bfr.readLine()) != null) {
                builder1.append(y);
            }
            while ((z = bfr1.readLine()) != null) {
                builder2.append(z);
            }

            return builder2.toString().equals(builder1.toString());

        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return false;
    }

    static String replaceWindowsChars(String text) {
        text = text.replace("|", "");
        text = text.replace("\"", "");
        text = text.replace("<", "");
        text = text.replace(">", "");
        text = text.replace("?", "");
        text = text.replace("*", "");
        text = text.replace(":", "");
        text = text.replace("/", "");
        text = text.replace("\\", "");
        if (".".equals(text.substring(text.length() - 1))) {
            text = text.substring(0, text.length() - 1);
        }
        return text.trim();
    }

    /**
     * @param toRemove
     * @param privateRepoFolder
     */
    public void removeFromRepo(File toRemove, File privateRepoFolder) {
        if (toRemove.exists()) {
            File indexLoc = new File(privateRepoFolder, "index");
            List<IndexSubtitle> index;
            if (indexLoc.exists()) {
                String strIndex = "";
                try {
                    strIndex = Files.read(indexLoc);
                    index = PrivateRepoIndex.getIndex(strIndex);
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
                index = PrivateRepoIndex.getIndex(strIndex);

                Iterator<IndexSubtitle> i = index.iterator();
                while (i.hasNext()) {
                    IndexSubtitle indexSubtitle = i.next();
                    if (PrivateRepoIndex.getFullFilename(indexSubtitle).equals(toRemove.getName())) {
                        i.remove();
                        boolean isDeleted = toRemove.delete();
                        if (isDeleted) {
                            // do nothing
                        }
                    }
                }

                File indexVersionLoc = new File(privateRepoFolder, "index.version");
                writeIndex(PrivateRepoIndex.setIndex(index), indexLoc, indexVersionLoc);
            }

        }

    }
}
