package org.lodder.subtools.sublibrary.data;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserInteractionSettings implements UserInteractionSettingsIntf {

    private final boolean optionsAlwaysConfirm;

    private final boolean optionsMinAutomaticSelection;

    private final int optionsMinAutomaticSelectionValue;

    private final boolean optionsDefaultSelection;

    private final List<String> optionsDefaultSelectionQualityList;

    private final boolean optionsConfirmProviderMapping;
}
