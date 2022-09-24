package org.lodder.subtools.sublibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.cache.DiskCache;
import org.lodder.subtools.sublibrary.cache.InMemoryCache;
import org.lodder.subtools.sublibrary.util.http.HttpClient;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.lodder.subtools.sublibrary.util.http.HttpClientSetupException;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Document;

import com.pivovarit.function.ThrowingSupplier;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@RequiredArgsConstructor
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
                throw new ManagerException("Error occured with httpclient response: " + e.getResponseCode()
                        + " " + e.getResponseMessage());
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

        <T extends Serializable, X extends Exception> ValueBuilderGetOptionalIntf<T, X>
                optionalValueSupplier(ThrowingSupplier<Optional<T>, X> valueSupplier);
    }

    public interface ValueBuilderGetIntf<T extends Serializable, X extends Exception> {
        T get() throws X;
    }

    public interface ValueBuilderGetOptionalIntf<T extends Serializable, X extends Exception> {
        Optional<T> getOptional() throws X;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static class ValueBuilder<T extends Serializable, X extends Exception>
            implements ValueBuilderGetOptionalIntf<T, X>, ValueBuilderGetIntf<T, X>, ValueBuilderCacheTypeIntf,
            ValueBuilderValueSupplierIntf, ValueBuilderKeyIntf {
        private final InMemoryCache inMemoryCache;
        private final DiskCache diskCache;
        private String key;
        private ThrowingSupplier<T, X> valueSupplier;
        private ThrowingSupplier<Optional<T>, X> optionalValueSupplier;
        private CacheType cacheType;

        @Override
        public <S extends Serializable, E extends Exception> ValueBuilder<S, E> valueSupplier(ThrowingSupplier<S, E> valueSupplier) {
            this.valueSupplier = (ThrowingSupplier<T, X>) valueSupplier;
            return (ValueBuilder<S, E>) this;
        }

        @Override
        public <S extends Serializable, E extends Exception> ValueBuilder<S, E>
                optionalValueSupplier(ThrowingSupplier<Optional<S>, E> valueSupplier) {
            this.optionalValueSupplier = (ThrowingSupplier) valueSupplier;
            return (ValueBuilder<S, E>) this;
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
        public Optional<T> getOptional() throws X {
            return switch (cacheType) {
                case NONE -> optionalValueSupplier.get();
                case MEMORY -> getOrPutOptional(inMemoryCache);
                case DISK -> getOrPutOptional(diskCache);
                default -> throw new IllegalArgumentException("Unexpected value: " + cacheType);
            };
        }

        private Optional<T> getOrPutOptional(InMemoryCache cache) throws X {
            if (cache.contains(key)) {
                return cache.get(key);
            }
            Optional<T> value = optionalValueSupplier.get();
            value.ifPresent(v -> cache.put(key, v));
            return value;
        }

    }
}
