package org.lodder.subtools.sublibrary.util.prompter;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import com.optimaize.langdetect.cybozu.util.Messages;

public class PrompterBuilderCommon {

    private PrompterBuilderCommon() {
        // hide constructor
    }

    protected static <T> Optional<T> prompt(Prompter prompter, Function<String, T> toObjectMapper, Predicate<String> validator,
            Predicate<T> objValidator, T defaultValue, Supplier<T> defaultValueSupplier, String message) {
        try {
            String value = prompter.prompt(message + System.lineSeparator());
            if (StringUtils.isEmpty(value)) {
                if (defaultValue != null) {
                    return Optional.ofNullable(defaultValue);
                } else if (defaultValueSupplier != null) {
                    return Optional.ofNullable(defaultValueSupplier.get());
                } else {
                    return prompt(prompter, toObjectMapper, validator, objValidator, defaultValue, defaultValueSupplier,
                            message);
                }
            } else {
                if (validator != null && !validator.test(value)) {
                    prompter.showMessage(Messages.getString("Prompter.ValueIsNotValid"));
                    return prompt(prompter, toObjectMapper, validator, objValidator, defaultValue, defaultValueSupplier,
                            message);
                }
                T object = toObjectMapper.apply(value);
                if (objValidator != null && !objValidator.test(object)) {
                    prompter.showMessage(Messages.getString("Prompter.ValueIsNotValid"));
                    return prompt(prompter, toObjectMapper, validator, objValidator, defaultValue, defaultValueSupplier,
                            message);
                }
                return Optional.ofNullable(object);
            }
        } catch (PrompterException e) {
            throw new IllegalStateException(e);
        }
    }

}
