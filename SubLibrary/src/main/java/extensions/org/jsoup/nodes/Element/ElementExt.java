package extensions.org.jsoup.nodes.Element;

import extensions.org.jsoup.select.Elements.UnmodifiableElements;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Intercept;
import manifold.ext.rt.api.Jailbreak;
import manifold.ext.rt.api.This;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;
import org.jsoup.select.Collector;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.exception.WebpageException;

@Extension
public class ElementExt {

    // --------------- \\
    // Get all Element \\
    // --------------- \\

    public static Elements selectAllByClass(@This @Nullable Element element, String className) {
        return element == null ? UnmodifiableElements.EMPTY : element.getElementsByClass(requireNotEmpty(className));
    }

    public static Elements selectAllByTag(@This @Nullable Element element, String tagName) {
        return element == null ? UnmodifiableElements.EMPTY : element.getElementsByTag(requireNotEmpty(tagName));
    }

    public static Elements selectAllByAttribute(@This @Nullable Element element, String attribute) {
        return element == null ? UnmodifiableElements.EMPTY :
                element.getElementsByAttribute(requireNotEmpty(attribute));
    }

    public static Elements selectAllByCss(@This @Nullable Element element, String cssQuery) {
        return element == null ? UnmodifiableElements.EMPTY : element.getElements(requireNotEmpty(cssQuery));
    }

    public static Elements selectAll(@This @Nullable Element element, Evaluator evaluator) {
        return element == null ? UnmodifiableElements.EMPTY : Collector.collect(evaluator, element);
    }

    // ----------------- \\
    // Get First Element \\
    // ----------------- \\

    public static @Nullable Element selectFirstByClass(@This @Nullable Element element, String className) {
        return element == null ? null : element.selectFirst(new Evaluator.Class(requireNotEmpty(className)));
    }

    public static @Nullable Element selectFirstByTag(@This @Nullable Element element, String tagName) {
        return element == null ? null : element.selectFirst(new Evaluator.Tag(requireNotEmpty(tagName)));
    }

    public static @Nullable Element selectFirstByAttribute(@This @Nullable Element element, String attribute) {
        return element == null ? null : element.selectFirst(new Evaluator.Attribute(requireNotEmpty(attribute)));
    }

    public static @Nullable Element selectFirstByCss(@This @Nullable Element element, String cssQuery) {
        return element == null ? null : element.selectFirst(QueryParser.parse(requireNotEmpty(cssQuery)));
    }

    public static @Nullable Element selectFirstById(@This @Nullable Element element, String id) {
        return element == null ? null : element.selectFirst(new Evaluator.Id(requireNotEmpty(id)));
    }

    public static @Nullable Element selectFirstBy(@This @Nullable Element element, Evaluator evaluator) {
        return element == null ? null : Collector.findFirst(evaluator, element);
    }

    // ---------------- \\
    // Get n-th Element \\
    // ---------------- \\

    public static @Nullable Element selectNthByClass(@This @Nullable Element element, String className, int index) {
        return element == null ? null : selectNth(element, new Evaluator.Class(requireNotEmpty(className)), index);
    }

    public static @Nullable Element selectNthByTag(@This @Nullable Element element, String tagName, int index) {
        return element == null ? null : selectNth(element, new Evaluator.Tag(requireNotEmpty(tagName)), index);
    }

    public static @Nullable Element selectNthByAttribute(@This @Nullable Element element, String attribute,
            int index) {
        return element == null ? null :
                selectNth(element, new Evaluator.Attribute(requireNotEmpty(attribute)), index);
    }

    public static @Nullable Element selectNthByCss(@This @Nullable Element element, String cssQuery, int index) {
        return element == null ? null : selectNth(element, QueryParser.parse(requireNotEmpty(cssQuery)), index);
    }

    public static @Nullable Element selectNth(@This @Nullable Element element, @Jailbreak Evaluator eval, int idx) {
        eval.reset();
        return new NthElementFinder(eval).find(element, element, idx);
    }

    // -------------------------- \\
    // Get First Element or Throw \\
    // -------------------------- \\

    public static Element selectFirstByClassOrThrow(@This @Nullable Element element, String className)
            throws WebpageException {
        Element n = selectFirstByClass(element, className);
        if (n == null) {
            throw new WebpageException("Could not find element with class '%s'".formatted(className));
        }
        return n;
    }

    public static Element selectFirstByTagOrThrow(@This @Nullable Element element, String tagName)
            throws WebpageException {
        Element n = selectFirstByTag(element, tagName);
        if (n == null) {
            throw new WebpageException("Could not find element with tag '%s'".formatted(tagName));
        }
        return n;
    }

    public static Element selectFirstByAttributeOrThrow(@This @Nullable Element element, String attribute)
            throws WebpageException {
        Element n = selectFirstByAttribute(element, attribute);
        if (n == null) {
            throw new WebpageException("Could not find element with attribute '%s'".formatted(attribute));
        }
        return n;
    }

    public static Element selectFirstByCssOrThrow(@This @Nullable Element element, String cssQuery)
            throws WebpageException {
        Element n = selectFirstByCss(element, cssQuery);
        if (n == null) {
            throw new WebpageException("Could not find element with css selector '%s'".formatted(cssQuery));
        }
        return n;
    }

    public static Element selectFirstByIdOrThrow(@This @Nullable Element element, String id)
            throws WebpageException {
        Element n = selectFirstById(element, id);
        if (n == null) {
            throw new WebpageException("Could not find element with id '%s'".formatted(id));
        }
        return n;
    }

    public static Element selectOrThrow(@This @Nullable Element element, Evaluator evaluator)
            throws WebpageException {
        Element n = selectFirstBy(element, evaluator);
        if (n == null) {
            throw new WebpageException("Could not find element using selector '%s'".formatted(evaluator));
        }
        return n;
    }

    // ------------------------- \\
    // Get n-th Element or Throw \\
    // ------------------------- \\

    public static Element selectNthByClassOrThrow(@This @Nullable Element element, String className, int index)
            throws WebpageException {
        Element elem = selectNthByClass(element, className, index);
        if (elem == null) {
            throw new WebpageException("Could not find %sth element with class '%s'".formatted(index, className));
        }
        return elem;
    }

    public static Element selectNthByTagOrThrow(@This @Nullable Element element, String tagName, int index)
            throws WebpageException {
        Element elem = selectNthByTag(element, tagName, index);
        if (elem == null) {
            throw new WebpageException("Could not find %sth element with tag '%s'".formatted(index, tagName));
        }
        return elem;
    }

    public static Element selectNthByAttributeOrThrow(@This @Nullable Element element, String attribute, int index)
            throws WebpageException {
        Element elem = selectNthByAttribute(element, attribute, index);
        if (elem == null) {
            throw new WebpageException("Could not find %sth element with attribute '%s'".formatted(index, attribute));
        }
        return elem;
    }

    public static Element selectNthByCssOrThrow(@This @Nullable Element element, String cssQuery, int index)
            throws WebpageException {
        Element elem = selectNthByCss(element, cssQuery, index);
        if (elem == null) {
            throw new WebpageException("Could not find %sth element with css selector '%s'".formatted(index, cssQuery));
        }
        return elem;
    }

    public static Element selectNthOrThrow(@This @Nullable Element element, Evaluator eval, int index)
            throws WebpageException {
        Element elem = selectNth(element, eval, index);
        if (elem == null) {
            throw new WebpageException(
                    "Could not find %sth element using selector '%s'".formatted(index, eval.toString()));
        }
        return elem;
    }

    // ------------ \\
    // Get Elements \\
    // ------------ \\

    public static Elements getElements(@This @Nullable Element element, String cssQuery) {
        return element == null ? new Elements() : element.select(cssQuery);
    }

    // --------------- \\
    // Element methods \\
    // --------------- \\


    @Intercept
    public static String text(@This @Nullable Element element) {
        return element == null ? "" : element.text();
    }

    @Intercept
    public static @Nullable Element parent(@This @Nullable Element element) {
        return element == null ? null : element.parent();
    }

    // --------------- \\
    // Utility methods \\
    // --------------- \\

    private static String requireNotEmpty(String value) {
        Validate.notEmpty(value);
        return value;
    }
}
