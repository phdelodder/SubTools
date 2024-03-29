package org.lodder.subtools.sublibrary.util.prompter;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.lodder.subtools.sublibrary.util.prompter.PrompterBuilderValue.ValueBuilderOtherMapperIntf;

public class PrompterBuilderString {

    private PrompterBuilderString() {
        // util class
    }

    protected static String getString(Prompter prompter) {
        return PrompterBuilderValue.getValue().toObjectMapper(Function.identity()).validator(StringUtils::isNotBlank)
                .prompt(prompter).get();
    }

    protected static ValueBuilderOtherMapperIntf<String> getString() {
        return PrompterBuilderValue.getValue().toObjectMapper(Function.identity()).validator(StringUtils::isNotBlank);
    }

}
