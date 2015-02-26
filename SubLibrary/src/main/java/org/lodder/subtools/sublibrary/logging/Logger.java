package org.lodder.subtools.sublibrary.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

public enum Logger {

    instance;

    private final transient List<Listener> listeners = new LinkedList<Listener>();
    private Level logLevel = Level.ALL;
    
    public void addListener(Listener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public void setLogLevel(Level level) {
        logLevel = level;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void log(String log, Level level) {
        if (level.intValue() >= logLevel.intValue()) {
            synchronized (listeners) {
                for (Listener l : listeners)
                    l.log(log);
            }
        }
    }

    public void log(String messages) {
        this.log(messages, Level.INFO);
    }

    public void error(String messages) {
        this.log("Error: " + messages, Level.ERROR);
    }

    public void debug(String messages) {
        this.log("Debug: " + messages, Level.DEBUG);
    }

    public static String stack2String(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return "-------\r\n" + sw.toString() + "-------\r\n";
    }

    public static String stack2String(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return "-------\r\n" + sw.toString() + "-------\r\n";
    }

    public void trace(String klass, String method, String messages) {
        this.log("Trace: " + klass + ": (" + method + ") " + messages, Level.TRACE);
    }
}
