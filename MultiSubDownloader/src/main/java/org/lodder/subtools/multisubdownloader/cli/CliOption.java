package org.lodder.subtools.multisubdownloader.cli;

import org.lodder.subtools.multisubdownloader.Messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CliOption {
    HELP("help", false, Messages.getString("App.OptionHelpMsg")),
    NO_GUI("nogui", false, Messages.getString("App.OptionNoGuiMsg")),
    RECURSIVE("R", "recursive", false, Messages.getString("App.OptionOptionRecursiveMsg")),
    LANGUAGE("language", true, Messages.getString("App.OptionOptionLanguageMsg")),
    DEBUG("debug", false, Messages.getString("App.OptionOptionDebugMsg")),
    TRACE("trace", false, Messages.getString("App.OptionOptionTraceMsg")),
    IMPORT_PREFERENCES("importpreferences", true, Messages.getString("App.OptionOptionImportPreferencesMsg")),
    FORCE("force", false, Messages.getString("App.OptionOptionForceMsg")),
    FOLDER("folder", true, Messages.getString("App.OptionOptionFolderMsg")),
    DOWNLOAD_ALL("downloadall", false, Messages.getString("App.OptionOptionDownloadAllMsg")),
    SELECTION("selection", false, Messages.getString("App.OptionOptionSelectionMsg")),
    SPEEDY("speedy", false, Messages.getString("App.OptionOptionSpeedyMsg")),
    VERBOSE_PROGRESS("verboseprogress", false, Messages.getString("App.OptionVerboseProgressCLI")),
    DRY_RUN("dryrun", false, Messages.getString("App.OptionDryRun")),
    CONFIRM_PROVIDER_MAPPING("confirmProviderMapping", false, Messages.getString("App.OptionConfirmProviderMapping"));

    private final String value;
    private final String longValue;
    private final boolean hasArg;
    private final String description;

    CliOption(String value, boolean hasArg, String description) {
        this(value, null, hasArg, description);
    }

}