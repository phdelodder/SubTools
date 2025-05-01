package org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model;

import java.io.Serializable;

import lombok.Builder;
import manifold.ext.props.rt.api.var;
import org.lodder.subtools.sublibrary.Language;

/**
 * Created by IntelliJ IDEA. User: lodder Date: 20/08/11 Time: 13:44 To change this template use Path | Settings | Path
 * Templates.
 */
@Builder
public class PodnapisiSubtitleDescriptor implements Serializable {

    @var String subtitleId;
    @var Language language;
    @var String uploaderName;
    // @var String uploaderUid;
    // @var String matchRanking;
    @var String releaseString;
    // @var String subtitleRating;
    @var String url;
    @var boolean hearingImpaired;
    @var boolean isInexact;
    @var String year;
    @var String omdb;
    @var String imdb;
}
