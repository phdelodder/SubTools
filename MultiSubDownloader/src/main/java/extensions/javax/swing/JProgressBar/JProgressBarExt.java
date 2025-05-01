package extensions.javax.swing.JProgressBar;

import javax.swing.*;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;


@UtilityClass
@Extension
public class JProgressBarExt {

    public static @Self JProgressBar indeterminate(@This JProgressBar progressBar, boolean indeterminate) {
        progressBar.setIndeterminate(indeterminate);
        return progressBar;
    }
}
