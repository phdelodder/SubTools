package org.lodder.subtools.multisubdownloader.util.prompter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import lombok.Setter;
import lombok.experimental.Accessors;

public class PrompterBuilderValueFromList {

    private PrompterBuilderValueFromList() {
        // util class
    }

    // --------- \\
    // Interface \\
    // --------- \\

    public static ValueFromListPromptBuilderIntf<String> getStringFromList(Collection<String> elements) {
        return getElementFromList(elements).toStringMapper(Function.identity());
    }

    public static <T> ValueFromListToStringMapperBuilderIntf<T> getElementFromList(Collection<T> elements) {
        return new ValueFromListBuilder<>(new ArrayList<>(elements));
    }

    public static <T> ValueFromListToStringMapperBuilderIntf<T> getElementFromList(T[] elements) {
        return new ValueFromListBuilder<>(Arrays.asList(elements));
    }

    public interface ValueFromListToStringMapperBuilderIntf<T> {
        ValueFromListPromptBuilderIntf<T> toStringMapper(Function<T, String> toStringMapper);
    }

    public interface ValueFromListPromptBuilderIntf<T> {
        ValueFromListPromptBuilderIntf<T> sort(Comparator<T> comparator);

        ValueFromListPromptBuilderIntf<T> message(String message, Object... replacements);

        ValueFromListPromptBuilderIntf<T> includeNull();

        ValueFromListPromptBuilderIntf<T> includeNull(T emptyValue);

        T prompt(Prompter prompter);
    }

    // ------- \\
    // Builder \\
    // ------- \\

    @Setter
    @Accessors(fluent = true, chain = true)
    public static class ValueFromListBuilder<T>
            implements ValueFromListToStringMapperBuilderIntf<T>, ValueFromListPromptBuilderIntf<T> {
        private final List<T> elements;
        private Function<T, String> toStringMapper;
        private String message;
        private boolean includeNull;
        private T emptyValue;

        ValueFromListBuilder(List<T> elements) {
            this.elements = elements;
        }

        @Override
        public ValueFromListBuilder<T> sort(Comparator<T> comparator) {
            elements.sort(comparator);
            return this;
        }

        @Override
        public ValueFromListBuilder<T> message(String message, Object... replacements) {
            this.message = String.format(message, replacements);
            return this;
        }

        @Override
        public ValueFromListBuilder<T> includeNull() {
            this.includeNull = true;
            return this;
        }

        @Override
        public ValueFromListBuilder<T> includeNull(T emptyValue) {
            this.includeNull = true;
            this.emptyValue = emptyValue;
            return this;
        }

        @Override
        public T prompt(Prompter prompter) {
            try {
                String choicesMessage = IntStream.range(0, elements.size())
                        .mapToObj(number -> "  - " + (number + 1) + ": " + toStringMapper.apply(elements.get(number)))
                        .collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator();
                String value = prompter.prompt(StringUtils.isBlank(message) ? choicesMessage
                        : message + System.lineSeparator() + choicesMessage);
                if (StringUtils.isBlank(value) && includeNull) {
                    return emptyValue;
                }
                int number = Integer.parseInt(value);
                if (number < 1 || number > elements.size()) {
                    PrompterUtil.showMessage(prompter, "The entered value isn't in the range [1, %s], try again.",
                            elements.size());
                    return prompt(prompter);
                }
                return elements.get(number - 1);
            } catch (PrompterException e) {
                throw new IllegalStateException(e);
            } catch (NumberFormatException e) {
                return prompt(PrompterUtil.showMessage(prompter, "Enter a valid number, try again."));
            }
        }

    }

}
