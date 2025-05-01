package org.lodder.subtools.multisubdownloader.cli.progress;

import static manifold.ext.props.rt.api.PropOption.*;

import manifold.ext.props.rt.api.var;
import manifold.ext.rt.api.Self;

abstract sealed class CLIProgress permits CLIFileIndexerProgress, CLISearchProgress {

    @var(Protected) int progress;
    @var(Protected) boolean enabled;
    @var(Protected) boolean verbose;

    protected CLIProgress() {
        enabled = true;
        verbose = false;
        progress = 0;
    }

    public void disable() {
        this.enabled = false;
        /* Print a line */
        System.out.println();
    }

    public @Self CLIProgress verbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    protected abstract void printProgress();

    protected void printProgBar(int percent) {
        // http://nakkaya.com/2009/11/08/command-line-progress-bar/
        StringBuilder bar = new StringBuilder("[");

        for (int i = 0; i < 50; i++) {
            if (i < percent / 2) {
                bar.append("=");
            } else if (i == percent / 2) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }

        bar.append("]   ").append(percent).append("%     ");
        System.out.print("\r" + bar);
    }
}
