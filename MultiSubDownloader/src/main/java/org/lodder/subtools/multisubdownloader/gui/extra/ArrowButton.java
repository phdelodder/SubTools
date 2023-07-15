package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArrowButton extends JButton {

    @Serial
    private static final long serialVersionUID = -4630720317499130016L;

    /**
     * The cardinal direction of the arrow(s),
     * any of {@link SwingConstants#NORTH}, {@link SwingConstants#SOUTH}, {@link SwingConstants#WEST} or {@link SwingConstants#EAST}
     */
    private int direction;

    private int arrowCount;

    private int arrowSize;

    public ArrowButton(int direction, int arrowCount, int arrowSize) {
        setMargin(new Insets(0, 2, 0, 2));
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.direction = direction;
        this.arrowCount = arrowCount;
        this.arrowSize = arrowSize;
    }

    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(arrowSize
                * (direction == SwingConstants.EAST || direction == SwingConstants.WEST ? arrowCount : 3)
                + getBorder().getBorderInsets(this).left + getBorder().getBorderInsets(this).right,
                arrowSize
                        * (direction == SwingConstants.NORTH || direction == SwingConstants.SOUTH ? arrowCount : 3)
                        + getBorder().getBorderInsets(this).top
                        + getBorder().getBorderInsets(this).bottom);
    }

    @Override
    public Dimension getMaximumSize() {
        return getMinimumSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // this will paint the background
        super.paintComponent(g);

        Color oldColor = g.getColor();
        g.setColor(isEnabled() ? getForeground() : getForeground().brighter());

        // paint the arrows
        int w = getSize().width;
        int h = getSize().height;
        for (int i = 0; i < arrowCount; i++) {
            paintArrow(g,
                    (w - arrowSize
                            * (direction == SwingConstants.EAST || direction == SwingConstants.WEST ? arrowCount : 1))
                            / 2
                            + arrowSize
                            * (direction == SwingConstants.EAST || direction == SwingConstants.WEST ? i : 0),
                    (h - arrowSize
                            * (direction == SwingConstants.EAST || direction == SwingConstants.WEST ? 1 : arrowCount))
                            / 2
                            + arrowSize
                            * (direction == SwingConstants.EAST || direction == SwingConstants.WEST ? 0 : i),
                    g.getColor());
        }

        g.setColor(oldColor);
    }

    private void paintArrow(Graphics g, int x, int y, Color highlight) {
        int mid, i, j;

        Color oldColor = g.getColor();
        boolean isEnabled = isEnabled();

        j = 0;
        arrowSize = Math.max(arrowSize, 2);
        mid = arrowSize / 2 - 1;

        g.translate(x, y);

        switch (direction) {
            case NORTH -> {
                for (i = 0; i < arrowSize; i++) {
                    g.drawLine(mid - i, i, mid + i, i);
                }
                if (!isEnabled) {
                    g.setColor(highlight);
                    g.drawLine(mid - i + 2, i, mid + i, i);
                }
            }
            case SOUTH -> {
                if (!isEnabled) {
                    g.translate(1, 1);
                    g.setColor(highlight);
                    for (i = arrowSize - 1; i >= 0; i--) {
                        g.drawLine(mid - i, j, mid + i, j);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(oldColor);
                }
                j = 0;
                for (i = arrowSize - 1; i >= 0; i--) {
                    g.drawLine(mid - i, j, mid + i, j);
                    j++;
                }
            }
            case WEST -> {
                for (i = 0; i < arrowSize; i++) {
                    g.drawLine(i, mid - i, i, mid + i);
                }
                if (!isEnabled) {
                    g.setColor(highlight);
                    g.drawLine(i, mid - i + 2, i, mid + i);
                }
            }
            case EAST -> {
                if (!isEnabled) {
                    g.translate(1, 1);
                    g.setColor(highlight);
                    for (i = arrowSize - 1; i >= 0; i--) {
                        g.drawLine(j, mid - i, j, mid + i);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(oldColor);
                }
                j = 0;
                for (i = arrowSize - 1; i >= 0; i--) {
                    g.drawLine(j, mid - i, j, mid + i);
                    j++;
                }
            }
            default -> {
            }
        }

        g.translate(-x, -y);
        g.setColor(oldColor);
    }
}
