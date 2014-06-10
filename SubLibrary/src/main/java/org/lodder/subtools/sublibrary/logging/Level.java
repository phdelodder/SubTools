package org.lodder.subtools.sublibrary.logging;

/**
 * Created by IntelliJ IDEA.
 * User: lodder
 * Date: 4/5/11
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Level {
    OFF(Integer.MAX_VALUE), FATAL(600), ERROR(500), WARN(400), INFO(300), DEBUG(200), TRACE(100), ALL(Integer.MIN_VALUE);

    private int level;

    Level(int value) {
        level = value;
    }

    public int intValue() {
        return level;
    }
}
