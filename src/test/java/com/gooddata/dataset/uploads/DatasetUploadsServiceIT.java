package com.gooddata.dataset.uploads;

import static com.gooddata.util.ResourceUtils.readFromResource;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeClass;

public class DatasetUploadsServiceIT extends AbstractGoodDataIT {

    private Project project;

    @BeforeClass
    public void setUp() throws Exception {
        project = MAPPER.readValue(readFromResource("/project/project.json"), Project.class);
    }

}