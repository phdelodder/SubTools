package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model;

import java.io.Serializable;

import com.pivovarit.function.ThrowingSupplier;
import lombok.EqualsAndHashCode;
import manifold.ext.props.rt.api.val;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.model.SeasonEpisode;

@EqualsAndHashCode
public class SubsceneSubtitleDescriptor implements Serializable {

    @val @Nullable Language language;
    @val @Nullable String name;
    @val boolean hearingImpaired;
    @val @Nullable String uploader;
    @val @Nullable String comment;
    @val @Nullable SeasonEpisode seasonEpisode;
    @EqualsAndHashCode.Exclude @val ThrowingSupplier<String, SubsceneException> urlSupplier;

    public SubsceneSubtitleDescriptor(@Nullable Language language,
        @Nullable String name, boolean hearingImpaired,@Nullable String uploader, @Nullable String comment,
        ThrowingSupplier<String, SubsceneException> urlSupplier) {
        this.language = language;
        this.name = name;
        this.hearingImpaired = hearingImpaired;
        this.uploader = uploader;
        this.comment = comment;
        this.seasonEpisode = SeasonEpisode.fromText(name).orElse(null);
        this.urlSupplier = urlSupplier;
    }
}
