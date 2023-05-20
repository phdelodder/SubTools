package org.lodder.subtools.sublibrary.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JSONUtils {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Stream<JSONObject> stream(JSONArray jsonArray) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize((Iterator<JSONObject>) (Iterator) jsonArray.iterator(), Spliterator.ORDERED), false);
    }
}
