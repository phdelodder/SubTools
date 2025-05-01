package org.lodder.subtools.multisubdownloader.serviceproviders;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JTVsubtitlesAdapter;

public class TvSubtitlesServiceProvider implements ServiceProvider {

    /* We define a priority lower than SubtitleServiceProvider */
    @val @override int priority = 1;

    @Override
    public void register(Container app, UserInteractionHandler userInteractionHandler) {
        /* Add the SubtitleProvider to the store */
        app.makeSubtitleProviderStore().addProvider(new JTVsubtitlesAdapter(app.makeManager(), userInteractionHandler));
    }
}
