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
import ee.jakarta.tck.data.framework.read.only._City;
import ee.jakarta.tck.data.framework.read.only._Country;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.inject.Inject;

/**
 * Tests for various Expressions used within Restrictions and Sorts
 * that are supplied to Repository methods.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class SortByExpressionTests {

    public static final Logger log =
            Logger.getLogger(SortByExpressionTests.class.getCanonicalName());

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
                .addClasses(SortByExpressionTests.class);
    }

    private final DatabaseType type = TestProperty.databaseType.getDatabaseType();


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

}
