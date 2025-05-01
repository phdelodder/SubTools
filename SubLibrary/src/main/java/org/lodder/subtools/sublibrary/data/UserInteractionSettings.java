package org.lodder.subtools.sublibrary.data;

import java.util.List;

import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.sublibrary.control.VideoPatterns;

@AllArgsConstructor
public class UserInteractionSettings implements UserInteractionSettingsIntf {

    @override @val boolean optionsAlwaysConfirm;

    @override @val boolean optionsMinAutomaticSelection;

    @override @val int optionsMinAutomaticSelectionValue;

    @override @val boolean optionsDefaultSelection;

    @override @val List<VideoPatterns.Source> optionsDefaultSelectionQualityList;

    @override @val boolean optionsConfirmProviderMapping;
}
