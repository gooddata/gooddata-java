package com.gooddata.project;

import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated use {@link com.gooddata.featureflag.FeatureFlagService} and {@link com.gooddata.featureflag.ProjectFeatureFlags} instead
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("featureFlags")
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class ProjectFeatureFlags {

    public static final String FEATURE_FLAGS_URI = Project.URI + "/projectFeatureFlags";
    public static final UriTemplate FEATURE_FLAGS_TEMPLATE = new UriTemplate(FEATURE_FLAGS_URI);

    private final List<ProjectFeatureFlag> items = new ArrayList<>();

    @JsonCreator
    ProjectFeatureFlags(@JsonProperty("items") List<ProjectFeatureFlag> items) {
        if (!isEmpty(items)) {
            this.items.addAll(items);
        }
    }

    public List<ProjectFeatureFlag> getItems() {
        return items;
    }
}
