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
import java.util.function.Predicate;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;

@Setter
@RequiredArgsConstructor
@ExtensionMethod({ OptionalExtension.class })
public class Manager {

    private final HttpClient httpClient;
    private final InMemoryCache inMemoryCache;
    private final DiskCache diskCache;
    // @Getter
    // private final UserInteractionHandler userInputHandler;

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

    public boolean isCached(String url) {
        return inMemoryCache.contains(url);
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

    public interface PageContentBuilderUserAgentIntf {
        PageContentBuilderCacheTypeIntf userAgent(String userAgent);
    }

    public interface PageContentBuilderCacheTypeIntf {
        PageContentBuilderGetIntf cacheType(CacheType cacheType);
    }

    public interface PageContentBuilderGetIntf {
        String get() throws ManagerException;

        InputStream getAsInputStream() throws ManagerException;

        Optional<Document> getAsDocument() throws ParserConfigurationException, ManagerException;

        Optional<Document> getAsDocument(Predicate<String> emptyResultPredicate) throws ParserConfigurationException, ManagerException;

        org.jsoup.nodes.Document getAsJsoupDocument() throws ManagerException;

        Optional<org.jsoup.nodes.Document> getAsJsoupDocument(Predicate<String> emptyResultPredicate) throws ManagerException;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor
    public static class PageContentBuilder implements PageContentBuilderGetIntf, PageContentBuilderCacheTypeIntf,
            PageContentBuilderUserAgentIntf, PageContentBuilderUrlIntf {
        private final HttpClient httpClient;
        private final InMemoryCache<String, String> inMemoryCache;
        private String url;
        private String userAgent;
        private CacheType cacheType;

        @Override
        public String get() throws ManagerException {
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

        private String getContentWithoutCache(String urlString, String userAgent) throws ManagerException {
            try {
                return httpClient.doGet(new URL(urlString), userAgent);
            } catch (MalformedURLException e) {
                throw new ManagerException("incorrect url", e);
            } catch (HttpClientException e) {
                throw new ManagerException("Error occured with httpclient response: %s %s".formatted(e.getResponseCode(), e.getResponseMessage()));
            } catch (IOException | HttpClientSetupException e) {
                throw new ManagerException(e);
            }
        }

    }

    // ========= \\
    // GET VALUE \\
    // ========= \\

    public ValueBuilderKeyIntf getValueBuilder() {
        return new ValueBuilder<>(inMemoryCache, diskCache);
    }

    public interface ValueBuilderKeyIntf {
        ValueBuilderCacheTypeIntf key(String key);
    }

    public interface ValueBuilderCacheTypeIntf {
        ValueBuilderValueSupplierIntf cacheType(CacheType cacheType);
    }

    public interface ValueBuilderValueSupplierIntf {
        <T extends Serializable, X extends Exception> ValueBuilderGetIntf<T, X> valueSupplier(ThrowingSupplier<T, X> valueSupplier);

        <C extends Collection<T>, T extends Serializable, X extends Exception> ValueBuilderGetCollectionIntf<C, T, X>
                collectionSupplier(Class<T> collectionValueType, ThrowingSupplier<C, X> valueSupplier);

        <T extends Serializable, X extends Exception> ValueBuilderGetOptionalIntf<T, X>
                optionalSupplier(ThrowingSupplier<Optional<T>, X> valueSupplier);

        <X extends Exception> ValueBuilderGetOptionalIntIntf<X> optionalIntSupplier(ThrowingSupplier<OptionalInt, X> optionalIntSupplier);
    }

    public interface ValueBuilderGetIntf<T extends Serializable, X extends Exception> {
        T get() throws X;
    }

    public interface ValueBuilderGetCollectionIntf<C extends Collection<T>, T extends Serializable, X extends Exception> {
        C getCollection() throws X;
    }

    public interface ValueBuilderGetOptionalIntf<T extends Serializable, X extends Exception> {
        Optional<T> getOptional() throws X;
    }

    public interface ValueBuilderGetOptionalIntIntf<X extends Exception> {
        OptionalInt getOptionalInt() throws X;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static class ValueBuilder<C extends Collection<T>, T extends Serializable, X extends Exception>
            implements ValueBuilderGetOptionalIntf<T, X>, ValueBuilderGetIntf<T, X>, ValueBuilderCacheTypeIntf,
            ValueBuilderValueSupplierIntf, ValueBuilderKeyIntf, ValueBuilderGetCollectionIntf<C, T, X>, ValueBuilderGetOptionalIntIntf<X> {
        private final InMemoryCache inMemoryCache;
        private final DiskCache diskCache;
        private String key;
        private ThrowingSupplier<T, X> valueSupplier;
        private ThrowingSupplier<C, X> collectionSupplier;
        private ThrowingSupplier<Optional<T>, X> optionalSupplier;
        private ThrowingSupplier<OptionalInt, X> optionalIntSupplier;
        private CacheType cacheType;

        @Override
        public <S extends Serializable, E extends Exception> ValueBuilder<?, S, E> valueSupplier(ThrowingSupplier<S, E> valueSupplier) {
            this.valueSupplier = (ThrowingSupplier<T, X>) valueSupplier;
            return (ValueBuilder<?, S, E>) this;
        }

        @Override
        public <S extends Serializable, E extends Exception> ValueBuilder<?, S, E>
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
        public <L extends Collection<S>, S extends Serializable, E extends Exception> ValueBuilder<L, S, E>
                collectionSupplier(Class<S> collectionValueType, ThrowingSupplier<L, E> collectionSupplier) {
            this.collectionSupplier = (ThrowingSupplier<C, X>) collectionSupplier;
            return (ValueBuilder<L, S, E>) this;
        }

        @Override
        public T get() throws X {
            try {
                return switch (cacheType) {
                    case NONE -> valueSupplier.get();
                    case MEMORY -> (T) inMemoryCache.getOrPut(key, valueSupplier);
                    case DISK -> (T) diskCache.getOrPut(key, valueSupplier);
                    default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
                };
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw (X) e;
            }
        }

        @Override
        public C getCollection() throws X {
            try {
                return switch (cacheType) {
                    case NONE -> collectionSupplier.get();
                    case MEMORY -> (C) inMemoryCache.getOrPut(key, collectionSupplier);
                    case DISK -> (C) diskCache.getOrPut(key, collectionSupplier);
                    default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
                };
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw (X) e;
            }
        }

        @Override
        public Optional<T> getOptional() throws X {
            return switch (cacheType) {
                case NONE -> optionalSupplier.get();
                case MEMORY -> getOrPutOptional(inMemoryCache);
                case DISK -> getOrPutOptional(diskCache);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

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
            if (cache.contains(key)) {
                try {
                    return cache.get(key).mapToInt(i -> (Integer) i);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            OptionalInt value = optionalIntSupplier.get();
            value.ifPresentOrElse(v -> cache.put(key, v), () -> {
                if (cache instanceof DiskCache diskCache) {
                    diskCache.putWithoutPersist(key, null);
                }
            });
            return value;
        }

        private Optional<T> getOrPutOptional(InMemoryCache cache) throws X {
            if (cache.contains(key)) {
                return cache.get(key);
            }
            Optional<T> value = optionalSupplier.get();
            value.ifPresentOrElse(v -> cache.put(key, v), () -> {
                if (cache instanceof DiskCache diskCache) {
                    diskCache.putWithoutPersist(key, null);
                }
            });
            return value;
        }
    }

    // ========== \\
    // GET VALUES \\
    // ========== \\

    public ValuesBuilderKeyIntf getValuesBuilder() {
        return new ValuesBuilder<>(inMemoryCache, diskCache);
    }

    public interface ValuesBuilderKeyIntf {
        ValuesBuilderKeyMatchIntf key(String key);

        ValuesBuilderCacheTypeIntf keyFilter(Predicate<String> keyFilter);
    }

    public interface ValuesBuilderKeyMatchIntf {
        ValuesBuilderCacheTypeIntf matchType(CacheKeyMatchEnum matchType);
    }

    public interface ValuesBuilderCacheTypeIntf {
        ValuesBuilderValueTypeIntf cacheType(CacheType cacheType);
    }

    public interface ValuesBuilderValueTypeIntf {
        <T extends Serializable> ValuesBuilderGetIntf<T> valueType(Class<T> valueType);
    }

    public interface ValuesBuilderGetIntf<T extends Serializable> {
        List<Pair<String, T>> get();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static class ValuesBuilder<T extends Serializable>
            implements ValuesBuilderKeyIntf, ValuesBuilderKeyMatchIntf, ValuesBuilderCacheTypeIntf, ValuesBuilderValueTypeIntf,
            ValuesBuilderGetIntf<T> {
        private final InMemoryCache inMemoryCache;
        private final DiskCache diskCache;
        private String key;
        private CacheType cacheType;
        private Predicate<String> keyFilter;
        private CacheKeyMatchEnum matchType;
        private Class<T> valueType;

        @Override
        public <S extends Serializable> ValuesBuilder<S> valueType(Class<S> valueType) {
            this.valueType = (Class<T>) valueType;
            return (ValuesBuilder<S>) this;
        }

        @Override
        public List<Pair<String, T>> get() {
            return switch (cacheType) {
                case MEMORY -> get(inMemoryCache);
                case DISK -> get(diskCache);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        private List<Pair<String, T>> get(InMemoryCache cache) {
            if (keyFilter != null) {
                return cache.getEntries(keyFilter);
            }
            return switch (matchType) {
                case STARTING_WITH -> cache.getEntries(k -> ((String) k).startsWith(key));
                case ENDING_WITH -> cache.getEntries(k -> ((String) k).endsWith(key));
                case CONTAINING -> cache.getEntries(k -> ((String) k).contains(key));
                default -> throw new IllegalArgumentException("Unexpected value: " + matchType);
            };
        }
    }

    // =========== \\
    // STORE VALUE \\
    // =========== \\

    public StoreValueBuilderKeyIntf getStoreValueBuilder() {
        return new StoreValue(inMemoryCache, diskCache);
    }

    public interface StoreValueBuilderKeyIntf {
        StoreValueCacheTypeIntf key(String key);
    }

    public interface StoreValueCacheTypeIntf {
        StoreValueValueIntf cacheType(CacheType cacheType);
    }

    public interface StoreValueValueIntf {
        <T extends Serializable> StoreValueGetIntf<T> value(T value);
    }

    public interface StoreValueGetIntf<T> {
        void store();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static class StoreValue<T extends Serializable>
            implements StoreValueBuilderKeyIntf, StoreValueCacheTypeIntf, StoreValueGetIntf<T>, StoreValueValueIntf {
        private final InMemoryCache inMemoryCache;
        private final DiskCache diskCache;
        private String key;
        private CacheType cacheType;
        private T value;

        @Override
        public <S extends Serializable> StoreValueGetIntf<S> value(S value) {
            this.value = (T) value;
            return (StoreValueGetIntf<S>) this;
        }

        @Override
        public void store() {
            switch (cacheType) {
                case MEMORY -> inMemoryCache.put(key, value);
                case DISK -> diskCache.put(key, value);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            }
        }

    }

    // =================== \\
    // REMOVE CACHED VALUE \\
    // =================== \\

    public RemoveCacheValueBuilderKeyIntf getRemoveCacheValueBuilder() {
        return new RemoveCacheValue(inMemoryCache, diskCache);
    }

    public interface RemoveCacheValueBuilderKeyIntf {
        RemoveCacheValueCacheTypeIntf key(String key);
    }

    public interface RemoveCacheValueCacheTypeIntf {
        RemoveCacheValueGetIntf cacheType(CacheType cacheType);
    }

    public interface RemoveCacheValueGetIntf {
        void remove();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static class RemoveCacheValue
            implements RemoveCacheValueBuilderKeyIntf, RemoveCacheValueCacheTypeIntf, RemoveCacheValueGetIntf {
        private final InMemoryCache inMemoryCache;
        private final DiskCache diskCache;
        private String key;
        private CacheType cacheType;

        @Override
        public void remove() {
            switch (cacheType) {
                case MEMORY -> inMemoryCache.remove(key);
                case DISK -> diskCache.remove(key);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            }
        }
    }
}
