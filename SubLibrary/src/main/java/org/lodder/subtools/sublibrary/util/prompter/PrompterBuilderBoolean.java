package org.lodder.subtools.sublibrary.util.prompter;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import org.codehaus.plexus.components.interactivity.Prompter;

import lombok.Setter;
import lombok.experimental.Accessors;

public class PrompterBuilderBoolean {

    private PrompterBuilderBoolean() {
        // util class
    }

    protected static boolean getValue(Prompter prompter) {
        return new ValueBuilder().prompt(prompter);
    }

    protected static ValueBuilderOtherMapperIntf getValue() {
        return new ValueBuilder();
    }

    public static ValueBuilderOtherMapperIntf defaultValue(boolean defaultValue) {
        return new ValueBuilder().defaultValue(defaultValue);
    }

    public static ValueBuilderOtherMapperIntf defaultValueSupplier(BooleanSupplier defaultValueSupplier) {
        return new ValueBuilder().defaultValueSupplier(defaultValueSupplier);
    }

    public static ValueBuilderOtherMapperIntf message(String message, Object... replacements) {
        return new ValueBuilder().message(message, replacements);
    }

    public interface ValueBuilderOtherMapperIntf {

        ValueBuilderOtherMapperIntf defaultValue(boolean defaultValue);

        ValueBuilderOtherMapperIntf defaultValueSupplier(BooleanSupplier defaultValueSupplier);

        ValueBuilderOtherMapperIntf message(String message, Object... replacements);

        boolean prompt(Prompter prompter);
    }

    // ------- \\
    // Builder \\
    // ------- \\

    @Setter
    @Accessors(fluent = true, chain = true)
    public static class ValueBuilder implements ValueBuilderOtherMapperIntf {
        public static final Predicate<String> TO_OBJECT_MAPPER = "y"::equalsIgnoreCase;
        public static final Predicate<String> VALIDATOR = v -> "y".equalsIgnoreCase(v) || "n".equalsIgnoreCase(v);
        private Boolean defaultValue;
        private BooleanSupplier defaultValueSupplier;
        private String message;

        private ValueBuilder() {
            // hide constructor
        }

        @Override
        public ValueBuilder defaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public ValueBuilder message(String message, Object... replacements) {
            this.message = String.format(message, replacements);
            return this;
        }

        @Override
        public boolean prompt(Prompter prompter) {
            return PrompterBuilderCommon.prompt(prompter, TO_OBJECT_MAPPER::test, VALIDATOR, null, defaultValue,
                    defaultValueSupplier == null ? null : defaultValueSupplier::getAsBoolean, message).get();
        }
    }
}
