package org.lodder.subtools.sublibrary.util;

import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;

public interface NamedMatchResult extends MatchResult {

    List<String> orderedGroups();

    Map<String, String> namedGroups();

    String group(String groupName);

    int start(String groupName);

    int end(String groupName);

}
