/**
 * 
 */
package org.lodder.subtools.multisubdownloader.settings.model;

import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;

/**
 * @author delodder
 * 
 */
public class SearchSubtitlePriority {

  private SubtitleSource subtitleSource;
  private int priority;

  public SearchSubtitlePriority(SubtitleSource subtitleSource, int priority) {
    this.setPriority(priority);
    this.setSubtitleSource(subtitleSource);
  }

  /**
   * @return the subtitleSource
   */
  public SubtitleSource getSubtitleSource() {
    return subtitleSource;
  }

  /**
   * @param subtitleSource the subtitleSource to set
   */
  public void setSubtitleSource(SubtitleSource subtitleSource) {
    this.subtitleSource = subtitleSource;
  }

  /**
   * @return the priority
   */
  public int getPriority() {
    return priority;
  }

  /**
   * @param priority the priority to set
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }

}
