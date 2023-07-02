package org.lodder.subtools.multisubdownloader;

import static org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName.*;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.codehaus.plexus.components.interactivity.DefaultInputHandler;
import org.codehaus.plexus.components.interactivity.DefaultOutputHandler;
import org.codehaus.plexus.components.interactivity.DefaultPrompter;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.joor.Reflect;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.util.prompter.ColumnDisplayer;
import org.lodder.subtools.sublibrary.util.prompter.PrompterUtil;
import org.lodder.subtools.sublibrary.util.prompter.TableDisplayer;

public class UserInteractionHandlerCLI extends org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandlerCLI
        implements UserInteractionHandler {
    private final Prompter prompter;

    public UserInteractionHandlerCLI(UserInteractionSettingsIntf settings) {
        super(settings);
        DefaultOutputHandler defaultOutputHandler = new DefaultOutputHandler();
        DefaultInputHandler defaultInputHandler = new DefaultInputHandler();
        try {
            defaultOutputHandler.initialize();
            defaultInputHandler.initialize();
        } catch (InitializationException e) {
            throw new RuntimeException(e);
        }
        prompter = Reflect.on(new DefaultPrompter())
                .set("outputHandler", defaultOutputHandler)
                .set("inputHandler", defaultInputHandler)
                .get();
    }

    @Override
    public List<Subtitle> selectSubtitles(Release release) {
        System.out.printf("\n%s : %s%n", Messages.getString("SelectDialog.SelectCorrectSubtitleThisRelease"), release.getFileName());
        return PrompterUtil
                .getElementsFromList(release.getMatchingSubs())
                .displayAsTable(createTableDisplayer())
                .message(Messages.getString("SelectDialog.EnterListSelectedSubtitles"))
                .sort(Comparator.comparing(Subtitle::getScore))
                .includeNull()
                .prompt(prompter);
    }

    private ColumnDisplayer<Subtitle> createSubtitleDisplayer(SubtitleTableColumnName column, Function<Subtitle, Object> toStringMapper) {
        return new ColumnDisplayer<>(column.getColumnName(), (Subtitle s) -> String.valueOf(toStringMapper.apply(s)));
    }

    private TableDisplayer<Subtitle> createTableDisplayer() {
        return new TableDisplayer<>(Stream.of(SCORE, FILENAME, RELEASEGROUP, QUALITY, SOURCE, UPLOADER, HEARINGIMPAIRED)
                .map(stcn -> createSubtitleDisplayer(stcn, stcn.getValueFunction())).toList());
    }

    @Override
    public void dryRunOutput(Release release) {
        createTableDisplayer().display(release.getMatchingSubs());
    }
}
