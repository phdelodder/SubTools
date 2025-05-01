package extensions.java.awt.GridBagConstraints;

import java.awt.*;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

@Extension
@UtilityClass
public class GridBagConstraintsExt {

    public static @Self GridBagConstraints insets(@This GridBagConstraints gridBagConstraints, Insets insets) {
        gridBagConstraints.insets = insets;
        return gridBagConstraints;
    }

    public static @Self GridBagConstraints fill(@This GridBagConstraints gridBagConstraints, int fill) {
        gridBagConstraints.fill = fill;
        return gridBagConstraints;
    }

    public static @Self GridBagConstraints gridx(@This GridBagConstraints gridBagConstraints, int gridx) {
        gridBagConstraints.gridx = gridx;
        return gridBagConstraints;
    }

    public static @Self GridBagConstraints gridy(@This GridBagConstraints gridBagConstraints, int gridy) {
        gridBagConstraints.gridy = gridy;
        return gridBagConstraints;
    }
}
