/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.gooddata.sdk.common.UriPrefixer;
import com.gooddata.sdk.service.httpcomponents.SingleEndpointGoodDataRestProvider;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;


import java.io.InputStream;
import java.net.URI;
import java.util.function.Supplier;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

@Component
public class DataStoreService {

    private final Supplier<String> stagingUriSupplier;
    private final URI gdcUri;
    private final WebClient webClient;
    private UriPrefixer prefixer;

    @Autowired
    public DataStoreService(SingleEndpointGoodDataRestProvider restProvider,
                            Supplier<String> stagingUriSupplier,
                            WebClient webClient) {
        this.stagingUriSupplier = notNull(stagingUriSupplier, "stagingUriSupplier");
        this.gdcUri = URI.create(notNull(restProvider.getEndpoint(), "endpoint").toUri());
        this.webClient = notNull(webClient, "webClient");
    }

    private UriPrefixer getPrefixer() {
        if (prefixer == null) {
            final String uriString = stagingUriSupplier.get();
            final URI uri = URI.create(uriString);
            prefixer = new UriPrefixer(uri.isAbsolute() ? uri : gdcUri.resolve(uriString));
        }
        return prefixer;
    }

    public URI getUri(String path) {
        return getPrefixer().mergeUris(path);
    }

    // Blocking upload (suitable for imperative code)
    public void upload(String path, InputStream stream) {
        notEmpty(path, "path");
        notNull(stream, "stream");
        uploadAsync(path, stream).block();
    }

    // Non-blocking upload (for reactive use-cases)
    public Mono<Void> uploadAsync(String path, InputStream stream) {
        notEmpty(path, "path");
        notNull(stream, "stream");
        URI url = getUri(path);
        return webClient.put()
            .uri(url)
            .body(BodyInserters.fromDataBuffers(DataBufferUtils.readInputStream(() -> stream,
                    new DefaultDataBufferFactory(), 4096)))
            .retrieve()
            .toBodilessEntity()
            .then()
            .onErrorMap(e -> wrapException("upload", url, e));
    }

    // Blocking download
    public InputStream download(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        return downloadAsync(path)
                .map(dataBuffer -> dataBuffer.asInputStream(true))
                .block();
    }

    // Non-blocking download (returns DataBuffer)
    public Mono<DataBuffer> downloadAsync(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        Flux<DataBuffer> dataBufferFlux = webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(
                    HttpStatusCode::isError,
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("Unknown error")
                        .map(body -> new DataStoreException("Download failed: " + uri + " Body: " + body, null))
                )
                .bodyToFlux(DataBuffer.class);

        return DataBufferUtils.join(dataBufferFlux)
                .onErrorMap(e -> wrapException("download", uri, e));
    }


    public void delete(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        deleteAsync(path).block();
    }

    public Mono<Void> deleteAsync(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        return webClient.delete()
                .uri(uri)
                .retrieve()
                .toBodilessEntity()
                .then()
                .onErrorMap(e -> wrapException("delete", uri, e));
    }

    private RuntimeException wrapException(String operation, URI uri, Throwable e) {
        return new DataStoreException("Unable to " + operation + " from " + uri, e);
    }
}
