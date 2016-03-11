package com.gooddata.md.data;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.project.Project;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

//TODO documentation
//TODO add missing IT and AT
public class DataService extends AbstractService {

    public DataService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public DatasetsInfo getDatasetsInfo(Project project) {
        notNull(project, "project");

        final URI uri = DatasetsInfo.URI_TEMPLATE.expand(project.getId());
        try {
            final DatasetsInfo result = restTemplate.getForObject(uri, DatasetsInfo.class);

            if (result != null) {
                return result;
            } else {
                throw new GoodDataException("empty response from API call");
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get datasets info from " + uri.toString());
        }
    }

    //TODO consider to use restrictions
    public DatasetsInfo.Dataset getDatasetInfo(Project project, String identifier) {
        notNull(project, "project");
        notEmpty(identifier, "identifier");

        final DatasetsInfo datasetsInfo = getDatasetsInfo(project);

        for (DatasetsInfo.Dataset dataset : datasetsInfo.getDatasets()) {
            if (identifier.equals(dataset.getIdentifier())) {
                return dataset;
            }
        }

        throw new DatasetInfoNotFoundException(identifier);
    }
}
