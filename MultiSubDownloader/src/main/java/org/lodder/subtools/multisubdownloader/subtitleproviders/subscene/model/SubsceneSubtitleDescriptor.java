package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model;

import org.lodder.subtools.sublibrary.ManagerException;

import com.pivovarit.function.ThrowingSupplier;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@NoArgsConstructor
@Getter
@Setter
public class SubsceneSubtitleDescriptor {

    private String language;
    private String name;
    private boolean hearingImpaired;
    private String uploader;
    private String comment;
    private ThrowingSupplier<String, ManagerException> urlSupplier;
}
