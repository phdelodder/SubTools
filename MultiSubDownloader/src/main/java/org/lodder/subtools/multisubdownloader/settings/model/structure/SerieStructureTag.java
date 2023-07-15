package org.lodder.subtools.multisubdownloader.settings.model.structure;

import org.lodder.subtools.multisubdownloader.Messages;

import lombok.Getter;

@Getter
public enum SerieStructureTag implements StructureTag {

    SHOW_NAME("%SHOW NAME%", "StructureBuilderDialog.NameTvShow"),
    TITLE("%TITLE%", "StructureBuilderDialog.EpisodeTitle"),
    EPISODE_LONG("%EE%", "StructureBuilderDialog.NumberOfEpisodeLeadingZero"),
    EPISODES_LONG("%EEX%", "StructureBuilderDialog.NumberOfEpisodeLeadingZeroForMultipe"),
    EPISODE_SHORT("%E%", "StructureBuilderDialog.NumberOfEpisodeWithoutLeadingZero"),
    EPISODES_SHORT("%EX%", "StructureBuilderDialog.NumberOfEpisodeLeadingZeroMultiple"),
    SEASON_LONG("%SS%", "StructureBuilderDialog.NumberOfSeasonLeading"),
    SEASON_SHORT("%S%", "StructureBuilderDialog.NumberOfSeasonsWithoutLeading"),
    QUALITY("%QUALITY%", "StructureBuilderDialog.QualityOfRelease"),
    DESCRIPTION("%DESCRIPTION%", "StructureBuilderDialog.Description");

    private final String label;
    private final String description;

    SerieStructureTag(String label, String descriptionMessage) {
        this.label = label;
        this.description = Messages.getString(descriptionMessage);
    }

}
