package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;

public class TextAreaAppender extends AppenderBase<ILoggingEvent> {

  private PatternLayout fPatternLayout;
  private JTextArea fTextArea;

  public TextAreaAppender(final Context loggerContext, final JTextArea textArea) {
    fTextArea = textArea;

    // Log the date, level, class name (no package), and the message.
    fPatternLayout = new PatternLayout();
    fPatternLayout.setPattern("%d{HH:mm:ss.SSS} %-5level - %msg");
    fPatternLayout.setContext(loggerContext);
    fPatternLayout.start();

    // Make sure not to call any subclass methods right now.
    super.setContext(loggerContext);
  }


  @Override
  protected void append(final ILoggingEvent eventObject) {
    // Actual appending must be done from the EDT.
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        final String logStr = fPatternLayout.doLayout(eventObject);

        // If the text area already has lines in it, append a newline first.
        if (fTextArea.getDocument().getLength() > 0) {
          fTextArea.append("\n" + logStr);
        } else {
          fTextArea.setText(logStr);
        }
      }
    });
  }
}
