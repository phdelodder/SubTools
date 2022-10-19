package org.lodder.subtools.multisubdownloader.framework.service.providers;

import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.Container;

public interface ServiceProvider {
    int getPriority();

    void register(Container app, UserInteractionHandler userInteractionHandler);
}
