package com.gooddata;

import com.gooddata.collections.PageableList;
import com.gooddata.warehouse.Warehouse;
import com.gooddata.warehouse.Warehouses;
import net.jadler.Jadler;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertTrue;

public class GoodDataTest {

    private static final String HEADER_NAME = "header-name";
    private static final String HEADER_VALUE = "header-value";
    private static final String LOGIN = "frank@gooddata.com";
    private static final String PASS = "super-secret-password";

    @BeforeMethod
    public void setUp() throws Exception {
        Jadler.initJadler().that().respondsWithDefaultContentType("application/json");
    }

    @AfterMethod
    public void tearDown() {
        closeJadler();
    }

    @Test
    public void httpHeadersAreSetProperly() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingHeader(HEADER_NAME, hasItem(HEADER_VALUE))
                .havingPathEqualTo(Warehouses.URI)
            .respond()
                .withBody(readFromResource("/warehouse/warehouses.json"))
                .withStatus(200);

        final GoodDataSettings settings = new GoodDataSettings();
        settings.setHttpHeader(HEADER_NAME, HEADER_VALUE);
        final GoodData goodData = new GoodData("localhost", LOGIN, PASS, port(), "http", settings){
            @Override
            protected HttpClient createHttpClient(final String login, final String password, final String hostname,
                                                  final int port, final String protocol,
                                                  final HttpClientBuilder builder) {
                return builder.build();
            }
        };

        final PageableList<Warehouse> warehouses = goodData.getWarehouseService().listWarehouses();

        assertTrue(warehouses.size() > 0);
    }
}
