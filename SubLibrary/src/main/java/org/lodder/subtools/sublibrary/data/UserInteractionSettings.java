package org.lodder.subtools.sublibrary.data;

import java.util.List;

import org.lodder.subtools.sublibrary.control.VideoPatterns;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserInteractionSettings implements UserInteractionSettingsIntf {

    private final boolean optionsAlwaysConfirm;

    private final boolean optionsMinAutomaticSelection;

    private final int optionsMinAutomaticSelectionValue;

    private final boolean optionsDefaultSelection;

    private final List<VideoPatterns.Source> optionsDefaultSelectionQualityList;

    private final boolean optionsConfirmProviderMapping;
}
