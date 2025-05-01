package org.lodder.subtools.multisubdownloader.lib.control;

import static manifold.ext.props.rt.api.PropOption.*;

import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.Release;

public abstract sealed class ReleaseControl permits MovieReleaseControl, TvReleaseControl {

    @val(Protected) Settings settings;
    @val(Protected) Manager manager;
    @val(Abstract) Release videoFile;

    ReleaseControl(Settings settings, Manager manager) {
        this.settings = settings;
        this.manager = manager;
    }

    public abstract void process() throws ReleaseControlException;
}
