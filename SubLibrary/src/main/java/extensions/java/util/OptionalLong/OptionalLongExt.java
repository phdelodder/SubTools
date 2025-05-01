package extensions.java.util.OptionalLong;

import java.util.Optional;
import java.util.OptionalLong;

import com.pivovarit.function.ThrowingFunction;
import com.pivovarit.function.ThrowingUnaryOperator;
import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import name.falgout.jeffrey.throwing.ThrowingLongUnaryOperator;

@Extension
@UtilityClass
public class OptionalLongExt {

    /**
     * If the value is present, apply the {@link ThrowingLongUnaryOperator} and return the value wrapped in an @{link
     * OptionalLong}. Otherwise, return an empty {@code OptionalLong}
     *
     * @param optional input object of the extension method
     * @param function the function to apply to the value if present
     * @param <X> type of the exception that can be thrown
     * @return the result of the function wrapped in an @{link OptionalLong} if the value is present, otherwise an empty
     *         {@code OptionalLong}
     * @throws X exception type of the throwing Function
     */
    public static <X extends Exception> OptionalLong map(@This OptionalLong optional,
            ThrowingLongUnaryOperator<X> function) throws X {
        return optional.isPresent() ? OptionalLong.of(function.applyAsLong(optional.getAsLong())) :
                OptionalLong.empty();
    }

    /**
     * If the value is present, apply the {@link ThrowingUnaryOperator} and return the value wrapped in an @{link
     * Optional}. Otherwise, return an empty {@code Optional}
     *
     * @param optional input object of the extension method
     * @param function the function to apply to the value if present
     * @param <T> type of the result value wrapped in the @{link Optional}
     * @param <X> type of the exception that can be thrown
     * @return the result of the function wrapped in an @{link Optional} if the value is present, otherwise an empty
     *         {@code Optional}
     * @throws X exception type of the throwing Function
     */
    public static <T, X extends Exception> Optional<T> mapToObj(@This OptionalLong optional,
            ThrowingFunction<Long, T, X> function) throws X {
        return optional.isPresent() ? Optional.ofNullable(function.apply(optional.getAsLong())) : Optional.empty();
    }
}
