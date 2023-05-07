package org.lodder.subtools.sublibrary.util.prompter;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.codehaus.plexus.components.interactivity.Prompter;

import lombok.Setter;
import lombok.experimental.Accessors;

public class PrompterBuilderValue {

    private PrompterBuilderValue() {
        // util class
    }

    public static ValueBuilderToObjectMapperIntf getValue() {
        return new ValueBuilder<>();
    }

    public static <T> ValueBuilderValidatorMapperIntf<T> toObjectMapper(Function<String, T> toObjectMapper) {
        return new ValueBuilder<>().toObjectMapper(toObjectMapper);
    }

    public interface ValueBuilderToObjectMapperIntf {
        <T> ValueBuilderValidatorMapperIntf<T> toObjectMapper(Function<String, T> toObjectMapper);
    }

    public interface ValueBuilderValidatorMapperIntf<T> extends ValueBuilderOtherMapperIntf<T> {
        ValueBuilderOtherMapperIntf<T> validator(Predicate<String> validator);
    }

    public interface ValueBuilderOtherMapperIntf<T> extends ValueBuilderOther2MapperIntf<T> {
        ValueBuilderOtherMapperIntf<T> objectValidator(Predicate<T> validator);

        ValueBuilderOtherMapperIntf<T> defaultValue(T defaultValue);

        ValueBuilderOtherMapperIntf<T> defaultValueSupplier(Supplier<T> defaultValueSupplier);

        @Override
        ValueBuilderOtherMapperIntf<T> message(String message, Object... replacements);

        @Override
        Optional<T> prompt(Prompter prompter);
    }

    public interface ValueBuilderOther2MapperIntf<T> {

        ValueBuilderOtherMapperIntf<T> message(String message, Object... replacements);

        ValueBuilderOtherMapperIntf<T> errorMessage(String errorMessage, Object... replacements);

        Optional<T> prompt(Prompter prompter);
    }

    // ------- \\
    // Builder \\
    // ------- \\

    @Setter
    @Accessors(fluent = true, chain = true)
    public static class ValueBuilder<T> implements ValueBuilderToObjectMapperIntf, ValueBuilderValidatorMapperIntf<T>,
            ValueBuilderOtherMapperIntf<T>, ValueBuilderOther2MapperIntf<T> {
        private Function<String, T> toObjectMapper;
        private Predicate<String> validator;
        private Predicate<T> objectValidator;
        private T defaultValue;
        private Supplier<T> defaultValueSupplier;
        private String message;
        private String errorMessage;

        private ValueBuilder() {
            // hide constructor
        }

        @SuppressWarnings("unchecked")
        @Override
        public <S> ValueBuilderValidatorMapperIntf<S> toObjectMapper(Function<String, S> toObjectMapper) {
            this.toObjectMapper = (Function<String, T>) toObjectMapper;
            return (ValueBuilderValidatorMapperIntf<S>) this;
        }

        @Override
        public ValueBuilder<T> message(String message, Object... replacements) {
            this.message = String.format(message, replacements);
            return this;
        }

        @Override
        public ValueBuilder<T> errorMessage(String errorMessage, Object... replacements) {
            this.errorMessage = String.format(errorMessage, replacements);
            return this;
        }

        @Override
        public Optional<T> prompt(Prompter prompter) {
            return PrompterBuilderCommon.prompt(prompter, toObjectMapper, validator, objectValidator, defaultValue,
                    defaultValueSupplier, message, errorMessage);
        }
    }
}
