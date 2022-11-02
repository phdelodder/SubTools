package org.lodder.subtools.multisubdownloader.cli;

import org.lodder.subtools.multisubdownloader.Messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CliOption {
    HELP("help", false, "App.OptionHelpMsg"),
    NO_GUI("nogui", false, "App.OptionNoGuiMsg"),
    RECURSIVE("R", "recursive", false, "App.OptionOptionRecursiveMsg"),
    LANGUAGE("language", true, "App.OptionOptionLanguageMsg"),
    DEBUG("debug", false, "App.OptionOptionDebugMsg"),
    TRACE("trace", false, "App.OptionOptionTraceMsg"),
    IMPORT_PREFERENCES("importpreferences", true, "App.OptionOptionImportPreferencesMsg"),
    FORCE("force", false, "App.OptionOptionForceMsg"),
    FOLDER("folder", true, "App.OptionOptionFolderMsg"),
    DOWNLOAD_ALL("downloadall", false, "App.OptionOptionDownloadAllMsg"),
    SELECTION("selection", false, "App.OptionOptionSelectionMsg"),
    SPEEDY("speedy", false, "App.OptionOptionSpeedyMsg"),
    VERBOSE_PROGRESS("verboseprogress", false, "App.OptionVerboseProgressCLI"),
    DRY_RUN("dryrun", false, "App.OptionDryRun"),
    CONFIRM_PROVIDER_MAPPING("confirmProviderMapping", false, "App.OptionConfirmProviderMapping");

    private final String value;
    private final String longValue;
    private final boolean hasArg;
    private final String msgCode;

    CliOption(String value, boolean hasArg, String description) {
        this(value, null, hasArg, description);
    }

    public String getDescription() {
        return Messages.getString(msgCode);
    }

}