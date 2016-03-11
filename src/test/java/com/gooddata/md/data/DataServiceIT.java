package com.gooddata.md.data;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static org.testng.Assert.*;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeClass;

public class DataServiceIT extends AbstractGoodDataIT {

    private Project project;

    @BeforeClass
    public void setUp() throws Exception {
        project = MAPPER.readValue(readFromResource("/project/project.json"), Project.class);
    }

}