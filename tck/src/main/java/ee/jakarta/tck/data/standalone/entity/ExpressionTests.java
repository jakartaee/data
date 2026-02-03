/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.jakarta.tck.data.standalone.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.logging.Logger;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.read.only.Countries;
import ee.jakarta.tck.data.framework.read.only.Country;
import ee.jakarta.tck.data.framework.read.only.CountryPopulator;
import ee.jakarta.tck.data.framework.read.only._Country;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.inject.Inject;

/**
 * Tests for various Expressions used within Restrictions that are
 * supplied to Repository methods.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class ExpressionTests {

    public static final Logger log =
            Logger.getLogger(ExpressionTests.class.getCanonicalName());

    @Inject
    Countries countries;

    private boolean initialized = false;

    // Inject doesn't happen until after BeforeClass, so this is necessary
    // before each test
    @BeforeEach
    public void beforeEach() {
        assertNotNull(countries);
        if (!initialized) {
            CountryPopulator.get().populate(countries);
            initialized = true;
        }
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(ExpressionTests.class);
    }

    private final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Assertion(id = "829", strategy = """
            Use the append TextExpression to allow comparison against
            a String attribute apppending additional characters,
            supplied as another TextExpression.
            """)
    public void testAppendExpression() {
        List<Country> found;
        found = countries.filter(_Country.code.left(1)
                                              .append(_Country.code.left(1))
                                              .equalTo(_Country.code));

        assertEquals(List.of("BB: Barbados",
                             "EE: Estonia",
                             "GG: Guernsey",
                             "MM: Burma",
                             "SS: South Sudan",
                             "TT: Trinidad and Tobago"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the append TextExpression to allow comparison against
            a String attribute appending additional characters,
            supplied as a literal value.
            """)
    public void testAppendValue() {
        List<Country> found;
        found = countries.filter(_Country.code.append("STRALIA")
                                              .equalTo("AUSTRALIA"));

        assertEquals(List.of("AU: Australia"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the left TextExpression to compare only the character
            at the beginning of a String attribute to a value.
            """)
    public void testLeft1() {
        List<Country> found;
        found = countries.filter(_Country.code.left(1)
                                              .equalTo("R"));

        assertEquals(List.of("RO: Romania",
                             "RS: Serbia",
                             "RU: Russia",
                             "RW: Rwanda"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the left TextExpression to compare only the characters
            at the beginning of a String attribute to a value.
            """)
    public void testLeft3() {
        List<Country> found;

        try {
            found = countries.filter(_Country.name.left(3)
                                                  .equalTo("Gre"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of
                //  restrictions on attributes that are not the Id.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of("GD: Grenada",
                             "GL: Greenland",
                             "GR: Greece"),
        found.stream()
             .map(c -> c.getCode() + ": " + c.getName())
             .sorted()
             .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the length TextExpression to compare the length
            of a String attribute, which is also the unique identifier,
            to a value.
            """)
    public void testLength2() {
        List<Country> found;
        found = countries.filter(_Country.code.length()
                                              .equalTo(2));

        assertEquals(CountryPopulator.EXPECTED_TOTAL,
                     found.size());
    }

    @Assertion(id = "829", strategy = """
            Use the length TextExpression to compare the length
            of a String attribute to a value.
            """)
    public void testLength5() {
        List<Country> found;
        found = countries.filter(_Country.name.length()
                                              .equalTo(5));

        assertEquals(List.of("AW: Aruba",
                             "BJ: Benin",
                             "CD: Congo",
                             "CL: Chile",
                             "CN: China",
                             "EG: Egypt",
                             "ES: Spain",
                             "GA: Gabon",
                             "GH: Ghana",
                             "HT: Haiti",
                             "IN: India",
                             "IT: Italy",
                             "JP: Japan",
                             "KE: Kenya",
                             "LY: Libya",
                             "MM: Burma",
                             "MO: Macau",
                             "MT: Malta",
                             "NE: Niger",
                             "NP: Nepal",
                             "NR: Nauru",
                             "QA: Qatar",
                             "SD: Sudan",
                             "SY: Syria",
                             "TO: Tonga",
                             "WS: Samoa",
                             "YE: Yemen"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the lower TextExpression to consider characters of a
            String attribute as lower case when compared to a value.
            """)
    public void testLower() {
        List<Country> found;
        try {
            found = countries.filter(_Country.code.lower()
                                                  .equalTo("be"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // TODO Otavio - which categories of NoSQL database must be excluded?
                // ??? might not be capable of LOWER case comparison.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of("BE: Belgium"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the prepend TextExpression to allow comparison against
            a String attribute prepending additional characters,
            supplied as another TextExpression.
            """)
    public void testPrependExpression() {
        List<Country> found;
        try {
            found = countries.filter(
                    _Country.name.prepend(_Country.code.right(1))
                                 .in("ACanada", // code CA ends in A
                                      "BLebanon", // code LB ends in B
                                      "CMonaco", // code MC ends in C
                                      "DBangladesh", // code BD ends in D
                                      "EEgypt", // code EG doesn't end in E
                                      "FFiji")); // code FJ doesn't end in F
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of
                //  restrictions on attributes that are not the Id.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of("BD: Bangladesh",
                             "CA: Canada",
                             "LB: Lebanon",
                             "MC: Monaco"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the prepend TextExpression to allow comparison against
            a String attribute prepending additional characters,
            supplied as a literal.
            """)
    public void testPrependValue() {
        List<Country> found;
        found = countries.filter(_Country.code.prepend("Jakarta ")
                                              .equalTo("Jakarta EE"));

        assertEquals(List.of("EE: Estonia"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the right TextExpression to compare only the character
            at the end of a String attribute to a value.
            """)
    public void testRight1() {
        List<Country> found;
        found = countries.filter(_Country.code.right(1)
                                              .equalTo("J"));

        assertEquals(List.of("BJ: Benin",
                             "DJ: Djibouti",
                             "FJ: Fiji",
                             "TJ: Tajikistan"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());

    }

    @Assertion(id = "829", strategy = """
            Use the right TextExpression to compare only the characters
            at the end of a String attribute to a value.
            """)
    public void testRight3() {
        List<Country> found;

        try {
            found = countries.filter(_Country.name.right(3)
                                                  .equalTo("dan"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of
                //  restrictions on attributes that are not the Id.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of("JO: Jordan",
                             "SD: Sudan",
                             "SS: South Sudan"),
        found.stream()
             .map(c -> c.getCode() + ": " + c.getName())
             .sorted()
             .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the upper TextExpression to consider characters of a
            String attribute as upper case when compared to a value.
            """)
    public void testUpper() {
        List<Country> found;
        try {
            found = countries.filter(_Country.name.upper()
                                                  .equalTo("UZBEKISTAN"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // TODO Otavio - which categories of NoSQL database must be excluded?
                // ??? might not be capable of UPPER case comparison.
                // Column and Key-Value databases might not be capable of
                //  restrictions on attributes that are not the Id.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of("UZ: Uzbekistan"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

}
