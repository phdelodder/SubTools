package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;
import java.awt.*;

public class ArrowButton extends JButton {

  /**
     *
     */
  private static final long serialVersionUID = -4630720317499130016L;

  /**
   * The cardinal direction of the arrow(s).
   */
  private int direction;

  /**
   * The number of arrows.
   */
  private int arrowCount;

  /**
   * The arrow size.
   */
  private int arrowSize;

  public ArrowButton(int direction, int arrowCount, int arrowSize) {
    setMargin(new Insets(0, 2, 0, 2));
    setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    this.direction = direction;
    this.arrowCount = arrowCount;
    this.arrowSize = arrowSize;
  }

  /**
   * Returns the cardinal direction of the arrow(s).
   * 
   * @see #setDirection(int)
   */
  public int getDirection() {
    return direction;
  }

  /**
   * Sets the cardinal direction of the arrow(s).
   * 
   * @param direction the direction of the arrow(s), can be SwingConstants.NORTH,
   *        SwingConstants.SOUTH, SwingConstants.WEST or SwingConstants.EAST
   * @see #getDirection()
   */
  public void setDirection(int direction) {
    this.direction = direction;
  }

  /**
   * Returns the number of arrows.
   */
  public int getArrowCount() {
    return arrowCount;
  }

  /**
   * Sets the number of arrows.
   */
  public void setArrowCount(int arrowCount) {
    this.arrowCount = arrowCount;
  }

  /**
   * Returns the arrow size.
   */
  public int getArrowSize() {
    return arrowSize;
  }

  /**
   * Sets the arrow size.
   */
  public void setArrowSize(int arrowSize) {
    this.arrowSize = arrowSize;
  }

  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  public Dimension getMinimumSize() {
    return new Dimension(arrowSize
        * (direction == SwingConstants.EAST || direction == SwingConstants.WEST ? arrowCount : 3)
        + getBorder().getBorderInsets(this).left + getBorder().getBorderInsets(this).right,
        arrowSize
            * (direction == SwingConstants.NORTH || direction == SwingConstants.SOUTH
                ? arrowCount
                : 3) + getBorder().getBorderInsets(this).top
            + getBorder().getBorderInsets(this).bottom);
  }

  public Dimension getMaximumSize() {
    return getMinimumSize();
  }

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
              * (direction == SwingConstants.EAST || direction == SwingConstants.WEST
                  ? arrowCount
                  : 1))
              / 2
              + arrowSize
              * (direction == SwingConstants.EAST || direction == SwingConstants.WEST ? i : 0),
          (h - arrowSize
              * (direction == SwingConstants.EAST || direction == SwingConstants.WEST
                  ? 1
                  : arrowCount))
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
    mid = (arrowSize / 2) - 1;

    g.translate(x, y);

    switch (direction) {
      case NORTH:
        for (i = 0; i < arrowSize; i++) {
          g.drawLine(mid - i, i, mid + i, i);
        }
        if (!isEnabled) {
          g.setColor(highlight);
          g.drawLine(mid - i + 2, i, mid + i, i);
        }
        break;
      case SOUTH:
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
        break;
      case WEST:
        for (i = 0; i < arrowSize; i++) {
          g.drawLine(i, mid - i, i, mid + i);
        }
        if (!isEnabled) {
          g.setColor(highlight);
          g.drawLine(i, mid - i + 2, i, mid + i);
        }
        break;
      case EAST:
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
        break;
    }

    g.translate(-x, -y);
    g.setColor(oldColor);
  }
}
