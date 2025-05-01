package org.lodder.subtools.multisubdownloader;

import static org.lodder.subtools.multisubdownloader.Messages.*;
import static org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName.*;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import extensions.org.codehaus.plexus.components.interactivity.Prompter.PrompterExt;
import org.codehaus.plexus.components.interactivity.DefaultInputHandler;
import org.codehaus.plexus.components.interactivity.DefaultOutputHandler;
import org.codehaus.plexus.components.interactivity.DefaultPrompter;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class UserInteractionHandlerCLI extends org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandlerCLI
    implements UserInteractionHandler {
    private final Prompter prompter;

    public UserInteractionHandlerCLI(UserInteractionSettingsIntf settings) {
        super(settings);
        prompter = new DefaultPrompter(new DefaultOutputHandler(), new DefaultInputHandler());
    }

    @Override
    public List<Subtitle> selectSubtitles(Release release) {
        System.out.printf("\n%s : %s%n", getText("SelectDialog.SelectCorrectSubtitleThisRelease"),
            release.fileName);
        return prompter.promptValuesFromList(
            getText("SelectDialog.EnterListSelectedSubtitles"),
            release.getMatchingSubs(),
            Subtitle::getFileName,
            true,
            createTableDisplayer(),
            Comparator.comparing(Subtitle::getScore));
    }

    private PrompterExt.ColumnDisplayer<Subtitle> createSubtitleDisplayer(SubtitleTableColumnName column,
        Function<Subtitle, Object> toStringMapper) {
        return new PrompterExt.ColumnDisplayer<>(column.columnName,
            subtitle -> String.valueOf(toStringMapper.apply(subtitle)));
    }

    @Override
    public void dryRunOutput(Release release) {
        createTableDisplayer().display(release.getMatchingSubs());
    }

    private PrompterExt.TableDisplayer<Subtitle> createTableDisplayer() {
        return new PrompterExt.TableDisplayer<>(
            Stream.of(SCORE, FILENAME, RELEASEGROUP, QUALITY, SOURCE, UPLOADER, HEARINGIMPAIRED)
                .map(stcn -> createSubtitleDisplayer(stcn, stcn.valueFunction)).toList());
    }
}
