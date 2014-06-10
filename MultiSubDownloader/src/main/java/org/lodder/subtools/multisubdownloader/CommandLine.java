package org.lodder.subtools.multisubdownloader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.lodder.subtools.multisubdownloader.lib.Actions;
import org.lodder.subtools.multisubdownloader.lib.control.VideoFileFactory;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Listener;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.VideoFile;

public class CommandLine implements Listener {

	private final SettingsControl prefctrl;
	private boolean recursive;
	private boolean parse;
	private String languagecode;
	private boolean gui;
	private boolean help;
	private boolean importPreferences;
	private File file;
	private boolean importexclude;
	private boolean importmapping;
	private boolean force;
	private File folder;
	private boolean downloadall;
	private final Actions actions;
	private boolean updateFromOnlineMapping;
	private boolean subtitleSelectionDialog;

	public CommandLine(final SettingsControl prefctrl) {
		Logger.instance.addListener(this);
		this.prefctrl = prefctrl;
		actions = new Actions(prefctrl.getSettings(), true);
		parse = false;
		gui = true;
		recursive = false;
		help = false;
		importPreferences = false;
		languagecode = "";
		file = null;
		importexclude = false;
		importmapping = false;
		force = false;
		folder = null;
		updateFromOnlineMapping = false;
		subtitleSelectionDialog = false;
	}

	public boolean isGui() {
		return gui;
	}

	public void Parse(String[] args) throws Exception {
		boolean speedy = false;
		String previousarg = "";
		for (String arg : args) {
			if (arg.equals("--help")) {
				gui = false;
				help = true;
			} else if (arg.equals("--nogui")) {
				gui = false;
			} else if (arg.equals("--recursive") || arg.equals("-R")) {
				recursive = true;
			} else if (arg.equalsIgnoreCase("--debug")) {
				Logger.instance.setLogLevel(Level.DEBUG);
			} else if (arg.equalsIgnoreCase("--trace")) {
				Logger.instance.setLogLevel(Level.ALL);
			} else if (arg.equalsIgnoreCase("--language")
					|| arg.equalsIgnoreCase("--folder")) {
				// do nothing
			} else if (arg.equalsIgnoreCase("--importpreferences")) {
				importPreferences = true;
			} else if (arg.equalsIgnoreCase("--importexclude")) {
				importexclude = true;
			} else if (arg.equalsIgnoreCase("--importmapping")) {
				importmapping = true;
			} else if (arg.equalsIgnoreCase("--updatefromonlinemapping")) {
				updateFromOnlineMapping = true;
			} else if (arg.equalsIgnoreCase("--downloadall")) {
				downloadall = true;
			} else if (arg.equalsIgnoreCase("--force")) {
				force = true;
			} else if (arg.equalsIgnoreCase("--selection")) {
				subtitleSelectionDialog = true;
			} else if (previousarg.equals("--language")) {
				if (arg.equals("nl") || arg.equals("en")) {
					languagecode = arg;
				} else {
					throw new Exception(
							"Language code not valid must be 'en' or 'nl' ");
				}
			} else if (previousarg.equals("--importpreferences")) {
				file = new File(arg);
			} else if (previousarg.equals("--folder")) {
				folder = new File(arg);
			} else if (arg.equalsIgnoreCase("--speedy")) {
				speedy = true;
			} else {
				Logger.instance.log("Unknown argument: " + arg);
			}
			previousarg = arg;
		}
		if (languagecode.equals("") && !gui) {
			Logger.instance.log("No language given using default: 'nl' ");
			languagecode = "nl";
		}
		Preferences preferences = Preferences.userRoot();
		preferences.putBoolean("speedy", speedy);
		parse = true;
	}

	public List<VideoFile> search() throws Exception {
		List<VideoFile> l = new ArrayList<VideoFile>();
		if (parse) {
			List<File> folders = new ArrayList<File>();
			if (folder == null) {
				folders.addAll(prefctrl.getSettings()
						.getDefaultIncomingFolders());
			} else {
				folders.add(folder);
			}
			
			for (File f : folders) {
				List<File> files = actions.getFileListing(f, recursive,
						languagecode, force);
				Logger.instance
						.debug("Files found to process: " + files.size());
				VideoFile videoFile;
				for (File file : files) {
					try {
						videoFile = VideoFileFactory.get(file, f,
								prefctrl.getSettings(), languagecode);
						if (videoFile != null)
							l.add(videoFile);
					} catch (Exception e) {
						Logger.instance.error("Search Process"
								+ Logger.stack2String(e));
					}
				}
			}
		} else {
			throw new Exception("parse wasn't called");
		}
		Logger.instance.debug("found files for doDownload: " + l.size());
		return l;
	}

	public void download(VideoFile videoFile) throws Exception {
		int selection = actions.determineWhatSubtitleDownload(videoFile,
				subtitleSelectionDialog);
		if (selection >= 0) {
			if (downloadall) {
				for (int j = 0; j < videoFile.getMatchingSubs().size(); j++) {
					actions.download(videoFile, videoFile.getMatchingSubs()
							.get(j), j + 1);
				}
				Logger.instance.log("Downloaded ALL subs for episode: "
						+ videoFile.getFilename());
			} else {
				actions.download(videoFile,
						videoFile.getMatchingSubs().get(selection));
				Logger.instance.log("Downloaded sub for episode: "
						+ videoFile.getFilename()
						+ " using these subs: "
						+ videoFile.getMatchingSubs().get(selection)
								.getFilename());
			}
		} else {
			Logger.instance
					.log("No subs found for: " + videoFile.getFilename());
		}
	}

	public void executeArgs() {
		if (help) {
			showHelp();
		} else if (importexclude) {
		} else if (importmapping) {
		} else if (updateFromOnlineMapping) {
			try {
				this.prefctrl.updateMappingFromOnline();
				this.prefctrl.store();
			} catch (Throwable e) {
				Logger.instance.error("executeArgs: updateFromOnlineMapping"
						+ Logger.stack2String(e));
			}
		} else if (importPreferences) {
			try {
				if (file != null && file.isFile()) {
					prefctrl.importPreferences(file);
				}
			} catch (Exception e) {
				Logger.instance.error("executeArgs: importPreferences"
						+ Logger.stack2String(e));
			}
		} else {
			List<VideoFile> l;
			try {
				l = search();
				for (VideoFile ef : l) {
					download(ef);
				}
			} catch (Exception e) {
				Logger.instance.error("executeArgs: search"
						+ Logger.stack2String(e));
			}
		}
	}

	public void showHelp() {
		System.out.println("--help\t show this");
		System.out.println("--nogui\t activated commandline usage");
		System.out
				.println("-R, --recursive\t search every folder found in the folder");
		System.out
				.println("--language LANG\t gives the language to search for example: --language nl only nl or en");
		System.out.println("--debug\t enables logging");
		System.out.println("--importpreferences FILE\t import preferences");
		System.out.println("--force\t force to overwrite the subtitle on disk");
		System.out.println("--folder FOLDER\t folder to search");
		System.out
				.println("--downloadall\t Download all found subs using '-v1' system ");
		System.out
				.println("--updatefromonlinemapping\t Update with the Mappings from online");
		System.out
				.println("--selection\t Subtitle selection possible if multiple subtitles detected");
		// System.out.println("--importexclude FILE\t import excludes");
		// System.out.println("--importmapping FILE\t import mapping");
	}

	@Override
	public void log(String log) {
		System.out.println(log + "\r");
	}

	public void CheckUpdate() {
		UpdateAvailableDropbox u = new UpdateAvailableDropbox();
		if (u.checkProgram()) {
			Logger.instance.log("Update available! : " + u.getUpdateUrl());
		}
	}

}
