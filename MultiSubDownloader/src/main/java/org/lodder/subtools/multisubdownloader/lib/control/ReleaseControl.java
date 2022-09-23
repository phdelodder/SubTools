package org.lodder.subtools.multisubdownloader.lib.control;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.settings.model.TvdbMappings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter(value = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class ReleaseControl<T extends Release> {

    private final T release;
    private final Settings settings;
    private final Manager manager;

    public abstract void process(TvdbMappings tvdbMappings) throws ReleaseControlException;

    public Release getVideoFile() {
        return release;
    }
}
