package extensions.java.util.regex.Matcher;

import java.util.Optional;
import java.util.regex.Matcher;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class MatcherExt {

    public static Optional<String> getMatch(@This Matcher matcher) {
        return matcher.getMatch(0);
    }

    public static Optional<String> getMatch(@This Matcher matcher, int idx) {
        return matcher.find() ? Optional.of(matcher.group(idx)) : Optional.empty();
    }

}
