package org.lodder.subtools.multisubdownloader.gui;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.sublibrary.Language;

public record LanguageWrapper(Language language) {
    @Override
    public String toString() {
        return Messages.getString(language.getMsgCode());
    }
}

