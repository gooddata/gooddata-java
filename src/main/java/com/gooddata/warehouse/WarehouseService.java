package com.gooddata.warehouse;

import static com.gooddata.Validate.notEmpty;
import static com.gooddata.Validate.notNull;
import static java.util.Collections.emptyList;

import com.gooddata.AbstractPollHandler;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;

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

        return new FutureResult<>(this, new AbstractPollHandler<WarehouseTask,Warehouse>(task.getPollLink(), WarehouseTask.class, Warehouse.class) {

            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                return HttpStatus.CREATED.equals(response.getStatusCode());
            }

            @Override
            protected void onFinish() {
                if (!getResult().isEnabled()) {
                    throw new GoodDataException("Created warehouse, uri: " + getResult().getSelfLink() + " is not enabled!");
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
        });
    }

    /**
     * Delete Warehouse.
     * @param warehouse to delete
     */
    public void removeWarehouse(Warehouse warehouse) {
        notNull(warehouse, "warehouse");
        try {
            restTemplate.delete(warehouse.getSelfLink());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to delete Warehouse, uri: " + warehouse.getSelfLink(), e);
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
     *
     * @return collection of instances or empty list
     */
    public Collection<Warehouse> listWarehouses() {
        try {
            final Warehouses result = restTemplate.getForObject(Warehouses.URI, Warehouses.class);
            if (result == null || result.getItems() == null) {
                return emptyList();
            }
            return setWarehouseConnection(result.getItems());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list Warehouses", e);
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
            restTemplate.put(toUpdate.getSelfLink(), toUpdate);
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to update Warehouse, uri: " + toUpdate.getSelfLink());
        }

        return getWarehouseByUri(toUpdate.getSelfLink());
    }

    private Warehouse setWarehouseConnection(Warehouse warehouse) {
        notNull(warehouse, "warehouse");
        warehouse.setWarehouseHost(warehouseHost);
        warehouse.setWarehousePort(warehousePort);
        return warehouse;
    }

    private Collection<Warehouse> setWarehouseConnection(Collection<Warehouse> warehouses) {
        notNull(warehouses, "warehouses");
        for (Warehouse warehouse : warehouses) {
            setWarehouseConnection(warehouse);
        }
        return warehouses;
    }
}
