/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier
import com.gooddata.sdk.model.executeafm.ObjQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import spock.lang.Specification

class ObjIdentifierUtilitiesTest extends Specification {


    def "should throw when required parameter is null"() {
        when:
        ObjIdentifierUtilities.copyIfNecessary(objectToCopy, qualifier, copyFactory, converter)

        then:
        thrown(IllegalArgumentException)

        where:
        objectToCopy | qualifier                        | copyFactory             | converter
        null         | new IdentifierObjQualifier("id") | { uri -> "new string" } | { id -> new UriObjQualifier("/uri") }
        "string"     | null                             | { uri -> "new string" } | { id -> new UriObjQualifier("/uri") }
        "string"     | new IdentifierObjQualifier("id") | null                    | { id -> new UriObjQualifier("/uri") }
        "string"     | new IdentifierObjQualifier("id") | { uri -> "new string" } | null
    }

    def "should throw when uri conversion failed"() {
        given:
        SomeClass original = new SomeClass(new IdentifierObjQualifier("id"))
        def copyFactory = { uriQualifier -> new SomeClass(uriQualifier) }
        def qualifiersConversionMap = [
                (new IdentifierObjQualifier("another-id")): new UriObjQualifier("/uri")
        ]
        ObjQualifierConverter qualifierConverter = { identifierQualifier ->
            return Optional.ofNullable(qualifiersConversionMap.get(identifierQualifier))
        }

        when:
        ObjIdentifierUtilities.copyIfNecessary(original, original.qualifier, copyFactory, qualifierConverter)

        then:
        thrown(IllegalArgumentException)
    }

    def "should return copied object with result of the qualifier result"() {
        given:
        SomeClass original = new SomeClass(new IdentifierObjQualifier("id"))
        def copyFactory = { uriQualifier -> new SomeClass(uriQualifier) }
        def qualifiersConversionMap = [
                (new IdentifierObjQualifier("id")): new UriObjQualifier("/uri")
        ]
        ObjQualifierConverter qualifierConverter = { identifierQualifier ->
            return Optional.ofNullable(qualifiersConversionMap.get(identifierQualifier))
        }

        when:
        SomeClass copy = ObjIdentifierUtilities.copyIfNecessary(original, original.qualifier, copyFactory, qualifierConverter)

        then:
        copy == new SomeClass(new UriObjQualifier("/uri"))
    }

    def "should return the original object when it already uses the uri qualifier"() {
        given:
        SomeClass original = new SomeClass(new UriObjQualifier("/uri"))
        def copyFactory = { uriQualifier -> new SomeClass(uriQualifier) }
        def qualifiersConversionMap = [
                (new IdentifierObjQualifier("id")): new UriObjQualifier("/another-uri")
        ]
        ObjQualifierConverter qualifierConverter = { identifierQualifier ->
            return Optional.ofNullable(qualifiersConversionMap.get(identifierQualifier))
        }

        when:
        SomeClass copy = ObjIdentifierUtilities.copyIfNecessary(original, original.qualifier, copyFactory, qualifierConverter)

        then:
        original == copy
    }

    private class SomeClass {

        private ObjQualifier qualifier

        SomeClass(ObjQualifier qualifier) {
            this.qualifier = qualifier
        }

        boolean equals(final o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false
            SomeClass someClass = (SomeClass) o
            if (qualifier != someClass.qualifier) return false
            return true
        }

        int hashCode() {
            return (qualifier != null ? qualifier.hashCode() : 0)
        }
    }
}
