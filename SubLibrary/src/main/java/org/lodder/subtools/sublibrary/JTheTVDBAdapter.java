package org.lodder.subtools.sublibrary;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import javax.swing.JOptionPane;

import org.lodder.subtools.sublibrary.data.thetvdb.TheTVDBApiV2;
import org.lodder.subtools.sublibrary.data.thetvdb.TheTVDBException;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class JTheTVDBAdapter {

    private TheTVDBApiV2 jtvapi;
    private static JTheTVDBAdapter adapter;
    private String exceptions = "";
    private final static String splitValue = ": '";
    private static final Logger LOGGER = LoggerFactory.getLogger(JTheTVDBAdapter.class);

    JTheTVDBAdapter(Manager manager) {
        this.jtvapi = null;
        try {
            this.jtvapi = new TheTVDBApiV2("A1720D2DDFDCE82D");
            exceptions = manager.downloadText2("https://raw.githubusercontent.com/midgetspy/sb_tvdb_scene_exceptions/gh-pages/exceptions.txt");
        } catch (TheTVDBException | ManagerException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public Optional<TheTVDBSerie> searchSerie(TvRelease episode) {
        try {
            return this.jtvapi.searchSerie(episode.getName(), null)
                    .orElseMap(() -> askUserToEnterTvdbId(episode.getName()))
                    .mapToOptionalObj(tvdbId -> jtvapi.getSerie(tvdbId, null));
        } catch (TheTVDBException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<TheTVDBEpisode> getEpisode(TvRelease episode) {
        try {
            return this.jtvapi.getEpisode(episode.getTvdbId(), episode.getSeason(), episode.getEpisodeNumbers().get(0), Language.ENGLISH);
        } catch (TheTVDBException e) {
            LOGGER.error(e.getMessage() + " " + episode.getName(), e);
            return Optional.empty();
        }
    }

    public Optional<TheTVDBSerie> getSerie(TvRelease episode) {
        try {
            if (episode.getTvdbId() > 0) {
                return this.jtvapi.getSerie(episode.getTvdbId(), null);
            } else {
                return sickbeardTVDBSceneExceptions(episode.getName())
                        .mapOrElseGet(tvdbId -> jtvapi.getSerie(tvdbId, null), () -> searchSerie(episode));
            }
        } catch (TheTVDBException e) {
            LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<TheTVDBSerie> getSerie(int tvdbid) {
        try {
            return this.jtvapi.getSerie(tvdbid, null);
        } catch (TheTVDBException e) {
            LOGGER.error("getSerie exception", e);
            return Optional.empty();
        }
    }

    public List<TheTVDBEpisode> getAllEpisodes(int tvdbid, Language language) {
        try {
            return this.jtvapi.getAllEpisodes(tvdbid, language);
        } catch (TheTVDBException e) {
            LOGGER.error("getAllEpisodes exception", e);
            return List.of();
        }
    }

    private OptionalInt sickbeardTVDBSceneExceptions(String serieName) {
        if (exceptions.isEmpty()) {
            return OptionalInt.empty();
        }
        for (String exception : exceptions.split("\n")) {
            int tvdbid = Integer.parseInt(exception.split(splitValue)[0].trim());
            String[] names = exception.split(splitValue)[1].split(",");
            for (String name : names) {
                String a = name.replaceAll("[^A-Za-z]", "");
                String b = serieName.replaceAll("[^A-Za-z]", "");
                if (!a.isEmpty() && a.equals(b)) {
                    return OptionalInt.of(tvdbid);
                }
            }
        }
        return OptionalInt.empty();
    }

    public synchronized static JTheTVDBAdapter getAdapter(Manager manager) {
        if (adapter == null) {
            adapter = new JTheTVDBAdapter(manager);
        }
        return adapter;
    }

    private OptionalInt askUserToEnterTvdbId(String showName) {
        LOGGER.error("Unknown serie name in tvdb: " + showName);
        String tvdbidString = JOptionPane.showInputDialog(null, "Enter tvdb id for serie " + showName);
        if (tvdbidString == null) {
            return OptionalInt.empty();
        }
        try {
            return OptionalInt.of(Integer.parseInt(tvdbidString));
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid tvdb id: " + tvdbidString);
            return askUserToEnterTvdbId(showName);
        }
    }
}
