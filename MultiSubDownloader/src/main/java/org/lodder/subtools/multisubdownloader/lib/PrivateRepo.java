package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.control.VideoFileParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.privateRepo.PrivateRepoIndex;
import org.lodder.subtools.sublibrary.privateRepo.model.IndexSubtitle;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.http.DropBoxClient;

public class PrivateRepo {

	private List<IndexSubtitle> index = new ArrayList<IndexSubtitle>();
	private String indexUrl = "/Ondertitels/PrivateRepo/index";
	private String indexVersionUrl = "/Ondertitels/PrivateRepo/index.version";
	private static PrivateRepo privateRepo;
	private final File path = new File(System.getProperty("user.home"),
			".MultiSubDownloader");
	private final File rVersion = new File(path, "rVersion");
	private final File rIndex = new File(path, "rIndex");

	PrivateRepo() {
		int localVersion = 0;
		String strIndex = "";
		int indexVersion = 0;

		if (!path.exists()) {
			path.mkdir();
		}

		try {
			if (rVersion.exists())
				localVersion = Integer.parseInt(Files.read(rVersion).trim());
			indexVersion = Integer.parseInt(DropBoxClient.getDropBoxClient()
					.getFile(indexVersionUrl).trim());
			if (indexVersion > localVersion | !rIndex.exists()) {
				strIndex = DropBoxClient.getDropBoxClient().getFile(indexUrl);
				Files.write(rIndex, strIndex);
				Files.write(rVersion, Integer.toString(indexVersion).trim());
			} else {
				strIndex = Files.read(rIndex);
			}
		} catch (Exception e) {
			Logger.instance.log(Logger.stack2String(e));
		}
		index = PrivateRepoIndex.getIndex(strIndex);
	}

	public static PrivateRepo getPrivateRepo() {
		if (privateRepo == null)
			privateRepo = new PrivateRepo();
		return privateRepo;
	}

	public List<Subtitle> searchSubtitles(EpisodeFile episodeFile,
			String languageCode) throws UnsupportedEncodingException {
		List<Subtitle> results = new ArrayList<Subtitle>();
		for (IndexSubtitle indexSubtitle : index) {
			if (indexSubtitle.getVideoType() == episodeFile.getVideoType()) {
				if (indexSubtitle.getTvdbid() == episodeFile.getTvdbid()) {
					if (indexSubtitle.getSeason() == episodeFile.getSeason()
							&& indexSubtitle.getEpisode() == episodeFile
									.getEpisodeNumbers().get(0)) {
						if (indexSubtitle.getLanguage().equalsIgnoreCase(
								languageCode)) {
							String location = "/"
									+ indexSubtitle.getName()
									+ "/"
									+ indexSubtitle.getSeason()
									+ "/"
									+ indexSubtitle.getEpisode()
									+ "/"
									+ indexSubtitle.getLanguage()
									+ "/"
									+ PrivateRepoIndex
											.getFullFilename(indexSubtitle);

							Subtitle tempSub = new Subtitle(
									Subtitle.SubtitleSource.PRIVATEREPO,
									indexSubtitle.getFilename(), location,
									indexSubtitle.getLanguage(), "",
									SubtitleMatchType.EVERYTHING,
									VideoFileParser.extractTeam(indexSubtitle
											.getFilename()), "", false);
							results.add(tempSub);
						}
					}
				}
			}
		}
		return results;
	}

	public List<Subtitle> searchSubtitles(MovieFile movieFile,
			String languageCode) {
		List<Subtitle> results = new ArrayList<Subtitle>();
		for (IndexSubtitle indexSubtitle : index) {
			if (indexSubtitle.getVideoType() == movieFile.getVideoType()) {
				if (indexSubtitle.getImdbid() == movieFile.getImdbid()) {
					if (indexSubtitle.getYear() == movieFile.getYear()) {
						if (indexSubtitle.getLanguage().equalsIgnoreCase(
								languageCode)) {
							String location = "/movies/"
									+ indexSubtitle.getName()
									+ " "
									+ indexSubtitle.getYear()
									+ "/"
									+ indexSubtitle.getLanguage()
									+ "/"
									+ PrivateRepoIndex
											.getFullFilename(indexSubtitle);

							Subtitle tempSub = new Subtitle(
									Subtitle.SubtitleSource.PRIVATEREPO,
									indexSubtitle.getFilename(), location,
									indexSubtitle.getLanguage(), "",
									SubtitleMatchType.EVERYTHING,
									VideoFileParser.extractTeam(indexSubtitle
											.getFilename()), "", false);
							results.add(tempSub);
						}
					}
				}
			}
		}
		return results;
	}
}
