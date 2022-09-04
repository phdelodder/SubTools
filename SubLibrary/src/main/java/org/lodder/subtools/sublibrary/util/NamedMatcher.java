package org.lodder.subtools.sublibrary.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NamedMatcher implements NamedMatchResult {

    private Matcher matcher;
    private NamedPattern parentPattern;

    NamedMatcher() {
    }

    NamedMatcher(NamedPattern parentPattern, MatchResult matcher) {
        this.parentPattern = parentPattern;
        this.matcher = (Matcher) matcher;
    }

    NamedMatcher(NamedPattern parentPattern, CharSequence input) {
        this.parentPattern = parentPattern;
        this.matcher = parentPattern.pattern().matcher(input);
    }

    public Pattern standardPattern() {
        return matcher.pattern();
    }

    public NamedPattern namedPattern() {
        return parentPattern;
    }

    public NamedMatcher usePattern(NamedPattern newPattern) {
        this.parentPattern = newPattern;
        matcher.usePattern(newPattern.pattern());
        return this;
    }

    public NamedMatcher reset() {
        matcher.reset();
        return this;
    }

    public NamedMatcher reset(CharSequence input) {
        matcher.reset(input);
        return this;
    }

    public boolean matches() {
        return matcher.matches();
    }

    public NamedMatchResult toMatchResult() {
        return new NamedMatcher(this.parentPattern, matcher.toMatchResult());
    }

    public boolean find() {
        return matcher.find();
    }

    public boolean find(int start) {
        return matcher.find(start);
    }

    public boolean lookingAt() {
        return matcher.lookingAt();
    }

    public NamedMatcher appendReplacement(StringBuffer sb, String replacement) {
        matcher.appendReplacement(sb, replacement);
        return this;
    }

    public StringBuffer appendTail(StringBuffer sb) {
        return matcher.appendTail(sb);
    }

    @Override
    public String group() {
        return matcher.group();
    }

    @Override
    public String group(int group) {
        return matcher.group(group);
    }

    @Override
    public int groupCount() {
        return matcher.groupCount();
    }

    @Override
    public List<String> orderedGroups() {
        return IntStream.rangeClosed(1, groupCount()).sequential().mapToObj(this::group).collect(Collectors.toList());
    }

    @Override
    public String group(String groupName) {
        return group(groupIndex(groupName));
    }

    @Override
    public Map<String, String> namedGroups() {
        return IntStream.rangeClosed(1, groupCount()).sequential()
                .collect(LinkedHashMap::new,
                        (map, i) -> map.put(parentPattern.groupNames().get(i - 1), matcher.group(i)),
                        Map::putAll);
    }

    private int groupIndex(String groupName) {
        return parentPattern.groupNames().indexOf(groupName) + 1;
    }

    @Override
    public int start() {
        return matcher.start();
    }

    @Override
    public int start(int group) {
        return matcher.start(group);
    }

    @Override
    public int start(String groupName) {
        return start(groupIndex(groupName));
    }

    @Override
    public int end() {
        return matcher.end();
    }

    @Override
    public int end(int group) {
        return matcher.end(group);
    }

    @Override
    public int end(String groupName) {
        return end(groupIndex(groupName));
    }

    public NamedMatcher region(int start, int end) {
        matcher.region(start, end);
        return this;
    }

    public int regionEnd() {
        return matcher.regionEnd();
    }

    public int regionStart() {
        return matcher.regionStart();
    }

    public boolean hitEnd() {
        return matcher.hitEnd();
    }

    public boolean requireEnd() {
        return matcher.requireEnd();
    }

    public boolean hasAnchoringBounds() {
        return matcher.hasAnchoringBounds();
    }

    public boolean hasTransparentBounds() {
        return matcher.hasTransparentBounds();
    }

    public String replaceAll(String replacement) {
        return matcher.replaceAll(replacement);
    }

    public String replaceFirst(String replacement) {
        return matcher.replaceFirst(replacement);
    }

    public NamedMatcher useAnchoringBounds(boolean b) {
        matcher.useAnchoringBounds(b);
        return this;
    }

    public NamedMatcher useTransparentBounds(boolean b) {
        matcher.useTransparentBounds(b);
        return this;
    }

    public boolean equals(Matcher obj) {
        return matcher.equals(obj);
    }

    @Override
    public int hashCode() {
        return matcher.hashCode();
    }

    @Override
    public String toString() {
        return matcher.toString();
    }

}
