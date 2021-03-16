/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import java.io.Serializable;
import java.util.List;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * {@link AttributeFilterElements} represented by simple array.
 *
 * @deprecated It has the same semantic as {@link UriAttributeFilterElements}.
 * Preserved because of compatibility and will be removed in future API version.
 */
@Deprecated
public class SimpleAttributeFilterElements implements AttributeFilterElements, Serializable {

    private static final long serialVersionUID = -2935674265292888490L;

    private final List<String> elements;

    public SimpleAttributeFilterElements(final List<String> elements) {
        this.elements = notNull(elements, "elements");
    }

    @Override
    public List<String> getElements() {
        return elements;
    }


}
