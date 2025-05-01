package org.lodder.subtools.sublibrary.data;

import java.util.List;

import manifold.ext.props.rt.api.val;
import org.lodder.subtools.sublibrary.control.VideoPatterns.Source;

public interface UserInteractionSettingsIntf {

    @val boolean optionsAlwaysConfirm;

    @val boolean optionsMinAutomaticSelection;

    @val int optionsMinAutomaticSelectionValue;

    @val boolean optionsDefaultSelection;

    @val List<Source> optionsDefaultSelectionQualityList;

    @val boolean optionsConfirmProviderMapping;
}
