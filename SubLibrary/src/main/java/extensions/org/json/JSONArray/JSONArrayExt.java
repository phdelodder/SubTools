package extensions.org.json.JSONArray;

import java.util.Iterator;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import org.json.JSONArray;
import org.json.JSONObject;

@Extension
@UtilityClass
public class JSONArrayExt {

    public static Stream<JSONObject> streamJsonObjects(@This JSONArray jsonArray) {
        Iterator<JSONObject> iterator = (Iterator<JSONObject>) (Iterator) jsonArray.iterator();
        return iterator.stream();
    }
}