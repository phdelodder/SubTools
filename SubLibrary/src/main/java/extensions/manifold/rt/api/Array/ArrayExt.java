package extensions.manifold.rt.api.Array;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Consumer;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

@UtilityClass
@Extension
public class ArrayExt {

    /**
     * Performs an action for each element of the array.
     *
     * @param array the Array
     * @param action an action to perform on the elements
     */
    public static void forEach(@This Object array, Consumer<? super @Self(true) Object> action) {
        primitiveCheck(array);
        Arrays.stream((Object[]) array, 0, Array.getLength(array)).forEach(action);
    }


    private static void primitiveCheck(Object array) {
        Class<?> componentType = array.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            throw new IllegalArgumentException("$array has not a primitive component type: " +
                    array.getClass().getComponentType().getSimpleName());
        }
    }
}