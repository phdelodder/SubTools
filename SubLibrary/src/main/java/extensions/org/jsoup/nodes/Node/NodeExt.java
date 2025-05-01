package extensions.org.jsoup.nodes.Node;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Intercept;
import manifold.ext.rt.api.This;
import name.falgout.jeffrey.throwing.ThrowingFunction;
import org.jsoup.nodes.Node;
import org.jspecify.annotations.Nullable;

@Extension
public class NodeExt {

    // ------------ \\
    // Node methods \\
    // ------------ \\

    @Intercept
    public static @Nullable Node parent(@This @Nullable Node node) {
        return node == null ? null : node.parent();
    }

    @Intercept
    public static String attr(@This @Nullable Node node, String attr) {
        return node == null ? null : node.attr(attr);
    }

    public static <T> T useAttrOrElse(@This @Nullable Node node, String attr, Function<String, T> mapper,
            Supplier<T> supplier) {
        return node != null && node.hasAttr(attr) ? mapper.apply(node.attr(attr)) : supplier.get();
    }

    public static Optional<String> getAttrOptional(@This @Nullable Node node, String attr) {
        return Optional.ofNullable(node.attr(attr));
    }

    // ------------- \\
    // Other methods \\
    // ------------- \\

    public static @Nullable Node filter(@This @Nullable Node node, Predicate<Node> predicate) {
        return node != null && predicate.test(node) ? node : null;
    }

    public static boolean matches(@This @Nullable Node node, Predicate<Node> predicate) {
        return node != null && predicate.test(node);
    }

    public static <@Nullable T> @Nullable T map(@This @Nullable Node node, Function<Node, T> mapper) {
        return node != null ? mapper.apply(node) : null;
    }

    public static <T> Optional<T> mapOptional(@This @Nullable Node node, Function<Node, T> mapper) {
        return node != null ? Optional.ofNullable(mapper.apply(node)) : Optional.empty();
    }

    public static <T> T mapOrElse(@This @Nullable Node node, Function<Node, T> mapper, T obj) {
        return node != null ? mapper.apply(node) : obj;
    }

    public static <T> T mapOrElseGet(@This @Nullable Node node, Function<Node, T> mapper,
            Supplier<T> elseSupplier) {
        return node != null ? mapper.apply(node) : elseSupplier.get();
    }

    public static <@Nullable T, X extends Exception> @Nullable T mapThrowing(@This @Nullable Node node,
            ThrowingFunction<Node, T, X> mapper)
            throws X {
        return node != null ? mapper.apply(node) : null;
    }

    public static Node orElseGet(@This @Nullable Node node, Supplier<Node> elementSupplier) {
        return node != null ? node : elementSupplier.get();
    }

    public static <X extends Exception> Node orElseThrow(@This @Nullable Node node,
            Supplier<X> exceptionSupplier) throws X {
        if (node == null) {
            throw exceptionSupplier.get();
        }
        return node;
    }

    public static void ifPresent(@This @Nullable Node node, Consumer<Node> consumer) {
        if (node != null) {
            consumer.accept(node);
        }
    }

    public static boolean isFound(@This @Nullable Node node) {
        return node != null;
    }

    public static boolean isNotNull(@This @Nullable Node node) {
        return node != null;
    }

    public static boolean isNotFound(@This @Nullable Node node) {
        return node == null;
    }

    public static boolean isNull(@This @Nullable Node node) {
        return node == null;
    }
}
