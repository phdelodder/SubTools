package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import static manifold.science.measures.TimeUnit.*;
import static org.lodder.subtools.sublibrary.util.Sleep.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.pivovarit.function.ThrowingSupplier;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import manifold.ext.rt.api.Self;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;

/**
 * @param <T> type of the subtitle objects returned by the api
 * @param <X> type of the exception thrown by the api
 */
@AllArgsConstructor
abstract sealed class AbstractAdapter<T, S extends ProviderSerieId, X extends Exception>
    implements Adapter<T, S, X>, SubtitleProvider
    permits JAddic7edAdapter, JAddic7edViaProxyAdapter, JOpenSubAdapter, JPodnapisiAdapter, JSubsceneAdapter,
    JTVsubtitlesAdapter {

    @val @override Manager manager;
    @val @override UserInteractionHandler userInteractionHandler;

    @RequiredArgsConstructor
    public static class ExecuteCall<T, X extends Exception> {
        private final ThrowingSupplier<T, X> supplier;
        private String message;
        private int retries = 3;
        private final List<Predicate<X>> retryPredicates = new ArrayList<>();
        private final List<HandleException<T, X>> exceptionHandlers = new ArrayList<>();

        private record HandleException<T, X extends Exception>(Predicate<X> predicate,
            Function<X, T> exceptionFunction) {}

        public @Self ExecuteCall<T, X> retryWhenException(Predicate<X> predicate) {
            retryPredicates.add(predicate);
            return this;
        }

        public @Self ExecuteCall<T, X> handleException(Predicate<X> predicate, Function<X, T> exceptionFunction) {
            exceptionHandlers.add(new HandleException<>(predicate, exceptionFunction));
            return this;
        }

        public @Self ExecuteCall<T, X> handleException(Predicate<X> predicate, Supplier<T> supplier) {
            return handleException(predicate, _ -> supplier.get());
        }

        public @Self ExecuteCall<T, X> handleException(Function<X, T> exceptionFunction) {
            return handleException(_ -> true, exceptionFunction);
        }

        public @Self ExecuteCall<T, X> handleException(Supplier<T> supplier) {
            return handleException(_ -> true, _ -> supplier.get());
        }

        public @Self ExecuteCall<T, X> retries(int retries) {
            if (retries <= 0) {
                throw new IllegalStateException("Retries should be greater than 0");
            }
            this.retries = retries;
            return this;
        }

        public @Self ExecuteCall<T, X> message(String message) {
            this.message = message;
            return this;
        }

        @SuppressWarnings("unchecked")
        public T execute() throws X {
            try {
                return supplier.get();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw e;
                }
                X exception = (X) e;
                if (retryPredicates.stream().anyMatch(predicate -> predicate.test(exception))) {
                    if (retries-- == 0) {
                        throw new RuntimeException("Max retries reached when calling %s".formatted(message));
                    }
                    sleep(5 Second);
                    return execute();
                } else {
                    try {
                        return exceptionHandlers.stream()
                            .filter(handleException -> handleException.predicate().test(exception))
                            .findAny()
                            .map(handleException -> handleException.exceptionFunction().apply(exception))
                            .orElseThrow(() -> e);
                    } catch (Exception e1) {
                        throw (X) e1;
                    }
                }
            }
        }
    }
}
