/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md.maintenance;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.md.maintenance.PartialMdArtifact;
import com.gooddata.sdk.model.md.maintenance.PartialMdExport;
import com.gooddata.sdk.model.md.maintenance.PartialMdExportToken;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.project.Project;
import org.junit.jupiter.api.BeforeEach;  
import org.junit.jupiter.api.Test;    
import org.junit.jupiter.api.extension.ExtendWith;  
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; 
import org.springframework.web.reactive.function.client.WebClient;  
import reactor.core.publisher.Mono;  

import static org.junit.jupiter.api.Assertions.assertThrows;  

@ExtendWith(MockitoExtension.class) 
public class ExportImportServiceTest {

    private static final String PROJECT_ID = "TEST_PROJ_ID";

    @Mock
    private Project project;

    @Mock
    private WebClient webClient;  

    private ExportImportService service;

    @BeforeEach  
    public void setUp() {
        service = new ExportImportService(webClient, new GoodDataSettings());  
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test
    void testCreatePartialExportError() {
 
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(PartialMdExport.URI))).thenReturn(uriSpec);
        when(uriSpec.bodyValue(any(PartialMdExport.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PartialMdArtifact.class))
                .thenReturn(Mono.error(new GoodDataRestException(400, "request", "Failed", "export", "error")));

        assertThrows(ExportImportException.class, () -> 
            service.partialExport(project, new PartialMdExport(false, false, "uri123"))
        );
    }

    @Test
    void testCreatePartialImportError() {

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(PartialMdExportToken.URI))).thenReturn(uriSpec);
        when(uriSpec.bodyValue(any(PartialMdExportToken.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(UriResponse.class))
                .thenReturn(Mono.error(new GoodDataRestException(400, "request", "Failed", "import", "error")));

        assertThrows(ExportImportException.class, () -> 
            service.partialImport(project, new PartialMdExportToken("TOKEN123"))
        );
    }
}
