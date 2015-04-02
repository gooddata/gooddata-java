package com.gooddata.collections;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

import static com.gooddata.util.Validate.notNull;

/**
 * {@link Page} implementation wrapping next page link from REST API.
 */
class UriPage implements Page {

    private final URI pageUri;

    /**
     * Creates new instance with defined page URI.
     *
     * @param pageUri page URI
     */
    public UriPage(final String pageUri) {
        this.pageUri = new UriTemplate(notNull(pageUri, "pageUri")).expand();
    }

    /**
     * This is effectively no-op. Returns internal URI provided by REST API.
     *
     * @param uriBuilder not used internally, can be null
     * @return next page URI provided by REST API
     */
    @Override
    public URI getPageUri(final UriComponentsBuilder uriBuilder) {
        return pageUri;
    }
}
