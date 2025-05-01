package org.lodder.subtools.multisubdownloader.settings.model.structure;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.Messages;

public enum SerieStructureTag implements StructureTag {

    SHOW_NAME("%SHOW NAME%", "StructureBuilderDialog.NameTvShow"),
    TITLE("%TITLE%", "StructureBuilderDialog.EpisodeTitle"),
    EPISODE_LONG("%EE%", "StructureBuilderDialog.NumberOfEpisodeLeadingZero"),
    EPISODES_LONG("%EEX%", "StructureBuilderDialog.NumberOfEpisodeLeadingZeroForMultiple"),
    EPISODE_SHORT("%E%", "StructureBuilderDialog.NumberOfEpisodeWithoutLeadingZero"),
    EPISODES_SHORT("%EX%", "StructureBuilderDialog.NumberOfEpisodeLeadingZeroMultiple"),
    SEASON_LONG("%SS%", "StructureBuilderDialog.NumberOfSeasonLeading"),
    SEASON_SHORT("%S%", "StructureBuilderDialog.NumberOfSeasonsWithoutLeading"),
    QUALITY("%QUALITY%", "StructureBuilderDialog.QualityOfRelease"),
    RELEASE_GROUP("%RELEASE GROUP%", "StructureBuilderDialog.ReleaseGroup");

    @val @override String label;
    @val @override String description;

    SerieStructureTag(String label, String descriptionMessage) {
        this.label = label;
        this.description = Messages.getText(descriptionMessage);
    }

}
