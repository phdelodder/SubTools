package org.lodder.subtools.multisubdownloader.gui.extra;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.encoder.Encoder;

public class LogTextAppender extends AppenderBase<ILoggingEvent> {
    private Encoder<ILoggingEvent> encoder = new EchoEncoder<>();
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    private JTextArea jTextArea;

    public LogTextAppender(JTextArea jTextArea) {
        this.jTextArea = jTextArea;
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setPattern("%msg%n");
        patternLayoutEncoder.setContext(loggerContext);
        patternLayoutEncoder.start();
        
        setContext(loggerContext);
        start();
        loggerContext.getLogger("ROOT").addAppender(this);
    }

    @Override
    public void start() {
        try {
            encoder.init(out);
        } catch (IOException e) {
        }
        super.start();
    }

    @Override
    public void append(ILoggingEvent event) {
        try {
            encoder.doEncode(event);
            out.flush();
            final String line = out.toString();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (jTextArea != null) {
                        jTextArea.append(line);
                    }
                }
            });
            out.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
