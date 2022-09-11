package org.lodder.subtools.multisubdownloader.gui;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.sublibrary.Language;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class LanguageWrapper {
    private final Language language;

    @Override
    public String toString() {
        return Messages.getString(language.getMsgCode());
    }
}

