package org.lodder.subtools.sublibrary.data;

import java.util.List;

public interface UserInteractionSettingsIntf {

    boolean isOptionsAlwaysConfirm();

    boolean isOptionsMinAutomaticSelection();

    int getOptionsMinAutomaticSelectionValue();

    boolean isOptionsDefaultSelection();

    List<String> getOptionsDefaultSelectionQualityList();

    boolean isOptionsConfirmProviderMapping();
}
