/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.warehouse;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.common.collections.CustomPageRequest;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.PageBrowser;
import com.gooddata.sdk.common.collections.PageRequest;
import com.gooddata.sdk.common.util.SpringMutableUri;
import com.gooddata.sdk.model.warehouse.Warehouse;
import com.gooddata.sdk.model.warehouse.WarehouseSchema;
import com.gooddata.sdk.model.warehouse.WarehouseSchemas;
import com.gooddata.sdk.model.warehouse.WarehouseTask;
import com.gooddata.sdk.model.warehouse.WarehouseUser;
import com.gooddata.sdk.model.warehouse.WarehouseUsers;
import com.gooddata.sdk.model.warehouse.Warehouses;
import com.gooddata.sdk.service.AbstractPollHandler;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.FutureResult;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.PollResult;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException; 
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.net.URI;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Provide access to warehouse API - create, update, list and delete warehouses.
 */
public class WarehouseService extends AbstractService {

    public static final UriTemplate SCHEMAS_TEMPLATE = new UriTemplate(WarehouseSchemas.URI);
    public static final UriTemplate SCHEMA_TEMPLATE = new UriTemplate(WarehouseSchema.URI);
    public static final UriTemplate WAREHOUSE_TEMPLATE = new UriTemplate(Warehouse.URI);
    public static final UriTemplate USERS_TEMPLATE = new UriTemplate(WarehouseUsers.URI);
    private static final String DEFAULT_SCHEMA_NAME = "default";

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     * @param restTemplate RESTful HTTP Spring template
     * @param settings settings
     */
    public WarehouseService(final WebClient webClient, final GoodDataSettings settings) {
        super(webClient, settings); 
    }

    /**
     * Create new warehouse.
     *
     * @param warehouse warehouse to create
     *
     * @return created warehouse
     */
    public FutureResult<Warehouse> createWarehouse(final Warehouse warehouse) {
        notNull(warehouse, "warehouse");
        final WarehouseTask task;
        try {
            task = webClient.post()
                    .uri(Warehouses.URI)
                    .bodyValue(warehouse)
                    .retrieve()
                    .bodyToMono(WarehouseTask.class)
                    .block();
        } catch (WebClientResponseException | GoodDataException e) {
            throw new GoodDataException("Unable to create Warehouse", e);
        }

        if (task == null) {
            throw new GoodDataException("Empty response when Warehouse POSTed to API");
        }

        return new PollResult<>(this, new AbstractPollHandler<WarehouseTask,Warehouse>(task.getPollUri(), WarehouseTask.class, Warehouse.class) {

            @Override
            public boolean isFinished(ClientResponse response) {
                return response.statusCode().equals(HttpStatus.CREATED);
            }

            @Override
            protected void onFinish() {
                if (!getResult().isEnabled()) {
                    throw new GoodDataException("Created warehouse, uri: " + getResult().getUri() + " is not enabled!");
                }
            }

            @Override
            public void handlePollResult(WarehouseTask pollResult) {
                try {
                    final Warehouse warehouse = webClient.get()
                            .uri(pollResult.getWarehouseUri())
                            .retrieve()
                            .bodyToMono(Warehouse.class)
                            .block();
                    setResult(warehouse);
                } catch (WebClientResponseException | GoodDataException e) {
                    throw new GoodDataException("Warehouse creation finished, but can't get created warehouse, uri: "
                            + pollResult.getWarehouseUri(), e);
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new GoodDataException("Unable to create warehouse", e);
            }
        });
    }

    /**
     * Delete Warehouse.
     * @param warehouse to delete
     */
    public void removeWarehouse(final Warehouse warehouse) {
        notNull(warehouse, "warehouse");
        notNull(warehouse.getUri(), "warehouse.uri");
        try {
            webClient.delete()
                    .uri(warehouse.getUri())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException | GoodDataException e) {
            throw new GoodDataException("Unable to delete Warehouse, uri: " + warehouse.getUri(), e);
        }
    }

    /**
     * Get Warehouse identified by given uri.
     * @param uri warehouse uri
     * @return Warehouse
     * @throws com.gooddata.sdk.common.GoodDataException when Warehouse can't be accessed
     */
    public Warehouse getWarehouseByUri(final String uri) {
        notEmpty(uri, "uri");
        try {
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Warehouse.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new WarehouseNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw new GoodDataException("Unable to get Warehouse instance " + uri, e);
        }
    }

    /**
     * Get Warehouse identified by given id.
     * @param id warehouse id
     * @return Warehouse
     * @throws com.gooddata.sdk.common.GoodDataException when Warehouse can't be accessed
     */
    public Warehouse getWarehouseById(String id) {
        notEmpty(id, "id");
        return getWarehouseByUri(uriFromId(id));
    }

    private static String uriFromId(String id) {
        return WAREHOUSE_TEMPLATE.expand(id).toString();
    }

    /**
     * Lists Warehouses. Returns empty list in case there are no warehouses.
     * Returns only first page if there's more instances than page limit. Use {@link PageBrowser#allItemsStream()} ()} to iterate
     * over all pages, or {@link PageBrowser#getAllItems()} ()} to load the entire list.
     *
     * @return {@link PageBrowser} first page of list of warehouse instances or empty list
     */
    public PageBrowser<Warehouse> listWarehouses() {
        return listWarehouses(new CustomPageRequest());
    }

    /**
     * Lists Warehouses. Returns empty list in case there are no warehouses.
     * Returns requested page (by page limit and offset). Use {@link #listWarehouses()} to get first page with default setting.
     *
     * @param startPage page to be listed
     * @return {@link PageBrowser} requested page of list of instances or empty list
     */
    public PageBrowser<Warehouse> listWarehouses(final PageRequest startPage) {
        notNull(startPage, "startPage");
        return new PageBrowser<>(startPage, page -> listWarehouses(getWarehousesUri(page)));
    }

    private URI getWarehousesUri() {
        return URI.create(Warehouses.URI);
    }

    private URI getWarehousesUri(final PageRequest page) {
        return page.getPageUri(new SpringMutableUri(getWarehousesUri()));
    }

    private Page<Warehouse> listWarehouses(final URI uri) {
        try {
            final Warehouses result = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Warehouses.class)
                    .block();
            if (result == null) {
                return new Page<>();
            }
            return result;
        } catch (WebClientResponseException | GoodDataException e) {
            throw new GoodDataException("Unable to list Warehouses", e);
        }
    }

    /**
     * Lists warehouse users. Returns empty list in case there are no users.
     * Use {@link PageBrowser#allItemsStream()} ()} to iterate over all pages,
     * or {@link PageBrowser#getAllItems()} ()} to load the entire list.
     *
     * @param warehouse warehouse
     * @return {@link PageBrowser} requested page of list of instances or empty list
     */
    public PageBrowser<WarehouseUser> listWarehouseUsers(final Warehouse warehouse) {
        return listWarehouseUsers(warehouse, new CustomPageRequest());
    }

    public PageBrowser<WarehouseUser> listWarehouseUsers(final Warehouse warehouse, final PageRequest startPage) {
        notNull(warehouse, "warehouse");
        notNull(warehouse.getId(), "warehouse.id");
        notNull(startPage, "startPage");

        return new PageBrowser<>(startPage,
                page -> listWarehouseUsers(warehouse, getWarehouseUsersUri(warehouse, page))
        );
    }

    /**
     * Lists warehouse users, starting with specified page. Returns empty list in case there are no users.
     * Use {@link PageBrowser#allItemsStream()} ()} to iterate over all pages,
     * or {@link PageBrowser#getAllItems()} ()} to load the entire list.
     *
     * @param warehouse warehouse
     * @param startPage page to start with
     * @return {@link PageBrowser} requested page of list of instances starting with startPage or empty list
     */
    private Page<WarehouseUser> listWarehouseUsers(final Warehouse warehouse, final URI uri) { //1
        try {
            final WarehouseUsers result = webClient.get() 
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(WarehouseUsers.class)
                    .block(); 
            return result == null ? new Page<>() : result;
        } catch (WebClientResponseException | GoodDataException e) { 
            throw new GoodDataException("Unable to list users of warehouse " + warehouse.getId(), e);
        }
    }

    private URI getWarehouseUsersUri(final Warehouse warehouse) {
        return USERS_TEMPLATE.expand(warehouse.getId());
    }

    private URI getWarehouseUsersUri(final Warehouse warehouse, final PageRequest page) {
        return page.getPageUri(new SpringMutableUri(getWarehouseUsersUri(warehouse)));
    }


    /**
     * Add given user to given warehouse.
     *
     * @param warehouse warehouse the user should be added to
     * @param user user to be added
     * @return added user in warehouse
     */
    public FutureResult<WarehouseUser> addUserToWarehouse(final Warehouse warehouse, final WarehouseUser user) {
        notNull(user, "user");
        notNull(warehouse, "warehouse");
        notNull(warehouse.getId(), "warehouse.id");

        final WarehouseTask task;
        try {
            task = webClient.post()
                    .uri(uriBuilder -> uriBuilder.path(WarehouseUsers.URI).build(warehouse.getId()))
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(WarehouseTask.class)
                    .block();
        } catch (WebClientResponseException | GoodDataException e) {
            throw new GoodDataException("Unable add user to warehouse " + warehouse.getId(), e);
        }
        if (task == null) {
            throw new GoodDataException("Empty response when user POSTed to API");
        }

        return new PollResult<>(this,
                new AbstractPollHandler<WarehouseTask, WarehouseUser>
                        (task.getPollUri(), WarehouseTask.class, WarehouseUser.class) {

            @Override
            public boolean isFinished(ClientResponse response) {
                return response.statusCode().equals(HttpStatus.CREATED);
            }

            @Override
            public void handlePollResult(WarehouseTask pollResult) {
                try {
                    final WarehouseUser newUser = webClient.get()
                            .uri(pollResult.getWarehouseUserUri())
                            .retrieve()
                            .bodyToMono(WarehouseUser.class)
                            .block();
                    setResult(newUser);
                } catch (WebClientResponseException | GoodDataException e) { 
                    throw new GoodDataException("User added to warehouse, but can't get it back, uri: "
                            + pollResult.getWarehouseUserUri(), e);
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new GoodDataException("Unable to add user to warehouse", e);
            }
        });
    }

    /**
     * Remove given user from warehouse instance
     * @param user to remove from warehouse
     * @return empty future result
     * @throws WarehouseUserNotFoundException when user for removal can't be found
     * @throws GoodDataException any other reason
     */
    public FutureResult<Void> removeUserFromWarehouse(final WarehouseUser user) {
        notNull(user, "user");
        notNull(user.getUri(), "user.uri");

        final WarehouseTask task;
        try {
            task = webClient.method(HttpMethod.DELETE)
                    .uri(user.getUri())
                    .retrieve()
                    .bodyToMono(WarehouseTask.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new WarehouseUserNotFoundException(user.getUri(), e);
            } else {
                throw e;
            }
        } catch (Exception e) { 
            throw new GoodDataException("Unable to remove Warehouse user from instance " + user.getUri(), e);
        }
        if (task == null) {
            throw new GoodDataException("Empty response when user removed");
        }

        return new PollResult<>(this,
                new AbstractPollHandler<WarehouseTask, Void>
                        (task.getPollUri(), WarehouseTask.class, Void.class) {

                @Override
                public boolean isFinished(ClientResponse response) {
                    return response.statusCode().equals(HttpStatus.CREATED);
                }

                @Override
                public void handlePollResult(WarehouseTask pollResult) {
                    setResult(null);
                }

                @Override
                public void handlePollException(final GoodDataRestException e) {
                    throw new GoodDataException("Unable to remove user from warehouse", e);
                }
        });
    }

    /**
     * Updates given Warehouse.
     *
     * @param toUpdate warehouse to be updated
     * @return updated warehouse
     * @throws com.gooddata.sdk.common.GoodDataException when update fails
     */
    public Warehouse updateWarehouse(final Warehouse toUpdate) {
        notNull(toUpdate, "warehouse");
        notNull(toUpdate.getUri(), "warehouse.uri");
        try {
            webClient.put()
                    .uri(toUpdate.getUri())
                    .bodyValue(toUpdate)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException | GoodDataException e) {
            throw new GoodDataException("Unable to update Warehouse, uri: " + toUpdate.getUri());
        }

        return getWarehouseByUri(toUpdate.getUri());
    }

    /**
     * list schemas for Warehouse
     *
     * @param warehouse to list schemas for
     * @return {@link PageBrowser} pageable list of warehouse schemas
     */
    public PageBrowser<WarehouseSchema> listWarehouseSchemas(final Warehouse warehouse) {
        return listWarehouseSchemas(warehouse, new CustomPageRequest());
    }

    /**
     * list schemas for Warehouse
     *
     * @param warehouse to list schemas for
     * @param startPage page to be listed
     * @return {@link PageBrowser} pageable list of warehouse schemas
     */
    public PageBrowser<WarehouseSchema> listWarehouseSchemas(final Warehouse warehouse, final PageRequest startPage) {
        return new PageBrowser<>(startPage,
                page -> listWarehouseSchemas(getWarehouseSchemasUri(warehouse, page))
        );
    }

    private URI getWarehouseSchemasUri(final Warehouse warehouse) {
        notNull(warehouse, "warehouse");
        notNull(warehouse.getId(), "warehouse.id");
        return SCHEMAS_TEMPLATE.expand(warehouse.getId());
    }

    private URI getWarehouseSchemasUri(final Warehouse warehouse, final PageRequest page) {
        notNull(page, "page");
        return page.getPageUri(new SpringMutableUri(getWarehouseSchemasUri(warehouse)));
    }

    private Page<WarehouseSchema> listWarehouseSchemas(final URI uri) {
        try {
            final WarehouseSchemas result = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(WarehouseSchemas.class)
                    .block();
            if (result == null) {
                return new Page<>();
            }
            return result;
        } catch (WebClientResponseException | GoodDataException e) {
            throw new GoodDataException("Unable to list Warehouse schemas", e);
        }
    }

    /**
     * get warehouse schema by name
     *
     * @param warehouse to get schema for
     * @param name of schema
     * @return warehouse schema
     */
    public WarehouseSchema getWarehouseSchemaByName(final Warehouse warehouse, final String name) {
        notNull(warehouse, "warehouse");
        notNull(warehouse.getId(), "warehouse.id");
        notEmpty(name, "name");
        final String uri = SCHEMA_TEMPLATE.expand(warehouse.getId(), name).toString();
        return getWarehouseSchemaByUri(uri);
    }

    /**
     * get warehouse schema by uri
     *
     * @param uri of schema
     * @return warehouse schema
     */
    public WarehouseSchema getWarehouseSchemaByUri(final String uri) {
        notEmpty(uri, "uri");
        try {
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(WarehouseSchema.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new WarehouseSchemaNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (Exception e) { 
            throw new GoodDataException("Unable to get Warehouse instance " + uri, e);
        }
    }

    /**
     * get default warehouse schema
     *
     * @param warehouse to get default schema for
     * @return default warehouse schema
     */
    public WarehouseSchema getDefaultWarehouseSchema(final Warehouse warehouse) {
        return getWarehouseSchemaByName(warehouse, DEFAULT_SCHEMA_NAME);
    }

}
