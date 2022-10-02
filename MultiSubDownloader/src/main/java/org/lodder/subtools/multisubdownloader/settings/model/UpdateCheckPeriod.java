package org.lodder.subtools.multisubdownloader.settings.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UpdateCheckPeriod {
    MANUAL("InputPanel.UpdateInterval.Manual"),
    DAILY("InputPanel.UpdateInterval.Daily"),
    WEEKLY("InputPanel.UpdateInterval.Weekly"),
    MONTHLY("InputPanel.UpdateInterval.Monthly");

    private final String langCode;
}
