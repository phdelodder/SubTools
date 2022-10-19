package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model;

import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.model.SeasonEpisode;

import com.pivovarit.function.ThrowingSupplier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode
@Accessors(chain = true)
@NoArgsConstructor
@Getter
@Setter
public class SubsceneSubtitleDescriptor {

    private Language language;
    private String name;
    private boolean hearingImpaired;
    private String uploader;
    private String comment;
    private SeasonEpisode seasonEpisode;
    @EqualsAndHashCode.Exclude
    private ThrowingSupplier<String, SubsceneException> urlSupplier;

    public SubsceneSubtitleDescriptor setName(String name) {
        this.name = name;
        this.seasonEpisode = SeasonEpisode.fromText(name).orElse(null);
        return this;
    }
}
