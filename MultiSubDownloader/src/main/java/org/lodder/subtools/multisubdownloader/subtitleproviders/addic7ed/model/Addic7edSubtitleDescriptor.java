package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model;

import java.io.Serializable;

import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.Language;

public record Addic7edSubtitleDescriptor(String version, @Nullable Language language, String url, String title,
                                         String uploader, boolean hearingImpaired) implements Serializable {
}
