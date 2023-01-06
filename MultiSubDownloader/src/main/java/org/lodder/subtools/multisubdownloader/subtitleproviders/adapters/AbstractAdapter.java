package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pivovarit.function.ThrowingSupplier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @param <T> type of the subtitle objects returned by the api
 * @param <X> type of the exception thrown by the api
 */
@Getter
@RequiredArgsConstructor
abstract class AbstractAdapter<T, S extends ProviderSerieId, X extends Exception> implements Adapter<T, S, X>, SubtitleProvider {
    Logger LOGGER = LoggerFactory.getLogger(AbstractAdapter.class);
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    @RequiredArgsConstructor
    public static class ExecuteCall<T, X extends Exception, E extends ExecuteCall<T, X, E>> {
        private final ThrowingSupplier<T, X> supplier;
        private String message;
        private int retries = 3;
        private final List<Predicate<X>> retryPredicates = new ArrayList<>();
        private final List<HandleException<T, X>> exceptionHandlers = new ArrayList<>();

        @Getter
        @RequiredArgsConstructor
        private static class HandleException<T, X extends Exception> {
            private final Predicate<X> predicate;
            private final Function<X, T> exceptionFunction;
        }

        public E retryWhenException(Predicate<X> predicate) {
            retryPredicates.add(predicate);
            return (E) this;
        }

        public E handleException(Predicate<X> predicate, Function<X, T> exceptionFunction) {
            exceptionHandlers.add(new HandleException<>(predicate, exceptionFunction));
            return (E) this;
        }

        public E handleException(Predicate<X> predicate, Supplier<T> supplier) {
            return handleException(predicate, e -> supplier.get());
        }

        public E handleException(Function<X, T> exceptionFunction) {
            return handleException(e -> true, exceptionFunction);
        }

        public E handleException(Supplier<T> supplier) {
            return handleException(e -> true, e -> supplier.get());
        }

        public E retries(int retries) {
            if (retries <= 0) {
                throw new IllegalStateException("Retries should be greater than 0");
            }
            this.retries = retries;
            return (E) this;
        }

        public E message(String message) {
            this.message = message;
            return (E) this;
        }

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
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        // continue
                    }
                    return execute();
                } else {
                    try {
                        return exceptionHandlers.stream().filter(handleException -> handleException.getPredicate().test(exception)).findAny()
                                .map(handleException -> handleException.getExceptionFunction().apply(exception)).orElseThrow(() -> e);
                    } catch (Exception e1) {
                        throw (X) e1;
                    }
                }
            }
        }
    }
}
