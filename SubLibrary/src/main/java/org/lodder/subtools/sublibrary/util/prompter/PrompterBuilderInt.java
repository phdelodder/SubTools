package org.lodder.subtools.sublibrary.util.prompter;

import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import org.codehaus.plexus.components.interactivity.Prompter;

import lombok.Setter;
import lombok.experimental.Accessors;

public class PrompterBuilderInt {

    private PrompterBuilderInt() {
        // util class
    }

    protected static int getValue(Prompter prompter) {
        return new ValueBuilder().prompt(prompter);
    }

    protected static ValueBuilderOtherMapperIntf getValue() {
        return new ValueBuilder();
    }

    public static ValueBuilderOtherMapperIntf defaultValue(int defaultValue) {
        return new ValueBuilder().defaultValue(defaultValue);
    }

    public static ValueBuilderOtherMapperIntf defaultValueSupplier(IntSupplier defaultValueSupplier) {
        return new ValueBuilder().defaultValueSupplier(defaultValueSupplier);
    }

    public static ValueBuilderOtherMapperIntf message(String message, Object... replacements) {
        return new ValueBuilder().message(message, replacements);
    }

    public static ValueBuilderOtherMapperIntf errorMessage(String errorMessage, Object... replacements) {
        return new ValueBuilder().errorMessage(errorMessage, replacements);
    }

    public interface ValueBuilderOtherMapperIntf {

        ValueBuilderOtherMapperIntf defaultValue(int defaultValue);

        ValueBuilderOtherMapperIntf defaultValueSupplier(IntSupplier defaultValueSupplier);

        ValueBuilderOtherMapperIntf message(String message, Object... replacements);

        ValueBuilderOtherMapperIntf errorMessage(String errorMessage, Object... replacements);

        ValueBuilderOtherMapperIntf validator(IntPredicate validator);

        int prompt(Prompter prompter);
    }

    // ------- \\
    // Builder \\
    // ------- \\

    @Setter
    @Accessors(fluent = true, chain = true)
    public static class ValueBuilder implements ValueBuilderOtherMapperIntf {
        public static final ToIntFunction<String> TO_OBJECT_MAPPER = Integer::parseInt;
        public static final Predicate<String> VALIDATOR = v -> {
            try {
                Integer.parseInt(v);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        };
        private Integer defaultValue;
        private IntSupplier defaultValueSupplier;
        private String message;
        private String errorMessage;
        private IntPredicate validator;

        private ValueBuilder() {
            // hide constructor
        }

        @Override
        public ValueBuilder defaultValue(int defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public ValueBuilder message(String message, Object... replacements) {
            this.message = String.format(message, replacements);
            return this;
        }

        @Override
        public ValueBuilder errorMessage(String errorMessage, Object... replacements) {
            this.errorMessage = String.format(errorMessage, replacements);
            return this;
        }

        @Override
        public int prompt(Prompter prompter) {
            return PrompterBuilderCommon.prompt(prompter, TO_OBJECT_MAPPER::applyAsInt, VALIDATOR,
                    v -> validator == null || validator.test(v), defaultValue,
                    defaultValueSupplier == null ? null : defaultValueSupplier::getAsInt, message, errorMessage).get();
        }
    }
}
