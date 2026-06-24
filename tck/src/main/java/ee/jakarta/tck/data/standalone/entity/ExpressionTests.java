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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
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
import ee.jakarta.tck.data.framework.read.only._City;
import ee.jakarta.tck.data.framework.read.only._Country;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.constraint.AtLeast;
import jakarta.data.repository.By;
import jakarta.data.repository.Find;
import jakarta.data.repository.Is;
import jakarta.inject.Inject;

/**
 * Tests for various Expressions used within Restrictions and Sorts
 * that are supplied to Repository methods.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class ExpressionTests {

    public static final Logger log =
            Logger.getLogger(ExpressionTests.class.getCanonicalName());

    @Inject
    Countries countries;

    // Inject doesn't happen until after BeforeClass, so this is necessary
    // before each test
    @BeforeEach
    public void beforeEach() {
        CountryPopulator.get().populate(countries);
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
        try {
            found = countries.filter(_Country.code
                                             .left(1)
                                             .append(_Country.code.left(1))
                                             .equalTo(_Country.code));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConcat() &&
                type.capableOfLeft()) {
                throw x;
            } else {
                return;
            }
        }

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
        try {
            found = countries.filter(_Country.code.append("STRALIA")
                                                  .equalTo("AUSTRALIA"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConcat()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("AU: Australia"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the asDouble NumericCast expression to allow long-typed
            values to be divided as doubles so that the result can be
            compared against a double-typed value, supplied as a literal.
            """)
    public void testCastToDouble() {
        List<Country> found;
        try {
            found = countries.filter(_Country.population.asDouble()
                    .dividedBy(_Country.area.asDouble())
                    .lessThan(8.75));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfDivision() &&
                type.capableOfLessThan()) {
                throw x;
            } else {
                return;
            }
        }

        Function<Country, String> getPopulationDensityAndName = c ->
            BigDecimal.valueOf(c.getPopulation())
                    .divide(BigDecimal.valueOf(c.getArea()),
                            2, // decimal digits
                            RoundingMode.HALF_UP) +
                    ": " +
                    c.getName();

        assertEquals(List.of("0.03: Greenland",
                             "2.28: Mongolia",
                             "3.47: Namibia",
                             "3.52: Australia",
                             "3.66: Iceland",
                             "4.05: Guyana",
                             "4.19: Suriname",
                             "4.24: Libya",
                             "4.31: Canada",
                             "4.38: Botswana",
                             "5.05: Mauritania",
                             "7.57: Kazakhstan",
                             "8.56: Russia"),
                     found.stream()
                          .map(getPopulationDensityAndName)
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Use the left TextExpression to compare only the character
            at the beginning of a String attribute to a value.
            """)
    public void testLeft1() {
        List<Country> found;
        try {
            found = countries.filter(_Country.code.left(1)
                                                  .equalTo("R"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLeft()) {
                throw x;
            } else {
                return;
            }
        }

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
            if (type.capableOfLeft() &&
                type.capableOfConstraintsOnNonIdAttributes()) {
                throw x;
            } else {
                return;
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
        try {
            found = countries.filter(_Country.code.length()
                                                  .equalTo(2));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLength()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(CountryPopulator.EXPECTED_TOTAL,
                     found.size());
    }

    @Assertion(id = "829", strategy = """
            Use the length TextExpression to compare the length
            of a String attribute to a value.
            """)
    public void testLength5() {
        List<Country> found;
        try {
            found = countries.filter(_Country.name.length()
                                                  .equalTo(5));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLeft() &&
                type.capableOfConstraintsOnNonIdAttributes()) {
                throw x;
            } else {
                return;
            }
        }


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
            if (type.capableOfLower()) {
                throw x;
            } else {
                return;
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
            if (type.capableOfConcat() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfRight()) {
                throw x;
            } else {
                return;
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
        try {
            found = countries.filter(_Country.code.prepend("Jakarta ")
                                                  .equalTo("Jakarta EE"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConcat()) {
                throw x;
            } else {
                return;
            }
        }

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
        try {
            found = countries.filter(_Country.code.right(1)
                                                  .equalTo("J"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfRight()) {
                throw x;
            } else {
                return;
            }
        }

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
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfRight()) {
                throw x;
            } else {
                return;
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
            Supply multiple expressions by which to sort.
            """)
    public void testSortByMultipleExpressions() {
        Sort<Country> secondCharAsc = _Country.code.right(1).asc();
        Sort<Country> firstCharDesc = _Country.code.left(1).desc();

        List<Country> found;
        try {
            found = countries.byCountryCodeUpTo("CZ",
                                                Order.by(secondCharAsc,
                                                         firstCharDesc));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLeft() &&
                type.capableOfLessThanEqual() &&
                type.capableOfMultipleSort() &&
                type.capableOfRight()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("CA: Canada",
                             "BA: Bosnia and Herzegovina",
                             "BB: Barbados",
                             "CD: Congo",
                             "BD: Bangladesh",
                             "AD: Andorra",
                             "BE: Belgium",
                             "AE: United Arab Emirates",
                             "CF: Central African Republic",
                             "BF: Burkina Faso",
                             "AF: Afghanistan",
                             "BG: Bulgaria",
                             "AG: Antigua and Barbuda",
                             "CH: Switzerland",
                             "BH: Bahrain",
                             "CI: Ivory Coast",
                             "BI: Burundi",
                             "BJ: Benin",
                             "CL: Chile",
                             "AL: Albania",
                             "CM: Cameroon",
                             "BM: Bermuda",
                             "AM: Armenia",
                             "CN: China",
                             "BN: Brunei",
                             "CO: Colombia",
                             "BO: Bolivia",
                             "AO: Angola",
                             "CR: Costa Rica",
                             "BR: Brazil",
                             "AR: Argentina",
                             "BS: Bahamas",
                             "AS: American Samoa",
                             "BT: Bhutan",
                             "AT: Austria",
                             "CU: Cuba",
                             "AU: Australia",
                             "CV: Cape Verde",
                             "BW: Botswana",
                             "AW: Aruba",
                             "CY: Cyprus",
                             "BY: Belarus",
                             "CZ: Czech Republic",
                             "BZ: Belize",
                             "AZ: Azerbaijan"),
                found.stream()
                     .map(c -> c.getCode() + ": " + c.getName())
                     .toList());
    }

    @Assertion(id = "829", strategy = """
            Sort based on a navigable expression.
            """)
    public void testSortByNavigableExpression() {

        Sort<Country> capitalNameAsc = //
                _Country.capital.navigate(_City.name).asc();

        List<Country> found;
        try {
            found = countries.findLargest(100000000L,
                                          Order.by(capitalNameAsc));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("Abuja, Nigeria",
                             "Addis Ababa, Ethiopia",
                             "Beijing, China",
                             "Brasilia, Brazil",
                             "Brazzaville, Congo",
                             "Cairo, Egypt",
                             "Dhaka, Bangladesh",
                             "Hanoi, Vietnam",
                             "Islamabad, Pakistan",
                             "Jakarta, Indonesia",
                             "Manila, Philippines",
                             "Mexico City, Mexico",
                             "Moscow, Russia",
                             "New Delhi, India",
                             "Tokyo, Japan",
                             "Washington, D.C., United States of America"),
                found.stream()
                     .map(c -> c.getCapital().getName() + ", " + c.getName())
                     .toList());
    }

    @Assertion(id = "829", strategy = """
            Supply a single expression by which to sort.
            """)
    public void testSortBySingleExpression() {

        Sort<Country> populationDensityDesc = //
                _Country.population.dividedBy(_Country.area).desc();

        List<Country> found;
        try {
            found = countries.findLargest(100000000L,
                                          Order.by(populationDensityDesc));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfDivision() &&
                type.capableOfGreaterThanEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("Bangladesh: 1307",
                             "India: 477",
                             "Philippines: 402",
                             "Vietnam: 344",
                             "Japan: 336",
                             "Pakistan: 333",
                             "Nigeria: 266",
                             "Indonesia: 156",
                             "China: 150",
                             "Egypt: 113",
                             "Ethiopia: 110",
                             "Mexico: 67",
                             "Congo: 52",
                             "United States of America: 36",
                             "Brazil: 26",
                             "Russia: 8"),
                found.stream()
                     .map(c -> c.getName() + ": " +
                               (c.getPopulation() / c.getArea()))
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
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfUpper()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("UZ: Uzbekistan"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

}
