package org.lodder.subtools.multisubdownloader.settings.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.val;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UpdateCheckPeriod {
    MANUAL("InputPanel.UpdateInterval.Manual"),
    DAILY("InputPanel.UpdateInterval.Daily"),
    WEEKLY("InputPanel.UpdateInterval.Weekly"),
    MONTHLY("InputPanel.UpdateInterval.Monthly");

    @val String langCode;
}
