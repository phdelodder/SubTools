package extensions.org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UnmodifiableElements extends Elements {

    public static UnmodifiableElements EMPTY = new UnmodifiableElements();

    public UnmodifiableElements() {
    }

    public UnmodifiableElements(int initialCapacity) {
        super(initialCapacity);
    }

    public UnmodifiableElements(Collection<Element> elements) {
        super(elements);
    }

    public UnmodifiableElements(List<Element> elements) {
        super(elements);
    }

    public UnmodifiableElements(Element... elements) {
        super(Arrays.asList(elements));
    }

    public Elements removeAttr(String attributeKey) {
        return this;
    }

    public Elements addClass(String className) {
        return this;
    }

    public Elements removeClass(String className) {
        return this;
    }

    public Elements toggleClass(String className) {
        return this;
    }


    public Elements prepend(String html) {
        return this;
    }

    public Elements append(String html) {
        return this;
    }

    public Elements before(String html) {
        return this;
    }

    public Elements after(String html) {
        return this;
    }

    public Elements wrap(String html) {
        return this;
    }

    public Elements unwrap() {
        return this;
    }

    public Elements empty() {
        return this;
    }

    public Elements remove() {
        return this;
    }

    // filters

    @Override
    public Element set(int index, Element element) {
        throw new IllegalStateException();
    }

    @Override
    public Element remove(int index) {
        throw new IllegalStateException();
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeIf(Predicate<? super Element> filter) {
        return false;
    }

    @Override
    public void replaceAll(UnaryOperator<Element> operator) {
    }
}
