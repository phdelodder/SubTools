package org.lodder.subtools.sublibrary.util;

import java.util.Map;
import java.util.regex.MatchResult;

import manifold.ext.props.rt.api.val;

public interface NamedMatchResult extends MatchResult {

    @val Map<String, Integer> namedGroups;

    String group(String groupName);

    int start(String groupName);

    int end(String groupName);

}
