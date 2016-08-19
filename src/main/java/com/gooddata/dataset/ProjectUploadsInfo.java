package com.gooddata.dataset;

import static com.gooddata.util.Validate.notEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains information about dataset uploads for every single dataset in the project.
 * For more about dataset uploads information, see {@link DatasetUploadsInfo}.
 * Deserialization only.
 */
@JsonTypeName("dataSetsInfo")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectUploadsInfo {

    public static final String URI = "/gdc/md/{projectId}/data/sets";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(URI);

    private final Map<String, DatasetUploadsInfo> datasets = new HashMap<>();

    //for deserialization and testing purposes only
    ProjectUploadsInfo(@JsonProperty("sets") Collection<DatasetUploadsInfo> datasets) {
        if (datasets != null) {
            for (DatasetUploadsInfo dataset : datasets) {
                this.datasets.put(dataset.getDatasetId(), dataset);
            }
        }
    }

    /**
     * Returns dataset uploads information for a dataset with the given ID.
     *
     * @param datasetId dataset identifier
     * @return {@link DatasetUploadsInfo} object
     */
    public DatasetUploadsInfo getDatasetUploadsInfo(String datasetId) {
        notEmpty(datasetId, "datasetId");

        if (hasDataset(datasetId)) {
            return datasets.get(datasetId);
        } else {
            throw new DatasetNotFoundException(datasetId);
        }
    }

    /**
     * Returns if dataset with the given identifier exists in the project dataset uploads.
     *
     * @param datasetId dataset identifier
     * @return {@code true} if the dataset exists, {@code false} otherwise
     */
    public boolean hasDataset(String datasetId) {
        notEmpty(datasetId, "datasetId");

        return datasets.containsKey(datasetId);
    }
}
