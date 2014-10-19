/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataload.processes;

import java.util.List;

/**
 * @deprecated This class has been replaced by {@link com.gooddata.dataload.processes.DataloadProcesses}
 */
@Deprecated
public class Processes extends DataloadProcesses {

    private Processes(List<DataloadProcess> items) {
        super(items);
    }
}
