package org.lodder.subtools.sublibrary;

import static manifold.science.measures.TimeUnit.*;
import static manifold.science.util.UnitConstants.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.val;
import manifold.science.measures.Time;
import name.falgout.jeffrey.throwing.ThrowingFunction;
import name.falgout.jeffrey.throwing.ThrowingSupplier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.cache.Cache;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.cache.DiskCache;
import org.lodder.subtools.sublibrary.cache.InMemoryCache;
import org.lodder.subtools.sublibrary.util.Nothing;
import org.lodder.subtools.sublibrary.util.Sleep;
import org.lodder.subtools.sublibrary.util.http.CookieManager;
import org.lodder.subtools.sublibrary.util.http.HttpClient;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Document;

@AllArgsConstructor
public class Manager {

    @val HttpClient httpClient;
    @val InMemoryCache<String, String> inMemoryCache;
    @val DiskCache<String, Serializable> diskCache;

    public boolean store(String downloadLink, Path file) throws ManagerException {
        try {
            return httpClient.doDownloadFile(new URI(downloadLink).toURL(), file);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new ManagerException("incorrect url", e);
        }
    }

    public void storeCookies(String domain, Map<String, String> cookieMap) {
        httpClient.storeCookies(domain, cookieMap);
    }

    // ==== \\
    // POST \\
    // ==== \\

    public PostBuilder postBuilder(String url, @Nullable String userAgent=null) {
        return new PostBuilder(httpClient, url, userAgent);
    }

    public static class PostBuilder {
        @val HttpClient httpClient;
        @val String url;
        @val @Nullable String userAgent;
        private Map<String, String> data = new HashMap<>();

        public PostBuilder(HttpClient httpClient, String url, @Nullable String userAgent=null) {
            this.httpClient = httpClient;
            this.url = url;
            this.userAgent = userAgent;
        }

        public PostBuilder addData(String key, String value) {
            data.put(key, value);
            return this;
        }

        public String post() throws ManagerException {
            try {
                return httpClient.doPost(new URI(url).toURL(), userAgent, data);
            } catch (MalformedURLException | URISyntaxException e) {
                throw new ManagerException("incorrect url", e);
            } catch (HttpClientException e) {
                throw new ManagerException(e);
            }
        }

        public org.jsoup.nodes.Document postAsJsoupDocument() throws ManagerException {
            return Jsoup.parse(post());
        }

    }

    // ================ \\
    // GET PAGE CONTENT \\
    // ================ \\

    public String get(PageContentParams params) throws ManagerException {
        return switch (params.cacheType) {
            case NONE -> getContentWithoutCache(params);
            case MEMORY -> inMemoryCache.getOrPut(params.url + params.cookieManager(),
                () -> getContentWithoutCache(params));
            case DISK -> throw new IllegalArgumentException("Unexpected value: " + params.cacheType);
        };
    }

    public InputStream getAsInputStream(PageContentParams params) throws ManagerException {
        return get(params).toInputStream(StandardCharsets.UTF_8);
    }

    public @Nullable Document getAsDocument(PageContentParams params,
        @Nullable Predicate<String> emptyResultPredicate=null)
        throws ParserConfigurationException, ManagerException, IOException {
        Optional<String> asStringDocument = getAsStringDocument(params, emptyResultPredicate);
        return asStringDocument.isPresent() ? XMLHelper.getDocument(asStringDocument.get()) : null;
    }

    public org.jsoup.nodes.Document getAsJsoupDocument(PageContentParams params,
        @Nullable Predicate<String> emptyResultPredicate=null) throws ManagerException {
        return getAsStringDocument(params, emptyResultPredicate).map(Jsoup::parse).orElse(null);
    }

    private Optional<String> getAsStringDocument(PageContentParams params,
        @Nullable Predicate<String> emptyResultPredicate) throws ManagerException {
        if (emptyResultPredicate == null) {
            return Optional.of(get(params));
        }
        String html = get(params);
        return StringUtils.isBlank(html) || emptyResultPredicate.test(html) ? Optional.empty() : Optional.of(html);
    }

    public JSONObject getAsJsonObject(PageContentParams params) throws ManagerException {
        return new JSONObject(getAsJsonString(params));
    }

    public JSONArray getAsJsonArray(PageContentParams params) throws ManagerException {
        return new JSONArray(getAsJsonString(params));
    }

    private String getAsJsonString(PageContentParams params) throws ManagerException {
        try {
            return new String(getAsInputStream(params).readAllBytes(), StandardCharsets.UTF_8);
        } catch (JSONException | IOException | ManagerException e) {
            throw new ManagerException(e);
        }
    }

    private String getContentWithoutCache(PageContentParams params) throws ManagerException {
        return getContentWithoutCache(params.url, params.userAgent, params.retry, params.cookieManager);
    }

    private String getContentWithoutCache(String url, String userAgent, Retry retry,
        CookieManager cookieManager) throws ManagerException {
        try {
            return httpClient.doGet(new URI(url).toURL(), userAgent, cookieManager);
        } catch (HttpClientException e) {
            if (retry.canRetry() && retry.predicate.test(e)) {
                return getContentWithoutCache(url, userAgent, retry.decreaseRetries().sleep(), cookieManager);
            }
            throw new ManagerException(
                "Error occurred with httpclient response: %s %s while accessing %s".formatted(e.responseCode,
                    e.responseMessage, url), e);
        } catch (IOException e) {
            if (retry.canRetry() && retry.predicate.test(e)) {
                return getContentWithoutCache(url, userAgent, retry.decreaseRetries(), cookieManager);
            }
            throw new ManagerException(e);
        } catch (URISyntaxException e) {
            throw new ManagerException("Invalid url [%s]".formatted(url), e);
        }
    }

    public record Retry(int retries, Predicate<Exception> predicate, Time waitTime) {

        public static final Retry NONE = new Retry(0, null, 0 Second);

        public Retry {
            if (retries < 0) {
                throw new IllegalStateException("Number of retries cannot be less than 0");
            }
        }

        public Retry decreaseRetries() {
            return new Retry(retries - 1, predicate, waitTime);
        }

        public boolean canRetry() {
            return retries > 0;
        }

        public Retry sleep() {
            Sleep.sleep(waitTime);
            return this;
        }
    }

    // ============= \\
    // CACHE METHODS \\
    // ============= \\

    public record CacheKey(Manager manager, CacheType cacheType, String key) {
        public boolean isPresent() {
            return manager.getOptionalCache(cacheType).map(cache -> cache.contains(key)).orElse(false);
        }

        public boolean isExpiredTemporary() {
            return manager.getOptionalCache(cacheType).map(cache -> cache.isTemporaryExpired(key)).orElse(false);
        }

        public boolean isTemporaryObject() {
            return manager.getOptionalCache(cacheType).map(cache -> cache.isTemporaryObject(key)).orElse(false);
        }

        public Optional<Time> getTemporaryTimeToLive() {
            return manager.getOptionalCache(cacheType).map(cache -> cache.getTemporaryTimeToLive(key))
                .orElseGet(() -> Optional.of(0 Second));
        }

        public void remove() {
            manager.getCache(cacheType).remove(key);
        }

        public <V extends Serializable, X extends Exception> V get(ThrowingSupplier<V, X> supplier,
            Retry retry=Retry.NONE) throws X {
            Cache<String, V> cache = manager.getCache(cacheType);
            if (cache.contains(key)) {
                return cache.get(key).orElseThrow();
            }
            V value = executeSupplier(supplier, retry);
            if (value != null) {
                cache.put(key, value);
            } else {
                switch (cache) {
                    case DiskCache<String, ?> dCache -> dCache.putWithoutPersist(key, null);
                    case InMemoryCache<String, ?> mCache -> mCache.put(key, null);
                }
            }
            return value;
        }

        public <C extends Iterable<? extends V>, V extends Serializable, X extends Exception> C getCollection(
            ThrowingSupplier<C, X> supplier, Retry retry=Retry.NONE) throws X {
            Optional<Cache<String, C>> optionalCache = manager.getOptionalCache(cacheType);
            return optionalCache.mapThrowing(cache -> {
                if (cache.contains(key)) {
                    return cache.get(key).orElseThrow();
                }
                C value = executeSupplier(supplier, retry);
                cache.put(key, value);
                return value;
            }).orElseGetThrowing(supplier);
        }

        public <V> Optional<V> getOptional() {
            Optional<Cache<String, V>> optionalCache = manager.getOptionalCache(cacheType);
            return optionalCache.flatMap(cache -> cache.get(key));
        }

        public <V extends Serializable, X extends Exception> Optional<V> getOptional(
            ThrowingSupplier<Optional<V>, X> supplier, Retry retry=Retry.NONE, @Nullable Time timeToLive=null,
            boolean storeTempNullValue=false, boolean storeAsTempValue=false) throws X {
            Optional<Cache<String, V>> optionalCache = manager.getOptionalCache(cacheType);

            if (optionalCache.isPresent()) {
                Cache<String, V> cache = optionalCache.get();
                boolean containsKey = cache.contains(key);
                if (!containsKey && storeTempNullValue) {
                    store(Value.ofOptional(supplier), retry, storeAsTempValue, storeTempNullValue, timeToLive);
                    return cache.get(key);
                } else if (containsKey && !isExpiredTemporary()) {
                    return cache.get(key);
                } else {
                    Optional<V> object = executeSupplier(supplier, retry);
                    object.ifPresentOrElse(v -> cache.put(key, v), () -> {
                        switch (cache) {
                            case DiskCache<String, ?> dCache -> dCache.putWithoutPersist(key, null);
                            case InMemoryCache<String, ?> mCache -> mCache.put(key, null);
                        }
                    });
                    return object;
                }
            } else {
                return supplier.get();
            }
        }

        public <X extends Exception> OptionalInt getOptionalInt(
            ThrowingSupplier<OptionalInt, X> supplier, Retry retry=Retry.NONE, @Nullable Time timeToLive=null,
            boolean storeTempNullValue=false, boolean storeAsTempValue=false) throws X {

            return manager.getOptionalCache(cacheType).mapThrowing(cache -> {
                boolean containsKey = cache.contains(key);
                if (!containsKey && storeTempNullValue) {
                    store(Value.ofOptionalInt(supplier), retry, storeAsTempValue, storeTempNullValue, timeToLive);
                    return cache.get(key).mapToInt(t -> (int) t);
                } else if (containsKey && !isExpiredTemporary()) {
                    return cache.get(key).mapToInt(t -> (int) t);
                } else {
                    OptionalInt object = executeSupplier(supplier, retry);
                    object.ifPresentOrElse(v -> cache.put(key, v), () -> {
                        switch (cache) {
                            case DiskCache dCache -> dCache.putWithoutPersist(key, null);
                            case InMemoryCache<String, ?> mCache -> mCache.put(key, null);
                        }
                    });
                    return object;
                }
            }).orElseGetThrowing(supplier);
        }

        public <V, X extends Exception> void store(Value<V, X> value,
            Retry retry=Retry.NONE, boolean storeAsTempValue=false, boolean storeTempNullValue=false,
            @Nullable Time timeToLive=null) throws X {

            Object object = value.getValue(retry);
            Time ttl = null;
            if (storeAsTempValue || (storeTempNullValue && object == null)) {
                ttl = getTemporaryTimeToLive().map(time -> time * 2)
                    .orElseGet(() -> timeToLive != null ? timeToLive : 1 day);
            }
            manager.getCache(cacheType).put(key, object, ttl);
        }
    }

    public record CacheKeyFilter(Manager manager, CacheType cacheType, Predicate<String> keyFilter) {
        public void clearExpiredCache() {
            manager.getCache(cacheType).cleanup(keyFilter);
        }

        public void remove() {
            manager.getCache(cacheType).deleteEntries(keyFilter);
        }

        public <V extends Serializable> List<Pair<String, V>> getEntries(Class<V> valueType=null) {
            Optional<Cache<String, V>> optionalCache = manager.getOptionalCache(cacheType);
            return optionalCache.map(cache -> cache.getEntries(keyFilter)).orElseGet(List::of);
        }
    }

    private <V> Cache<String, V> getCache(CacheType cacheType) {
        Optional<Cache<String, V>> optionalCache = getOptionalCache(cacheType);
        return optionalCache.orElseThrow(() -> new IllegalArgumentException("Unexpected value: " + cacheType));
    }

    private <V> Optional<Cache<String, V>> getOptionalCache(CacheType cacheType) {
        return switch (cacheType) {
            case NONE -> Optional.empty();
            case MEMORY -> (Optional) Optional.of(inMemoryCache);
            case DISK -> (Optional) Optional.of(diskCache);
        };
    }

    public CacheKey getCache(CacheType cacheType, String key) {
        return new CacheKey(this, cacheType, key);
    }

    public CacheKeyFilter getCache(CacheType cacheType, Predicate<String> keyFilter) {
        return new CacheKeyFilter(this, cacheType, keyFilter);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public record Value<V, X extends Exception>(ThrowingFunction<Retry, V, X> supplier) {
        public static <V> Value<V, Nothing> of(V value) {
            return new Value<>(_ -> value);
        }

        public static <V, X extends Exception> Value<V, X> of(ThrowingSupplier<V, X> supplier) {
            return new Value<>(retry -> executeSupplier(supplier, retry));
        }

        public static <V> Value<V, Nothing> ofOptional(Optional<V> value) {
            return new Value<>(_ -> value.orElse(null));
        }

        public static <V, X extends Exception> Value<V, X> ofOptional(ThrowingSupplier<Optional<V>, X> supplier) {
            return new Value<>(retry -> executeSupplier(supplier, retry).orElse(null));
        }

        public static Value<Integer, Nothing> ofOptionalInt(OptionalInt value) {
            return ofOptional(() -> value.mapToObj(i -> i));
        }

        public static <X extends Exception> Value<Integer, X> ofOptionalInt(ThrowingSupplier<OptionalInt, X> supplier) {
            return ofOptional(() -> supplier.get().mapToObj(i -> i));
        }

        public static <C extends Collection<V>, V> Value<C, Nothing> ofCollection(C value) {
            return new Value<>(_ -> value);
        }

        public static <C extends Collection<V>, V, X extends Exception> Value<C, X> ofCollection(ThrowingSupplier<C,
            X> supplier) {
            return new Value<>(retry -> executeSupplier(supplier, retry));
        }

        public V getValue(Retry retry) throws X {
            return supplier.apply(retry);
        }
    }

    // ############## \\
    // HELPER METHODS \\
    // ############## \\

    private static <V, X extends Exception> V executeSupplier(ThrowingSupplier<V, X> supplier, Retry retry) throws X {
        try {
            return supplier.get();
        } catch (Exception e) {
            if (retry.canRetry() && retry.predicate.test(e)) {
                return executeSupplier(supplier, retry.decreaseRetries().sleep());
            }
            throw (X) e;
        }
    }
}
