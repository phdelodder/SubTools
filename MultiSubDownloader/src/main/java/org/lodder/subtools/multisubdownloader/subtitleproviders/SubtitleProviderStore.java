package org.lodder.subtools.multisubdownloader.subtitleproviders;

import java.util.HashSet;
import java.util.Set;

public class SubtitleProviderStore {
    protected final Set<SubtitleProvider> subtitleProviders = new HashSet<>();

    public Set<SubtitleProvider> getAllProviders() {
        return new HashSet<>(this.subtitleProviders);
    }

    public void addProvider(SubtitleProvider provider) {
        this.subtitleProviders.add(provider);
    }

    public void deleteProvider(SubtitleProvider subtitleProvider) {
        this.subtitleProviders.remove(subtitleProvider);
    }
}
