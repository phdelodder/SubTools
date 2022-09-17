package org.lodder.subtools.multisubdownloader.cli.progress;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PROTECTED)
abstract class CLIProgress {

    private int progress;
    private boolean isEnabled;
    private boolean isVerbose;

    protected CLIProgress() {
        isEnabled = true;
        isVerbose = false;
        progress = 0;
    }

    public void disable() {
        this.isEnabled = false;
        /* Print a line */
        System.out.println("");
    }

    public void setVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
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
        System.out.print("\r" + bar.toString());
    }
}
