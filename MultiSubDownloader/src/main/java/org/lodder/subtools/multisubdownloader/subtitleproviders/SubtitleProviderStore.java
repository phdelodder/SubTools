package org.lodder.subtools.multisubdownloader.subtitleproviders;

import java.util.ArrayList;
import java.util.List;

public class SubtitleProviderStore {
    protected List<SubtitleProvider> subtitleProviders = new ArrayList<>();

    public List<SubtitleProvider> getAllProviders() {
        return new ArrayList<>(this.subtitleProviders);
    }

    public void addProvider(SubtitleProvider provider) {
        if (!this.subtitleProviders.contains(provider)) {
            this.subtitleProviders.add(provider);
        }
    }

    public void deleteProvider(SubtitleProvider subtitleProvider) {
        if (!this.subtitleProviders.contains(subtitleProvider)) {
            return;
        }

        this.subtitleProviders.remove(subtitleProvider);
    }
}
