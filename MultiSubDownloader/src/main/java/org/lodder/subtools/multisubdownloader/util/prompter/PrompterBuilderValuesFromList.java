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

public class PrompterBuilderValuesFromList {

    private PrompterBuilderValuesFromList() {
        // util class
    }

    // --------- \\
    // Interface \\
    // --------- \\

    public static ValuesFromListPromptBuilderIntf<String> getStringsFromList(Collection<String> elements) {
        return getElementsFromList(elements).toStringMapper(Function.identity());
    }

    public static <T> ValuesFromListToStringMapperBuilderIntf<T> getElementsFromList(Collection<T> elements) {
        return new ValuesFromListBuilder<>(new ArrayList<>(elements));
    }

    public static <T> ValuesFromListToStringMapperBuilderIntf<T> getElementsFromList(T[] elements) {
        return new ValuesFromListBuilder<>(Arrays.asList(elements));
    }

    public interface ValuesFromListToStringMapperBuilderIntf<T> {
        ValuesFromListPromptBuilderIntf<T> toStringMapper(Function<T, String> toStringMapper);
    }

    public interface ValuesFromListPromptBuilderIntf<T> {
        ValuesFromListPromptBuilderIntf<T> sort(Comparator<T> comparator);

        ValuesFromListPromptBuilderIntf<T> message(String message, Object... replacements);

        ValuesFromListPromptBuilderIntf<T> includeNull();

        List<T> prompt(Prompter prompter);
    }

    // ------- \\
    // Builder \\
    // ------- \\

    @Setter
    @Accessors(fluent = true, chain = true)
    public static class ValuesFromListBuilder<T>
            implements ValuesFromListToStringMapperBuilderIntf<T>, ValuesFromListPromptBuilderIntf<T> {
        private final List<T> elements;
        private Function<T, String> toStringMapper;
        private String message;
        private boolean includeNull;

        ValuesFromListBuilder(List<T> elements) {
            this.elements = new ArrayList<>(elements);
        }

        @Override
        public ValuesFromListBuilder<T> sort(Comparator<T> comparator) {
            elements.sort(comparator);
            return this;
        }

        @Override
        public ValuesFromListBuilder<T> message(String message, Object... replacements) {
            this.message = String.format(message, replacements);
            return this;
        }

        @Override
        public ValuesFromListBuilder<T> includeNull() {
            this.includeNull = true;
            return this;
        }

        @Override
        public List<T> prompt(Prompter prompter) {
            try {
                String choicesMessage = IntStream.range(0, elements.size())
                        .mapToObj(number -> "  - " + (number + 1) + ": " + toStringMapper.apply(elements.get(number)))
                        .collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator();
                String value = prompter.prompt(StringUtils.isBlank(message) ? choicesMessage
                        : message + System.lineSeparator() + choicesMessage);
                if (StringUtils.isBlank(value) && includeNull) {
                    return new ArrayList<>();
                }
                if (StringUtils.isBlank(value)) {
                    return prompt(PrompterUtil.showMessage(prompter, "Enter a valid value, try again."));
                }
                List<Integer> choices = Arrays.stream(value.split(",")).map(Integer::parseInt).map(i -> i - 1)
                        .collect(Collectors.toList());
                if (choices.stream().distinct().count() != choices.size()) {
                    return prompt(PrompterUtil.showMessage(prompter, "Choose all distinct options, try again."));
                }
                if (choices.stream().anyMatch(number -> number < 0 || number > elements.size() - 1)) {
                    PrompterUtil.showMessage(prompter, "The entered number(s) aren't in the range [1, %s], try again.",
                            elements.size());
                    return prompt(prompter);
                }
                return choices.stream().map(elements::get).collect(Collectors.toList());
            } catch (PrompterException e) {
                throw new IllegalStateException(e);
            } catch (NumberFormatException e) {
                PrompterUtil.showMessage(prompter,
                        "Invalid number(s) encountered. Enter a comma separated list of the choices.");
                return prompt(prompter);
            }
        }
    }

}
