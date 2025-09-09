/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.project

import com.gooddata.sdk.common.util.ResourceUtils
import spock.lang.Specification

class ProjectsTest extends Specification {

    def "should deserialize"() {
        when:
        def projects = ResourceUtils.readObjectFromResource("/project/projects.json", Projects)

        then:
        projects.projects.size() == 1
        projects.projects.first().title == "Project Name"
        projects.paging
    }
}

