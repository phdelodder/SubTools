package org.lodder.subtools.sublibrary;

import java.util.Locale;

import manifold.ext.props.rt.api.val;

/**
 * helper class to check the operating system this Java VM runs in
 * <p>
 * please keep the notes below as a pseudo-license
 * <p>
 * http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java compare to
 * http://svn.terracotta.org/svn/tc/dso/tags/2.6.4/code/base/common/src/com/tc/util/runtime/Os.java
 * http://www.docjar.com/html/api/org/apache/commons/lang/SystemUtils.java.html
 */
public final class OsCheck {
    /**
     * types of Operating Systems
     */
    public enum OSType {
        WINDOWS, MAC, LINUX, OTHER
    }

    @val static OSType operatingSystemType = calculatedOsType();

    private static OSType calculatedOsType() {
        // detect the operating system from the os.name System property
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if (os.contains("mac") || os.contains("darwin")) {
            return OSType.MAC;
        } else if (os.contains("win")) {
            return OSType.WINDOWS;
        } else if (os.contains("nux")) {
            return OSType.LINUX;
        } else {
            return OSType.OTHER;
        }
    }
}
