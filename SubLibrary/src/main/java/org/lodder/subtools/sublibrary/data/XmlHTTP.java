package org.lodder.subtools.sublibrary.data;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.PageContentBuilderCacheTypeIntf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter(value = AccessLevel.PROTECTED)
public class XmlHTTP {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlHTTP.class);

    @Getter(value = AccessLevel.PROTECTED)
    private final Manager manager;

    public PageContentBuilderCacheTypeIntf getXML(String url) {
        return manager.getPageContentBuilder().url(url).userAgent(null);
    }
}
