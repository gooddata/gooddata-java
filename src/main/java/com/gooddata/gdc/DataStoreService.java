/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineImpl;
import com.gooddata.UriPrefixer;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static com.gooddata.Validate.notEmpty;
import static com.gooddata.Validate.notNull;

/**
 */
public class DataStoreService {

    private final Sardine sardine;
    private final GdcService gdcService;
    private UriPrefixer prefixer;

    public DataStoreService(HttpClientBuilder httClientBuilder, GdcService gdcService, String user, String pass) {
        this.gdcService = notNull(gdcService, "gdcService");
        sardine = new SardineImpl(httClientBuilder, user, pass);
    }

    private UriPrefixer getPrefixer() {
        if (prefixer == null) {
            final String webDAVUri = gdcService.getGdc().getLink("uploads").getLink();
            prefixer = new UriPrefixer(webDAVUri);
            sardine.enablePreemptiveAuthentication(prefixer.getDefaultUri().getHost());
        }
        return prefixer;
    }

    private URI getUri(String uri) {
        return getPrefixer().mergeUris(uri);
    }

    public void upload(String path, InputStream stream) {
        notEmpty(path, "path");
        upload(getUri(path), stream);
    }

    private void upload(URI url, InputStream stream) {
        try {
            sardine.put(url.toString(), stream);
        } catch (IOException e) {
            throw new DataStoreException("Unable to upload to " + url, e);
        }
    }

    public InputStream download(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        try {
            return sardine.get(uri.toString());
        } catch (IOException e) {
            throw new DataStoreException("Unable to download from " + uri, e);
        }
    }

    public void delete(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        try {
            sardine.delete(uri.toString());
        } catch (IOException e) {
            throw new DataStoreException("Unable to delete " + uri, e);
        }
    }
}
