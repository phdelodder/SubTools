package org.lodder.subtools.sublibrary.util.prompter;

import java.util.Collection;
import java.util.function.Function;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.lodder.subtools.sublibrary.util.prompter.PrompterBuilderValue.ValueBuilderOther2MapperIntf;
import org.lodder.subtools.sublibrary.util.prompter.PrompterBuilderValue.ValueBuilderOtherMapperIntf;
import org.lodder.subtools.sublibrary.util.prompter.PrompterBuilderValueFromList.ValueFromListPromptBuilderIntf;
import org.lodder.subtools.sublibrary.util.prompter.PrompterBuilderValueFromList.ValueFromListToStringMapperBuilderIntf;
import org.lodder.subtools.sublibrary.util.prompter.PrompterBuilderValuesFromList.ValuesFromListPromptBuilderIntf;
import org.lodder.subtools.sublibrary.util.prompter.PrompterBuilderValuesFromList.ValuesFromListToStringMapperBuilderIntf;

public class PrompterUtil {

    private PrompterUtil() {
        // util class
    }

    public static Prompter showMessage(Prompter prompter, String message, Object... replacements) {
        try {
            prompter.showMessage(String.format(message, replacements));
            return prompter;
        } catch (PrompterException e) {
            throw new IllegalStateException(e);
        }
    }

    public static PrompterBuilderBoolean.ValueBuilderOtherMapperIntf userApproves() {
        return getBoolean();
    }

    public static ValueBuilderOther2MapperIntf<String> pressAnyKeyToContinue() {
        return getString().defaultValue("");
    }

    public static void pressAnyKeyToContinue(Prompter prompter) {
        pressAnyKeyToContinue().message("Press any key to continue").prompt(prompter);
    }

    // ############### \\
    // ## Get Value ## \\
    // ############### \\

    public static int getInt(Prompter prompter) {
        return PrompterBuilderInt.getValue(prompter);
    }

    public static PrompterBuilderInt.ValueBuilderOtherMapperIntf getInt() {
        return PrompterBuilderInt.getValue();
    }

    public static PrompterBuilderValue.ValueBuilderOtherMapperIntf<Integer> getIntValue() {
        return PrompterBuilderValue.getValue().toObjectMapper(Integer::parseInt)
                .validator(PrompterBuilderInt.ValueBuilder.VALIDATOR);
    }

    public static boolean getBoolean(Prompter prompter) {
        return PrompterBuilderBoolean.getValue(prompter);
    }

    public static PrompterBuilderBoolean.ValueBuilderOtherMapperIntf getBoolean() {
        return PrompterBuilderBoolean.getValue();
    }

    public static PrompterBuilderValue.ValueBuilderOtherMapperIntf<Boolean> getBooleanValue() {
        return PrompterBuilderValue.getValue().toObjectMapper("y"::equalsIgnoreCase)
                .validator(PrompterBuilderBoolean.ValueBuilder.VALIDATOR);
    }

    public static String getString(Prompter prompter) {
        return PrompterBuilderString.getString(prompter);
    }

    public static ValueBuilderOtherMapperIntf<String> getString() {
        return PrompterBuilderString.getString();
    }

    // ######################### \\
    // ## Get Value From List ## \\
    // ######################### \\

    public static ValueFromListPromptBuilderIntf<String> getStringFromList(Collection<String> elements) {
        return PrompterBuilderValueFromList.getElementFromList(elements).toStringMapper(Function.identity());
    }

    public static <T> ValueFromListToStringMapperBuilderIntf<T> getElementFromList(Collection<T> elements) {
        return PrompterBuilderValueFromList.getElementFromList(elements);
    }

    public static <T> ValueFromListToStringMapperBuilderIntf<T> getElementFromList(T[] elements) {
        return PrompterBuilderValueFromList.getElementFromList(elements);
    }

    // ########################## \\
    // ## Get Values From List ## \\
    // ########################## \\

    public static ValuesFromListPromptBuilderIntf<String> getStringsFromList(Collection<String> elements) {
        return PrompterBuilderValuesFromList.getStringsFromList(elements);
    }

    public static <T> ValuesFromListToStringMapperBuilderIntf<T> getElementsFromList(Collection<T> elements) {
        return PrompterBuilderValuesFromList.getElementsFromList(elements);
    }

    public static <T> ValuesFromListToStringMapperBuilderIntf<T> getElementsFromList(T[] elements) {
        return PrompterBuilderValuesFromList.getElementsFromList(elements);
    }
}
