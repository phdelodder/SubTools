package org.lodder.subtools.sublibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.cache.DiskCache;
import org.lodder.subtools.sublibrary.cache.InMemoryCache;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.http.HttpClient;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.lodder.subtools.sublibrary.util.http.HttpClientSetupException;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Document;

import com.pivovarit.function.ThrowingSupplier;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import name.falgout.jeffrey.throwing.Nothing;

@Setter
@RequiredArgsConstructor
@ExtensionMethod({ OptionalExtension.class })
public class Manager {

    private final HttpClient httpClient;
    private final InMemoryCache inMemoryCache;
    private final DiskCache diskCache;

    public String downloadText(String urlString) throws ManagerException {
        try {
            return httpClient.downloadText(urlString);
        } catch (MalformedURLException e) {
            throw new ManagerException("incorrect url", e);
        } catch (IOException e) {
            throw new ManagerException(e);
        }
    }

    public boolean store(String downloadlink, File file) throws ManagerException {
        try {
            return httpClient.doDownloadFile(new URL(downloadlink), file);
        } catch (MalformedURLException e) {
            throw new ManagerException("incorrect url", e);
        }
    }

    public String post(String urlString, String userAgent, Map<String, String> data) throws ManagerException {
        try {
            return httpClient.doPost(new URL(urlString), userAgent, data);
        } catch (MalformedURLException e) {
            throw new ManagerException("incorrect url", e);
        } catch (HttpClientSetupException | HttpClientException e) {
            throw new ManagerException(e);
        }
    }

    public void storeCookies(String domain, Map<String, String> cookieMap) {
        httpClient.storeCookies(domain, cookieMap);
    }

    // ================ \\
    // GET PAGE CONTENT \\
    // ================ \\

    public PageContentBuilderUrlIntf getPageContentBuilder() {
        return new PageContentBuilder(httpClient, inMemoryCache);
    }

    public interface PageContentBuilderUrlIntf {
        PageContentBuilderUserAgentIntf url(String url);
    }

    public interface PageContentBuilderUserAgentIntf extends PageContentBuilderCacheTypeIntf {
        PageContentBuilderCacheTypeIntf userAgent(String userAgent);
    }

    public interface PageContentBuilderCacheTypeIntf extends PageContentBuilderRetryIntf {
        PageContentBuilderRetryIntf cacheType(CacheType cacheType);
    }

    public interface PageContentBuilderRetryIntf extends PageContentBuilderGetIntf {
        PageContentBuilderRetryConditionIntf retries(int retries);
    }

    public interface PageContentBuilderRetryConditionIntf {
        PageContentBuilderRetryWaitIntf retryPredicate(Predicate<Exception> retryPredicate);
    }

    public interface PageContentBuilderRetryWaitIntf {
        PageContentBuilderGetIntf retryWait(int retryWait);
    }

    public interface PageContentBuilderGetIntf {
        String get() throws ManagerException;

        InputStream getAsInputStream() throws ManagerException;

        Optional<Document> getAsDocument() throws ParserConfigurationException, ManagerException;

        Optional<Document> getAsDocument(Predicate<String> emptyResultPredicate) throws ParserConfigurationException, ManagerException;

        org.jsoup.nodes.Document getAsJsoupDocument() throws ManagerException;

        Optional<org.jsoup.nodes.Document> getAsJsoupDocument(Predicate<String> emptyResultPredicate) throws ManagerException;

        JSONObject getAsJsonObject() throws ManagerException;

        JSONArray getAsJsonArray() throws ManagerException;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor
    public static class PageContentBuilder implements PageContentBuilderGetIntf, PageContentBuilderCacheTypeIntf,
            PageContentBuilderUserAgentIntf, PageContentBuilderUrlIntf, PageContentBuilderRetryIntf, PageContentBuilderRetryConditionIntf,
            PageContentBuilderRetryWaitIntf {
        private final HttpClient httpClient;
        private final InMemoryCache<String, String> inMemoryCache;
        private String url;
        private String userAgent = "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)";
        private CacheType cacheType;
        private int retries;
        private Predicate<Exception> retryPredicate;
        private int retryWait;

        @Override
        public PageContentBuilder retries(int retries) {
            if (retries < 0) {
                throw new IllegalStateException("Number of retries cannot be less than 0");
            }
            this.retries = retries;
            return this;
        }

        @Override
        public String get() throws ManagerException {
            if (cacheType == null) {
                cacheType = CacheType.NONE;
            }
            return switch (cacheType) {
                case NONE -> getContentWithoutCache(url, userAgent);
                case MEMORY -> inMemoryCache.getOrPut(url, () -> getContentWithoutCache(url, userAgent));
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        @Override
        public InputStream getAsInputStream() throws ManagerException {
            return IOUtils.toInputStream(get(), StandardCharsets.UTF_8);
        }

        @Override
        public Optional<Document> getAsDocument() throws ParserConfigurationException, ManagerException {
            return XMLHelper.getDocument(get());
        }

        @Override
        public Optional<Document> getAsDocument(Predicate<String> emptyResultPredicate) throws ParserConfigurationException, ManagerException {
            String html = get();
            return StringUtils.isBlank(html) || (emptyResultPredicate != null && emptyResultPredicate.test(html)) ? Optional.empty()
                    : XMLHelper.getDocument(html);
        }

        @Override
        public org.jsoup.nodes.Document getAsJsoupDocument() throws ManagerException {
            return Jsoup.parse(get());
        }

        @Override
        public Optional<org.jsoup.nodes.Document> getAsJsoupDocument(Predicate<String> emptyResultPredicate) throws ManagerException {
            String html = get();
            return StringUtils.isBlank(html) || (emptyResultPredicate != null && emptyResultPredicate.test(html)) ? Optional.empty()
                    : Optional.of(Jsoup.parse(html));
        }

        @Override
        public JSONObject getAsJsonObject() throws ManagerException {
            try {
                return new JSONObject(new String(getAsInputStream().readAllBytes(), StandardCharsets.UTF_8));
            } catch (JSONException | IOException | ManagerException e) {
                throw new ManagerException(e);
            }
        }

        @Override
        public JSONArray getAsJsonArray() throws ManagerException {
            try {
                return new JSONArray(new String(getAsInputStream().readAllBytes(), StandardCharsets.UTF_8));
            } catch (JSONException | IOException | ManagerException e) {
                throw new ManagerException(e);
            }
        }

        private String getContentWithoutCache(String urlString, String userAgent) throws ManagerException {
            try {
                return httpClient.doGet(new URL(urlString), userAgent);
            } catch (MalformedURLException e) {
                if (retries-- > 0 && retryPredicate.test(e)) {
                    return getContentWithoutCache(urlString, userAgent);
                }
                throw new ManagerException("incorrect url", e);
            } catch (HttpClientException e) {
                if (retries-- > 0 && retryPredicate.test(e)) {
                    try {
                        Thread.sleep(retryWait * 1000);
                    } catch (InterruptedException e1) {
                        // continue
                    }
                    return getContentWithoutCache(urlString, userAgent);
                }
                throw new ManagerException("Error occured with httpclient response: %s %s".formatted(e.getResponseCode(), e.getResponseMessage()),
                        e);
            } catch (IOException | HttpClientSetupException e) {
                if (retries-- > 0 && retryPredicate.test(e)) {
                    return getContentWithoutCache(urlString, userAgent);
                }
                throw new ManagerException(e);
            }
        }
    }

    // ============= \\
    // VALUE BUILDER \\
    // ============= \\

    public ValueBuilderCacheTypeIntf valueBuilder() {
        return new ValueBuilder<>(inMemoryCache, diskCache);
    }

    public interface ValueBuilderCacheTypeIntf {

        <T extends Serializable> ValueBuilderKeyIntf<T> cacheType(CacheType cacheType);

        <T extends Object> ValueBuilderKeyIntf<T> memoryCache();

        <T extends Serializable> ValueBuilderKeyIntf<T> diskCache();
    }

    public interface ValueBuilderKeyIntf<T> {
        ValueBuilderIsPresentIntf<T> key(String key);

        ValuesBuilderCacheTypeIntf<T> keyFilter(Predicate<String> keyFilter);
    }

    public interface ValueBuilderIsPresentIntf<T> extends ValuesBuilderCacheTypeIntf<T> {
        boolean isPresent();

        boolean isExpiredTemporary();

        boolean isTemporaryObject();

        OptionalLong getTemporaryTimeToLive();
    }

    public interface ValuesBuilderCacheTypeIntf<T> extends ValueBuilderRetryIntf<T> {
        <S extends T> ValueBuilderGetOptionalIntf<S, Nothing> returnType(Class<S> returnType);

        <C extends Collection<S>, S extends T> ValueBuilderGetCollectionIntf<C, S, Nothing> returnType(Class<C> collectionReturnType,
                Class<S> returnType);

        void remove();
    }

    public interface ValueBuilderRetryIntf<T> extends ValueBuilderValueSupplierIntf<T> {
        ValueBuilderRetryConditionIntf<T> retries(int retries);

        <S extends T> ValueBuilderGetValueStoreTempValueIntf<S, Nothing> value(S value);

        <S extends T> ValueBuilderGetOptionalStoreTempValueIntf<S, Nothing> optionalValue(Optional<S> optionalValue);

        ValueBuilderGetOptionalIntStoreTempValueIntf<Nothing> optionalIntValue(OptionalInt optionalIntValue);

        <C extends Collection<S>, S extends T> ValueBuilderGetCollectionIntf<C, S, Nothing> collectionValue(C collectionValue);
    }

    public interface ValueBuilderRetryConditionIntf<T> {
        ValueBuilderRetryWaitIntf<T> retryPredicate(Predicate<Exception> retryPredicate);
    }

    public interface ValueBuilderRetryWaitIntf<T> {
        ValueBuilderValueSupplierIntf<T> retryWait(int retryWait);
    }

    public interface ValueBuilderValueSupplierIntf<T> {

        <S extends T, X extends Exception> ValueBuilderGetValueStoreTempValueIntf<S, X> valueSupplier(ThrowingSupplier<S, X> valueSupplier);

        <C extends Collection<S>, S extends T, X extends Exception> ValueBuilderGetCollectionIntf<C, S, X>
                collectionSupplier(Class<S> collectionValueType, ThrowingSupplier<C, X> valueSupplier);

        <S extends T, X extends Exception> ValueBuilderGetOptionalStoreTempValueIntf<S, X>
                optionalSupplier(ThrowingSupplier<Optional<S>, X> valueSupplier);

        <X extends Exception> ValueBuilderGetOptionalIntStoreTempValueIntf<X>
                optionalIntSupplier(ThrowingSupplier<OptionalInt, X> optionalIntSupplier);
    }

    public interface ValueBuilderGetValueStoreTempValueIntf<T, X extends Exception> extends ValueBuilderGetValueIntf<T, X> {
        ValueBuilderGetValueStoreTempValueTtlIntf<T, X> storeTempNullValue();
    }

    public interface ValueBuilderGetValueStoreTempValueTtlIntf<T, X extends Exception>
            extends ValueBuilderGetValueIntf<T, X> {
        ValueBuilderGetValueIntf<T, X> timeToLive(long seconds);

        ValueBuilderGetValueIntf<T, X> timeToLiveFunction(Function<Long, Long> timeToLiveFunction);
    }

    public interface ValueBuilderGetValueIntf<T, X extends Exception> extends ValueBuilderStoreIntf<X> {
        T get() throws X;
    }

    public interface ValueBuilderGetOptionalStoreTempValueIntf<T, X extends Exception>
            extends ValueBuilderGetOptionalIntf<T, X> {
        ValueBuilderGetOptionalStoreTempValueTtlIntf<T, X> storeTempNullValue();
    }

    public interface ValueBuilderGetOptionalStoreTempValueTtlIntf<T, X extends Exception>
            extends ValueBuilderGetOptionalIntf<T, X> {
        ValueBuilderGetOptionalIntf<T, X> timeToLive(long seconds);

        ValueBuilderGetOptionalIntf<T, X> timeToLiveFunction(Function<Long, Long> timeToLiveFunction);
    }

    public interface ValueBuilderGetOptionalIntf<T, X extends Exception> extends ValueBuilderStoreIntf<X> {
        List<Pair<String, T>> getEntries();

        Optional<T> getOptional() throws X;
    }

    public interface ValueBuilderGetOptionalIntStoreTempValueIntf<X extends Exception> extends ValueBuilderGetOptionalIntIntf<X> {
        ValueBuilderGetOptionalIntStoreTempValueTtlIntf<X> storeTempNullValue();
    }

    public interface ValueBuilderGetOptionalIntStoreTempValueTtlIntf<X extends Exception> extends ValueBuilderGetOptionalIntIntf<X> {
        ValueBuilderGetOptionalIntIntf<X> timeToLive(long seconds);

        ValueBuilderGetOptionalIntIntf<X> timeToLiveFunction(Function<Long, Long> timeToLiveFunction);
    }

    public interface ValueBuilderGetOptionalIntIntf<X extends Exception> extends ValueBuilderStoreIntf<X> {
        OptionalInt getOptionalInt() throws X;
    }

    // public interface ValueBuilderGetCollectionStoreTempValueIntf<C extends Collection<T>, T, X extends Exception>
    // extends ValueBuilderGetCollectionIntf<C, T, X> {
    // ValueBuilderGetCollectionStoreTempValueTtlIntf<C, T, X> storeTempValue();
    // }
    //
    // public interface ValueBuilderGetCollectionStoreTempValueTtlIntf<C extends Collection<T>, T, X extends Exception>
    // extends ValueBuilderGetCollectionIntf<C, T, X> {
    // ValueBuilderGetCollectionIntf<C, T, X> timeToLive(long seconds);
    //
    // ValueBuilderGetCollectionIntf<C, T, X> timeToLiveFunction(Function<Long, Long> timeToLiveFunction);
    // }

    public interface ValueBuilderGetCollectionIntf<C extends Collection<T>, T, X extends Exception>
            extends ValueBuilderStoreIntf<X> {
        C getCollection() throws X;
    }

    public interface ValueBuilderStoreIntf<X extends Exception> {
        void store() throws X;

        void storeAsTempValue() throws X;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static class ValueBuilder<C extends Collection<T>, T, X extends Exception>
            implements ValueBuilderGetOptionalIntf<T, X>, ValueBuilderCacheTypeIntf,
            ValueBuilderValueSupplierIntf<T>, ValueBuilderKeyIntf<T>, ValueBuilderGetCollectionIntf<C, T, X>, ValueBuilderGetOptionalIntIntf<X>,
            ValueBuilderRetryIntf<T>, ValueBuilderRetryConditionIntf<T>, ValueBuilderRetryWaitIntf<T>,
            ValueBuilderIsPresentIntf<T>, ValuesBuilderCacheTypeIntf<T>,
            ValueBuilderStoreIntf<X>, ValueBuilderGetOptionalIntStoreTempValueIntf<X>, ValueBuilderGetOptionalStoreTempValueIntf<T, X>,
            ValueBuilderGetOptionalIntStoreTempValueTtlIntf<X>, ValueBuilderGetOptionalStoreTempValueTtlIntf<T, X>,
            ValueBuilderGetValueStoreTempValueIntf<T, X>, ValueBuilderGetValueStoreTempValueTtlIntf<T, X>, ValueBuilderGetValueIntf<T, X> {
        private final InMemoryCache inMemoryCache;
        private final DiskCache diskCache;
        private String key;
        private ThrowingSupplier<T, X> valueSupplier;
        private ThrowingSupplier<C, X> collectionSupplier;
        private ThrowingSupplier<Optional<T>, X> optionalSupplier;
        private ThrowingSupplier<OptionalInt, X> optionalIntSupplier;
        private T value;
        private Optional<T> optionalValue;
        private OptionalInt optionalIntValue;
        private C collectionValue;

        private CacheType cacheType;
        private Class<T> returnType;
        private int retries;
        private Predicate<Exception> retryPredicate;
        private int retryWait;
        private Predicate<String> keyFilter;
        @Setter(value = AccessLevel.NONE)
        private Long timeToLive;
        @Setter(value = AccessLevel.NONE)
        private boolean storeTempNullValue;
        private Function<Long, Long> timeToLiveFunction;

        //
        // @Override
        // public ValueBuilder<C, T, X> timeToLiveFunction(Function<Long, Long> timeToLiveFunction) {
        // this.timeToLiveFunction = timeToLiveFunction;
        // return this;
        // }
        @Override
        public ValueBuilder<C, T, Nothing> optionalIntValue(OptionalInt optionalIntValue) {
            this.optionalIntValue = optionalIntValue;
            return (ValueBuilder<C, T, Nothing>) this;
        }

        @Override
        public ValueBuilder<C, T, X> retries(int retries) {
            if (retries < 0) {
                throw new IllegalStateException("Number of retries cannot be less than 0");
            }
            this.retries = retries;
            return this;
        }

        @Override
        public ValueBuilder<?, ?, ?> memoryCache() {
            this.cacheType = CacheType.MEMORY;
            return this;
        }

        @Override
        public <S extends Serializable> ValueBuilder<?, S, ?> diskCache() {
            this.cacheType = CacheType.DISK;
            return (ValueBuilder<?, S, ?>) this;
        }

        @Override
        public <S extends T> ValueBuilder<?, S, Nothing> returnType(Class<S> returnType) {
            this.returnType = (Class<T>) returnType;
            return (ValueBuilder<?, S, Nothing>) this;
        }

        @Override
        public <L extends Collection<S>, S extends T> ValueBuilderGetCollectionIntf<L, S, Nothing>
                returnType(Class<L> collectionReturnType, Class<S> returnType) {
            this.returnType = (Class<T>) returnType;
            return (ValueBuilder<L, S, Nothing>) this;
        }

        @Override
        public <S extends T, E extends Exception> ValueBuilder<?, S, E> valueSupplier(ThrowingSupplier<S, E> valueSupplier) {
            this.valueSupplier = (ThrowingSupplier<T, X>) valueSupplier;
            return (ValueBuilder<?, S, E>) this;
        }

        @Override
        public <S extends T, E extends Exception> ValueBuilder<?, S, E>
                optionalSupplier(ThrowingSupplier<Optional<S>, E> valueSupplier) {
            this.optionalSupplier = (ThrowingSupplier) valueSupplier;
            return (ValueBuilder<?, S, E>) this;
        }

        @Override
        public <E extends Exception> ValueBuilder<?, Integer, E> optionalIntSupplier(ThrowingSupplier<OptionalInt, E> optionalIntSupplier) {
            this.optionalIntSupplier = (ThrowingSupplier) optionalIntSupplier;
            return (ValueBuilder<?, Integer, E>) this;
        }

        @Override
        public <L extends Collection<S>, S extends T, E extends Exception> ValueBuilder<L, S, E>
                collectionSupplier(Class<S> collectionValueType, ThrowingSupplier<L, E> collectionSupplier) {
            this.collectionSupplier = (ThrowingSupplier<C, X>) collectionSupplier;
            return (ValueBuilder<L, S, E>) this;
        }

        @Override
        public <S extends T> ValueBuilder<?, S, Nothing> value(S value) {
            this.value = value;
            return (ValueBuilder<?, S, Nothing>) this;
        }

        @Override
        public <S extends T> ValueBuilder<?, S, Nothing> optionalValue(Optional<S> optionalValue) {
            this.optionalValue = (Optional<T>) optionalValue;
            return (ValueBuilder<?, S, Nothing>) this;
        }

        @Override
        public <L extends Collection<S>, S extends T> ValueBuilder<L, S, Nothing> collectionValue(L collectionValue) {
            this.collectionValue = (C) collectionValue;
            return (ValueBuilder<L, S, Nothing>) this;
        }

        @Override
        public ValueBuilder<C, T, X> storeTempNullValue() {
            this.storeTempNullValue = true;
            return this;
        }

        @Override
        public ValueBuilder<C, T, X> timeToLive(long seconds) {
            this.timeToLive = seconds * 1000;
            return this;
        }

        // ######### \\
        // GET VALUE \\
        // ######### \\

        @Override
        public T get() throws X {
            return switch (cacheType) {
                case NONE -> valueSupplier.get();
                case MEMORY -> getOrPutValue(inMemoryCache);
                case DISK -> getOrPutValue(diskCache);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        private T getOrPutValue(InMemoryCache cache) throws X {
            if (cache.contains(key)) {
                try {
                    return (T) cache.get(key).get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            T value = executeSupplier(valueSupplier);
            if (value != null) {
                cache.put(key, value);
            } else {
                if (cache instanceof DiskCache diskCache) {
                    diskCache.putWithoutPersist(key, null);
                } else {
                    cache.put(key, null);
                }
            }
            return value;
        }

        // ############## \\
        // GET COLLECTION \\
        // ############## \\

        @Override
        public C getCollection() throws X {
            return switch (cacheType) {
                case NONE -> collectionSupplier.get();
                case MEMORY -> getOrPutCollection(inMemoryCache);
                case DISK -> getOrPutCollection(diskCache);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        private C getOrPutCollection(InMemoryCache cache) throws X {
            if (cache.contains(key)) {
                try {
                    return (C) cache.get(key).get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            C value = executeSupplier(collectionSupplier);
            cache.put(key, value);
            return value;
        }

        // ############ \\
        // GET OPTIONAL \\
        // ############ \\

        @Override
        public Optional<T> getOptional() throws X {
            if (returnType != null) {
                return switch (cacheType) {
                    case NONE -> Optional.empty();
                    case MEMORY -> inMemoryCache.get(key);
                    case DISK -> diskCache.get(key);
                    default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
                };
            }
            return switch (cacheType) {
                case NONE -> optionalSupplier.get();
                case MEMORY -> getOrPutOptional(inMemoryCache);
                case DISK -> getOrPutOptional(diskCache);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        private Optional<T> getOrPutOptional(InMemoryCache cache) throws X {
            boolean containsKey = cache.contains(key);
            if (!containsKey && storeTempNullValue) {
                timeToLive(calculateTtl()).store();
                return cache.get(key);
            } else if (containsKey && !isExpiredTemporary()) {
                return cache.get(key);
            } else {
                Optional<T> value = executeSupplier(optionalSupplier);
                value.ifPresentOrElse(v -> cache.put(key, v), () -> {
                    if (cache instanceof DiskCache diskCache) {
                        diskCache.putWithoutPersist(key, null);
                    } else {
                        cache.put(key, null);
                    }
                });
                return value;
            }
        }

        // ################ \\
        // GET OPTIONAL INT \\
        // ################ \\

        @Override
        public OptionalInt getOptionalInt() throws X {
            return switch (cacheType) {
                case NONE -> optionalIntSupplier.get();
                case MEMORY -> getOrPutOptionalInt(inMemoryCache);
                case DISK -> getOrPutOptionalInt(diskCache);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        private OptionalInt getOrPutOptionalInt(InMemoryCache cache) throws X {
            boolean containsKey = cache.contains(key);
            if (!containsKey && storeTempNullValue) {
                timeToLive(calculateTtl()).store();
                return cache.get(key).mapToOptionalInt();
            } else if (containsKey && !isExpiredTemporary()) {
                return cache.get(key).mapToOptionalInt();
            } else {
                OptionalInt value = executeSupplier(optionalIntSupplier);
                value.ifPresentOrElse(v -> cache.put(key, v), () -> {
                    if (cache instanceof DiskCache diskCache) {
                        diskCache.putWithoutPersist(key, null);
                    } else {
                        cache.put(key, null);
                    }
                });
                return value;
            }
        }

        // ########### \\
        // GET ENTRIES \\
        // ########### \\

        @Override
        public List<Pair<String, T>> getEntries() {
            return switch (cacheType) {
                case NONE -> List.of();
                case MEMORY -> inMemoryCache.getEntries(keyFilter);
                case DISK -> diskCache.getEntries(keyFilter);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        // ########## \\
        // IS PRESENT \\
        // ########## \\

        @Override
        public boolean isPresent() {
            return switch (cacheType) {
                case NONE -> false;
                case MEMORY -> inMemoryCache.contains(key);
                case DISK -> diskCache.contains(key);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        // ###################### \\
        // TEMPORARY CACHE OBJECT \\
        // ###################### \\

        @Override
        public boolean isExpiredTemporary() {
            return switch (cacheType) {
                case NONE -> false;
                case MEMORY -> inMemoryCache.isTemporaryExpired(key);
                case DISK -> diskCache.isTemporaryExpired(key);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        @Override
        public boolean isTemporaryObject() {
            return switch (cacheType) {
                case NONE -> false;
                case MEMORY -> inMemoryCache.isTemporaryObject(key);
                case DISK -> diskCache.isTemporaryObject(key);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        @Override
        public OptionalLong getTemporaryTimeToLive() {
            return switch (cacheType) {
                case NONE -> OptionalLong.of(0);
                case MEMORY -> getTemporaryTimeToLive(inMemoryCache);
                case DISK -> getTemporaryTimeToLive(diskCache);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        private OptionalLong getTemporaryTimeToLive(InMemoryCache cache) {
            return cache.getTemporaryTimeToLive(key).map(v -> TimeUnit.SECONDS.convert(v, TimeUnit.MILLISECONDS));
        }

        // ##### \\
        // STORE \\
        // ##### \\

        @Override
        public void storeAsTempValue() throws X {
            store(true);
        }

        @Override
        public void store() throws X {
            store(false);
        }

        private void store(boolean storeAsTempValue) throws X {
            Object value;
            if (valueSupplier != null) {
                value = executeSupplier(valueSupplier);
            } else if (optionalSupplier != null) {
                value = executeSupplier(optionalSupplier).orElse(null);
            } else if (optionalIntSupplier != null) {
                value = OptionalExtension.mapToObj(executeSupplier(optionalIntSupplier), i -> i).orElse(null);
            } else if (collectionSupplier != null) {
                value = executeSupplier(collectionSupplier);
            } else if (optionalValue != null) {
                value = optionalValue.orElse(null);
            } else if (optionalIntValue != null) {
                value = optionalIntValue.mapToObj(i -> i).orElse(null);
            } else if (collectionValue != null) {
                value = collectionValue;
            } else {
                value = this.value;
            }
            if (storeAsTempValue || (storeTempNullValue && value == null)) {
                long ttl = timeToLive != null ? timeToLive : TimeUnit.SECONDS.convert(1, TimeUnit.DAYS);
                switch (cacheType) {
                    case MEMORY -> inMemoryCache.put(key, value, ttl);
                    case DISK -> diskCache.put(key, value, ttl);
                    default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
                }
            } else {
                switch (cacheType) {
                    case MEMORY -> inMemoryCache.put(key, value);
                    case DISK -> diskCache.put(key, value);
                    default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
                }
            }
        }

        // ###### \\
        // REMOVE \\
        // ###### \\

        @Override
        public void remove() {
            if (keyFilter != null) {
                switch (cacheType) {
                    case MEMORY -> inMemoryCache.deleteEntries(keyFilter);
                    case DISK -> diskCache.deleteEntries(keyFilter);
                    default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
                }
            } else {
                switch (cacheType) {
                    case MEMORY -> inMemoryCache.remove(key);
                    case DISK -> diskCache.remove(key);
                    default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
                }
            }
        }

        // ############## \\
        // HELPER METHODS \\
        // ############## \\

        private <V> V executeSupplier(ThrowingSupplier<V, X> supplier) throws X {
            try {
                return supplier.get();
            } catch (Exception e) {
                if (retries-- > 0 && retryPredicate.test(e)) {
                    try {
                        Thread.sleep(retryWait * 1000);
                    } catch (InterruptedException e1) {
                        throw new RuntimeException(e1);
                    }
                    return executeSupplier(supplier);
                }
                throw new RuntimeException("Exception while getting value (%s)".formatted(e.getMessage()), e);
            }
        }

        private long calculateTtl() {
            return getTemporaryTimeToLive().mapToObj(v -> timeToLiveFunction != null ? timeToLiveFunction.apply(v) : v * 2)
                    .orElseGet(() -> timeToLive != null ? timeToLive : TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        }
    }
}
