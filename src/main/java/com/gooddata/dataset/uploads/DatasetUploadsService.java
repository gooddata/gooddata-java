package com.gooddata.dataset.uploads;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.dataset.DatasetNotFoundException;
import com.gooddata.dataset.UploadStatus;
import com.gooddata.project.Project;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

//TODO documentation
//TODO add missing IT and AT
//TODO get data upload/uploads by uri?
//TODO
/**
 * Service providing information about dataset uploads
 */
public class DatasetUploadsService extends AbstractService {

    public DatasetUploadsService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Collection<DatasetUploads> getDatasets(Project project) {
        notNull(project, "project");

        final URI uri = DatasetsUploads.URI_TEMPLATE.expand(project.getId());
        try {
            final DatasetsUploads result = restTemplate.getForObject(uri, DatasetsUploads.class);

            if (result != null) {
                return result.getDatasets();
            } else {
                throw new GoodDataException("empty response from API call");
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get datasets uploads.", e);
        }
    }

    public DatasetUploads getDataset(Project project, String datasetId) {
        notNull(project, "project");
        notEmpty(datasetId, "datasetId");

        for (DatasetUploads datasetUploads : getDatasets(project)) {
            if (datasetId.equals(datasetUploads.getDatasetId())) {
                return datasetUploads;
            }
        }

        throw new DatasetNotFoundException(datasetId);
    }

    public Upload getUpload(String uri) {
        notEmpty(uri, "uri");

        try {
            return restTemplate.getForObject(uri, Upload.class);
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get upload.", e);
        }
    }

    public Collection<Upload> getUploads(String uri) {
        notEmpty(uri, "uri");

        try {
            final Uploads result = restTemplate.getForObject(uri, Uploads.class);

            if (result != null) {
                return result.getUploads();
            } else {
                throw new GoodDataException("empty response from API call");
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get uploads.", e);
        }
    }

    public Map<UploadStatus, Integer> getStatusesCounts(Project project) {
        notNull(project, "project");

        try {
            final StatusesCounts result = restTemplate.getForObject(StatusesCounts.URI, StatusesCounts.class, project.getId());

            if (result != null) {
                return result.getStatusesMap();
            } else {
                throw new GoodDataException("empty response from API call");
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get statuses counts.", e);
        }
    }
}
