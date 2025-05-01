package org.lodder.subtools.multisubdownloader.gui.extra;

import manifold.ext.props.rt.api.val;

public class BoxModelProperties {
    @val Integer top;
    @val Integer left;
    @val Integer bottom;
    @val Integer right;

    public BoxModelProperties(Integer top=null, Integer left=null, Integer bottom=null, Integer right=null) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public BoxModelProperties(int padding) {
        this(padding, padding, padding, padding);
    }

    public String getInsets() {
        return "insets %s %s %s %s".formatted(getPadding(top), getPadding(left), getPadding(bottom), getPadding(right));
    }

    private String getPadding(Integer padding) {
        return padding == null ? "n" : String.valueOf(padding);
    }
}
