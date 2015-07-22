package com.gooddata.warehouse;

import com.gooddata.AbstractPollHandler;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.PollResult;
import com.gooddata.collections.Page;
import com.gooddata.collections.PageableList;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * Provide access to warehouse API - create, update, list and delete warehouses.
 */
public class WarehouseService extends AbstractService {

    private final String warehouseHost;
    private final int warehousePort;

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     *
     * @param restTemplate RESTful HTTP Spring template
     * @param warehouseHost host to connect warehouses
     * @param warehousePort port to connect warehouses
     */
    public WarehouseService(RestTemplate restTemplate, String warehouseHost, int warehousePort) {
        super(restTemplate);
        this.warehouseHost = notNull(warehouseHost, "warehouseHost");
        this.warehousePort = warehousePort;
    }

    /**
     * Create new warehouse.
     *
     * @param warehouse warehouse to create
     *
     * @return created warehouse
     */
    public FutureResult<Warehouse> createWarehouse(Warehouse warehouse) {
        notNull(warehouse, "warehouse");
        final WarehouseTask task;
        try {
            task = restTemplate.postForObject(Warehouses.URI, warehouse, WarehouseTask.class);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create Warehouse", e);
        }

        if (task == null) {
            throw new GoodDataException("Empty response when Warehouse POSTed to API");
        }

        return new PollResult<>(this, new AbstractPollHandler<WarehouseTask,Warehouse>(task.getPollLink(), WarehouseTask.class, Warehouse.class) {

            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                return HttpStatus.CREATED.equals(response.getStatusCode());
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
                    final Warehouse warehouse = restTemplate.getForObject(pollResult.getWarehouseLink(), Warehouse.class);
                    setResult(setWarehouseConnection(warehouse));
                } catch (GoodDataException | RestClientException e) {
                    throw new GoodDataException("Warehouse creation finished, but can't get created warehouse, uri: "
                            + pollResult.getWarehouseLink(), e);
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
    public void removeWarehouse(Warehouse warehouse) {
        notNull(warehouse, "warehouse");
        try {
            restTemplate.delete(warehouse.getUri());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to delete Warehouse, uri: " + warehouse.getUri(), e);
        }
    }

    /**
     * Get Warehouse identified by given uri.
     * @param uri warehouse uri
     * @return Warehouse
     * @throws com.gooddata.GoodDataException when Warehouse can't be accessed
     */
    public Warehouse getWarehouseByUri(String uri) {
        notEmpty(uri, "uri");
        try {
            return setWarehouseConnection(restTemplate.getForObject(uri, Warehouse.class));
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new WarehouseNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get Warehouse instance " + uri, e);
        }
    }

    /**
     * Get Warehouse identified by given id.
     * @param id warehouse id
     * @return Warehouse
     * @throws com.gooddata.GoodDataException when Warehouse can't be accessed
     */
    public Warehouse getWarehouseById(String id) {
        notEmpty(id, "id");
        return getWarehouseByUri(uriFromId(id));
    }

    private static String uriFromId(String id) {
        return Warehouse.TEMPLATE.expand(id).toString();
    }

    /**
     * Lists Warehouses. Returns empty list in case there are no warehouses.
     * Returns only first page if there's more instances than page limit. Use {@link #listWarehouses(Page)} to get other pages.
     *
     * @return first page of list of warehouse instances or empty list
     */
    public PageableList<Warehouse> listWarehouses() {
        return listWarehouses(URI.create(Warehouses.URI));
    }

    /**
     * Lists Warehouses. Returns empty list in case there are no warehouses.
     * Returns requested page (by page limit and offset). Use {@link #listWarehouses()} to get first page with default setting.
     *
     * @return requested page of list of instances or empty list
     */
    public PageableList<Warehouse> listWarehouses(Page page) {
        notNull(page, "page");
        return listWarehouses(page.getPageUri(UriComponentsBuilder.fromUriString(Warehouses.URI)));
    }

    private PageableList<Warehouse> listWarehouses(final URI uri) {
        try {
            final Warehouses result = restTemplate.getForObject(uri, Warehouses.class);
            if (result == null) {
                return new PageableList<>();
            }
            return setWarehouseConnection(result);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list Warehouses", e);
        }
    }

    public PageableList<WarehouseUser> listWarehouseUsers(final Warehouse warehouse, final Page page) {
        notNull(warehouse, "warehouse");
        notNull(warehouse.getId(), "warehouse.id");
        notNull(page, "page");
        try {
            final WarehouseUsers result = restTemplate.getForObject(WarehouseUsers.URI, WarehouseUsers.class, warehouse.getId());
            return result != null ? result : new PageableList<WarehouseUser>();
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list users of warehouse " + warehouse.getId(), e);
        }
    }

    /**
     * Updates given Warehouse.
     *
     * @param toUpdate warehouse to be updated
     * @return updated warehouse
     * @throws com.gooddata.GoodDataException when update fails
     */
    public Warehouse updateWarehouse(Warehouse toUpdate) {
        notNull(toUpdate, "warehouse to update");
        try {
            restTemplate.put(toUpdate.getUri(), toUpdate);
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to update Warehouse, uri: " + toUpdate.getUri());
        }

        return getWarehouseByUri(toUpdate.getUri());
    }

    private Warehouse setWarehouseConnection(Warehouse warehouse) {
        notNull(warehouse, "warehouse");
        warehouse.setWarehouseHost(warehouseHost);
        warehouse.setWarehousePort(warehousePort);
        return warehouse;
    }

    private PageableList<Warehouse> setWarehouseConnection(PageableList<Warehouse> warehouses) {
        notNull(warehouses, "warehouses");
        for (Warehouse warehouse : warehouses) {
            setWarehouseConnection(warehouse);
        }
        return warehouses;
    }
}
