package org.lodder.subtools.sublibrary.userinteraction;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.codehaus.plexus.components.interactivity.DefaultPrompter;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.util.prompter.PrompterUtil;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserInteractionHandlerCLI implements UserInteractionHandler {
    private final Prompter prompter = new DefaultPrompter();
    private final UserInteractionSettingsIntf settings;

    @Override
    public Optional<String> selectFromList(Collection<String> options, String message, String title) {
        return PrompterUtil.getStringFromList(options).message(message).includeNull().prompt(prompter);
    }

    @Override
    public <T> Optional<T> selectFromList(Collection<T> options, String message, String title, Function<T, String> toStringMapper) {
        return PrompterUtil.getElementFromList(options).toStringMapper(toStringMapper).message(message).includeNull().prompt(prompter);
    }

    @Override
    public boolean confirm(String message, String title) {
        return PrompterUtil.getBooleanValue().message(message + " (Y/N)").prompt(prompter).get();
    }

    @Override
    public Optional<String> enter(String title, String message, String errorMessage, Predicate<String> validator) {
        return PrompterUtil.getString().message(message).errorMessage(errorMessage).objectValidator(validator).prompt(prompter);
    }
}
