package org.lodder.subtools.sublibrary.userinteraction;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

import extensions.org.codehaus.plexus.components.interactivity.Prompter.PrompterExt;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.codehaus.plexus.components.interactivity.DefaultInputHandler;
import org.codehaus.plexus.components.interactivity.DefaultOutputHandler;
import org.codehaus.plexus.components.interactivity.DefaultPrompter;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInteractionHandlerCLI implements UserInteractionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInteractionHandlerCLI.class);
    @val Prompter prompter = new DefaultPrompter(new DefaultOutputHandler(), new DefaultInputHandler());
    @val @override UserInteractionSettingsIntf settings;

    public UserInteractionHandlerCLI(UserInteractionSettingsIntf settings) {
        this.settings = settings;
    }

    @Override
    public <T> Optional<T> selectFromList(Iterable<T> options, @Nullable String message, @Nullable String title,
        @Nullable Function<T, String> toStringMapper) {
        // TODO use extension method
        return PrompterExt.promptValueFromList(prompter,
            message,
            options,
            toStringMapper,
            true);
    }

    @Override
    public boolean confirm(String message, String title) {
        // TODO Use extension method
        return PrompterExt.promptBoolean(prompter,
                message + " (%s/%s)".formatted(getText("Prompter.YesAbbreviation"),
                    getText("Prompter.NoAbbreviation")))
            .orElse(false);
    }

    @Override
    public Optional<String> enter(String message, @Nullable String title,
        @Nullable List<Validator<String>> inputValidators) {
        // TODO use extension method
        return PrompterExt.promptString(prompter, message, inputValidators:inputValidators);
    }

    @Override
    public OptionalInt enterNumber(String title, String message,
        @Nullable List<Validator<Integer>> objectValidators) {
        // TODO use extension method
        return PrompterExt.promptInt(prompter, message, objectValidators:objectValidators);
    }

    @Override
    public void showMessage(String message, String title, MessageSeverity messageSeverity) {
        switch (messageSeverity) {
            case INFO -> LOGGER.info(message);
            case WARNING -> LOGGER.warn(message);
            case ERROR -> LOGGER.error(message);
            default -> LOGGER.info(message);
        }
    }
}
