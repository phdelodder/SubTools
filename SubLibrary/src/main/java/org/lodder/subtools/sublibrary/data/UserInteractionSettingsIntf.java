package org.lodder.subtools.sublibrary.data;

import java.util.List;

import org.lodder.subtools.sublibrary.control.VideoPatterns.Source;

public interface UserInteractionSettingsIntf {

    boolean isOptionsAlwaysConfirm();

    boolean isOptionsMinAutomaticSelection();

    int getOptionsMinAutomaticSelectionValue();

    boolean isOptionsDefaultSelection();

    List<Source> getOptionsDefaultSelectionQualityList();

    boolean isOptionsConfirmProviderMapping();
}
