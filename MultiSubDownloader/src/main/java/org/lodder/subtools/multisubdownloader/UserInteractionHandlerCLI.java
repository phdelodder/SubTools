package org.lodder.subtools.multisubdownloader;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.codehaus.plexus.components.interactivity.DefaultPrompter;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.util.prompter.ColumnDisplayer;
import org.lodder.subtools.sublibrary.util.prompter.PrompterUtil;
import org.lodder.subtools.sublibrary.util.prompter.TableDisplayer;

public class UserInteractionHandlerCLI extends org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandlerCLI implements UserInteractionHandler {
    private final Prompter prompter = new DefaultPrompter();

    public UserInteractionHandlerCLI(UserInteractionSettingsIntf settings) {
        super(settings);
    }

    @Override
    public List<Subtitle> selectSubtitles(Release release) {
        System.out.println("\nSelect subtitle(s) for : " + release.getFileName());
        return PrompterUtil
                .getElementsFromList(release.getMatchingSubs())
                .displayAsTable(createTableDisplayer())
                .message("Enter comma separated list of numbers of selected subtitle: ")
                .sort(Comparator.comparing(Subtitle::getScore))
                .includeNull()
                .prompt(prompter);
    }

    private ColumnDisplayer<Subtitle> createSubtitleDisplayer(SubtitleTableColumnName column, Function<Subtitle, Object> toStringMapper) {
        return new ColumnDisplayer<>(column.getColumnName(), (Subtitle s) -> String.valueOf(toStringMapper.apply(s)));
    }

    private TableDisplayer<Subtitle> createTableDisplayer() {
        return new TableDisplayer<>(List.of(
                createSubtitleDisplayer(SubtitleTableColumnName.SCORE, Subtitle::getScore),
                createSubtitleDisplayer(SubtitleTableColumnName.FILENAME, Subtitle::getFileName),
                createSubtitleDisplayer(SubtitleTableColumnName.RELEASEGROUP, Subtitle::getReleaseGroup),
                createSubtitleDisplayer(SubtitleTableColumnName.QUALITY, Subtitle::getQuality),
                createSubtitleDisplayer(SubtitleTableColumnName.SOURCE, Subtitle::getSubtitleSource),
                createSubtitleDisplayer(SubtitleTableColumnName.UPLOADER, Subtitle::getUploader),
                createSubtitleDisplayer(SubtitleTableColumnName.HEARINGIMPAIRED, Subtitle::isHearingImpaired)));
    }

    @Override
    public void dryRunOutput(Release release) {
        createTableDisplayer().display(release.getMatchingSubs());
    }
}
