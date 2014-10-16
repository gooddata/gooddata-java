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
 * Uploads, downloads, deletes, ... at datastore
 */
public class DataStoreService {

    private final Sardine sardine;
    private final GdcService gdcService;
    private final URI gdcUri;
    private UriPrefixer prefixer;


    /**
     * Creates new DataStoreService
     * @param httClientBuilder httpClientBuilder to build datastore connection
     * @param gdcService used to obtain datastore URI
     * @param gdcUri complete GDC URI used to prefix possibly relative datastore path
     * @param user datastore user
     * @param pass datastore password
     */
    public DataStoreService(HttpClientBuilder httClientBuilder, GdcService gdcService, String gdcUri, String user, String pass) {
        this.gdcService = notNull(gdcService, "gdcService");
        this.gdcUri = URI.create(notEmpty(gdcUri, "gdcUri"));
        sardine = new SardineImpl(httClientBuilder, user, pass);
    }

    private UriPrefixer getPrefixer() {
        if (prefixer == null) {
            final String uriString = gdcService.getGdc().getUserStagingLink();
            final URI uri = URI.create(uriString);
            prefixer = new UriPrefixer(uri.isAbsolute() ? uri : gdcUri.resolve(uriString));
            sardine.enablePreemptiveAuthentication(prefixer.getDefaultUri().getHost());
        }
        return prefixer;
    }

    private URI getUri(String uri) {
        return getPrefixer().mergeUris(uri);
    }

    /**
     * Uploads given stream to given datastore path
     * @param path path where to upload to
     * @param stream stream to upload
     * @throws com.gooddata.gdc.DataStoreException in case upload failed
     */
    public void upload(String path, InputStream stream) {
        notEmpty(path, "path");
        notNull(stream, "stream");
        upload(getUri(path), stream);
    }

    private void upload(URI url, InputStream stream) {
        try {
            sardine.put(url.toString(), stream);
        } catch (IOException e) {
            throw new DataStoreException("Unable to upload to " + url, e);
        }
    }

    /**
     * Download given path and return data as stream
     * @param path path from where to download
     * @return download stream
     * @throws com.gooddata.gdc.DataStoreException in case download failed
     */
    public InputStream download(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        try {
            return sardine.get(uri.toString());
        } catch (IOException e) {
            throw new DataStoreException("Unable to download from " + uri, e);
        }
    }

    /**
     * Delete given path from datastore.
     * @param path path to delete
     * @throws com.gooddata.gdc.DataStoreException in case delete failed
     */
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
