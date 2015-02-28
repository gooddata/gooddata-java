package com.gooddata.md;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.jadler.Jadler.onRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MetadataServiceIT extends AbstractGoodDataIT {

    private static final String USEDBY_URI = "/gdc/md/PROJECT_ID/usedby2";
    private static final String IDENTIFIERS_URI = "/gdc/md/PROJECT_ID/identifiers";
    private static final String OBJ_URI = "OBJ_URI";
    private static final String ID = "ID";
    private static final String TITLE = "TITLE";

    private Project project;

    @BeforeClass
    public void setUp() throws Exception {
        project = MAPPER.readValue(readResource("/project/project.json"), Project.class);
    }

    @Test
    public void testUsedBy() throws Exception {
        final List<Entry> entryList = new ArrayList<>();
        final Entry entry = new Entry(null, TITLE, null, null, null, null, false, null, null, null, null, false, false);
        entryList.add(entry);
        final InUseEntries entries = new InUseEntries(entryList);

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(USEDBY_URI)
                .respond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(entries));

        final Collection<Entry> result = gd.getMetadataService().usedBy(project, OBJ_URI, false, ReportDefinition.class);

        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(1));
        assertThat(result.iterator().next().getTitle(), is(TITLE));
    }

    @Test
    public void testFindIdentifierUris() throws IOException {
        final IdentifierAndUri identifierAndUri = new IdentifierAndUri(ID, OBJ_URI);

        final List<IdentifierAndUri> identifiersAndUris = new ArrayList<>();
        identifiersAndUris.add(identifierAndUri);
        IdentifiersAndUris response = new IdentifiersAndUris(identifiersAndUris);

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(IDENTIFIERS_URI)
                .respond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(response));

        final Collection<String> uris = gd.getMetadataService().findUris(project, Restriction.identifier(ID));
        assertThat(uris.size(), is(1));
        assertThat(uris.iterator().next(), is(OBJ_URI));
    }
}
