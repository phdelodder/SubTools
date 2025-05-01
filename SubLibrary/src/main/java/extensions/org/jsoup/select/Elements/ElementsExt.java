package extensions.org.jsoup.select.Elements;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Intercept;
import manifold.ext.rt.api.This;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jspecify.annotations.Nullable;

@Extension
@UtilityClass
public class ElementsExt {

    @Intercept
    public static String text(@This @Nullable Elements elements) {
        return elements == null ? "" : elements.text();
    }

    @Intercept
    public static @Nullable String attr(@This @Nullable Elements elements, String attribute) {
        return elements == null ? null : elements.attr(attribute);
    }

    @Intercept
    public static @Nullable Element first(@This @Nullable Elements elements) {
        return elements == null ? null : elements.first();
    }
}
