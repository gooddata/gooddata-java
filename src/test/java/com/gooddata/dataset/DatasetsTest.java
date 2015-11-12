package com.gooddata.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatasetsTest {
    @Test
    public void deserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/datasets.json");
        final Datasets datasets = new ObjectMapper().readValue(stream, Datasets.class);
        assertThat(datasets, is(notNullValue()));

        final Collection<Dataset> links = datasets.getLinks();
        assertThat(links, is(notNullValue()));
        assertThat(links, hasSize(1));

        final Dataset link = links.iterator().next();
        assertThat(link, is(notNullValue()));
        assertThat(link.getIdentifier(), is("dataset.person"));
    }
}
