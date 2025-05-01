package extensions.org.w3c.dom.Document;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Intercept;
import manifold.ext.rt.api.This;
import org.jsoup.helper.Validate;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Extension
public class DocumentExt {

    // ----------------- \\
    // Get First Element \\
    // ----------------- \\

    @Intercept
    public static @Nullable NodeList getElementsByTagName(@This @Nullable Document document, String tagName) {
        return document == null ? null : document.getElementsByTagName(requireNotEmpty(tagName));
    }

    // --------------- \\
    // Utility methods \\
    // --------------- \\

    private static String requireNotEmpty(String value) {
        Validate.notEmpty(value);
        return value;
    }
}
