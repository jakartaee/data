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

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import ee.jakarta.tck.data.framework.read.only.Region;
import ee.jakarta.tck.data.framework.read.only._Country;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.expression.TemporalExpression;
import jakarta.data.restrict.Restrict;
import jakarta.data.restrict.Restriction;
import jakarta.inject.Inject;

/**
 * Tests for repository methods with a Restriction parameter.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class RestrictionTests {

    public static final Logger log =
            Logger.getLogger(RestrictionTests.class.getCanonicalName());

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
                .addClasses(RestrictionTests.class);
    }

    private final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Assertion(id = "829", strategy = """
            Supply a composite ALL restriction to a repository
            find method, where the ALL restriction includes only
            a single restrictions that results must match.
            """)
    public void testAllOf1Restriction() {
        List<Country> found =
                countries.filter(Restrict.all(_Country.code.equalTo("MN")));

        assertEquals(1, found.size());

        Country country = found.get(0);

        assertEquals("MN",
                     country.getCode());
        assertEquals("Mongolia",
                     country.getName());
        assertEquals(Region.ASIA,
                     country.getRegion());
        assertEquals(1553556L,
                     country.getArea());
        assertEquals(3543677L,
                     country.getPopulation());
        assertEquals(56474000000L,
                     country.getGdp());
        assertEquals(38176424000L,
                     country.getDebt());
        assertEquals(null,
                     country.getDaylightTimeBegins());
        assertEquals(null,
                     country.getDaylightTimeEnds());
        assertEquals("Ulaanbaatar",
                     country.getCapital().getName());
        assertEquals(1466125,
                     country.getCapital().getPopulation());
    }

    @Assertion(id = "829", strategy = """
            Supply a composite ALL restriction to a repository
            find method, where the ALL restriction combines two
            equality restrictions such that results must match
            both of the restrictions.
            """)
    public void testAllOf2Restrictions() {
        List<Country> found;
        try {
            found = countries.filter(Restrict.all(_Country.code.greaterThan("JM"),
                                                  _Country.code.lessThan("JR")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfGreaterThan() &&
                type.capableOfLessThan()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("JO: Jordan",
                             "JP: Japan"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Supply a composite ALL restriction to a repository
            find method, where the ALL restriction combines three
            equality restrictions such that results must match
            all three of the restrictions.
            """)
    public void testAllOf3Restrictions() {
        List<Country> found;
        try {
            found = countries.filter(Restrict.all(_Country.code.notNull(),
                                                  _Country.code.in("FI", "FR", "GR"),
                                                  _Country.code.notEqualTo("FR")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfNotEqual() &&
                type.capableOfNotNull()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("FI: Finland",
                             "GR: Greece"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Supply a composite ALL restriction to a repository
            find method, where the ALL restriction combines two
            ANY restrictions that are nested under it, such that
            results must satisfy both of the ANY restrictions by
            satisfying at least one of their respective restrictions.
            """)
    public void testAllWithNestedAnyRestrictions() {
        Restriction<Country> restriction =
                Restrict.all(Restrict.any(_Country.code.equalTo("IS"),
                                          _Country.code.in("DO", "GD", "JM", "HT")),
                             Restrict.any(_Country.code.notEqualTo("JM"),
                                          _Country.code.notIn("GD", "JM", "IS")));

        List<Country> found;
        try {
            found = countries.filter(restriction);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfNotEqual() &&
                type.capableOfNotIn() &&
                type.capableOfOr() &&
                type.capableOfParentheses()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("DO: Dominican Republic",
                             "GD: Grenada",
                             "HT: Haiti",
                             "IS: Iceland"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Supply a composite ANY restriction to a repository
            find method, where the ANY restriction includes only
            a single restrictions that results must match.
            """)
    public void testAnyOf1Restriction() {
        List<Country> found =
                countries.filter(Restrict.any(_Country.code.equalTo("KE")));

        assertEquals(1, found.size());

        Country country = found.get(0);

        assertEquals("KE",
                     country.getCode());
        assertEquals("Kenya",
                     country.getName());
        assertEquals(Region.AFRICA,
                     country.getRegion());
        assertEquals(569140L,
                     country.getArea());
        assertEquals(55751717L,
                     country.getPopulation());
        assertEquals(314491000000L,
                     country.getGdp());
        assertEquals(170454122000L,
                     country.getDebt());
        assertEquals(null,
                     country.getDaylightTimeBegins());
        assertEquals(null,
                     country.getDaylightTimeEnds());
        assertEquals("Nairobi",
                     country.getCapital().getName());
        assertEquals(4397073,
                     country.getCapital().getPopulation());
    }

    @Assertion(id = "829", strategy = """
            Supply a composite ANY restriction to a repository
            find method, where the ANY restriction combines two
            equality restrictions such that results can match
            either of the two restrictions.
            """)
    public void testAnyOf2Restrictions() {
        List<Country> found;
        try {
            found = countries.filter(Restrict.any(_Country.code.equalTo("CO"),
                                                  _Country.code.equalTo("MY")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(2, found.size());

        Country colombia;
        Country malaysia;
        if ("CO".equals(found.get(0).getCode())) {
            colombia = found.get(0);
            malaysia = found.get(1);
        } else {
            malaysia = found.get(0);
            colombia = found.get(1);
        }

        assertEquals("CO",
                     colombia.getCode());
        assertEquals("Colombia",
                     colombia.getName());
        assertEquals(Region.SOUTH_AMERICA,
                     colombia.getRegion());
        assertEquals(1038700L,
                     colombia.getArea());
        assertEquals(49842298L,
                     colombia.getPopulation());
        assertEquals(978007000000L,
                     colombia.getGdp());
        assertEquals(703187033000L,
                     colombia.getDebt());
        assertEquals(null,
                     colombia.getDaylightTimeBegins());
        assertEquals(null,
                     colombia.getDaylightTimeEnds());
        assertEquals("Bogota",
                     colombia.getCapital().getName());
        assertEquals(7181469,
                     colombia.getCapital().getPopulation());

        assertEquals("MY",
                     malaysia.getCode());
        assertEquals("Malaysia",
                     malaysia.getName());
        assertEquals(Region.ASIA,
                     malaysia.getRegion());
        assertEquals(328657L,
                     malaysia.getArea());
        assertEquals(34905275L,
                     malaysia.getPopulation());
        assertEquals(1153000000000L,
                     malaysia.getGdp());
        assertEquals(741379000000L,
                     malaysia.getDebt());
        assertEquals(null,
                     malaysia.getDaylightTimeBegins());
        assertEquals(null,
                     malaysia.getDaylightTimeEnds());
        assertEquals("Kuala Lumpur",
                     malaysia.getCapital().getName());
        assertEquals(1782500,
                     malaysia.getCapital().getPopulation());
    }

    @Assertion(id = "829", strategy = """
            Supply a composite ANY restriction to a repository
            find method, where the ANY restriction combines four
            equality restrictions such that results can match
            any of the four restrictions.
            """)
    public void testAnyOf4Restrictions() {
        List<Country> found;
        try {
            found = countries.filter(Restrict.any(_Country.code.equalTo("EC"),
                                                  _Country.code.equalTo("ET"),
                                                  _Country.code.equalTo("EH"), // no match
                                                  _Country.code.equalTo("TL")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("EC: Ecuador",
                             "ET: Ethiopia",
                             "TL: East Timor"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Supply a composite ANY restriction to a repository
            find method, where the ANY restriction combines three
            ALL restrictions that are nested under it, such that
            results must satisy both of the restrictions under ALL
            for at least one of the restrictions under ANY.
            """)
    public void testAnyWithNestedAllRestrictions() {
        Restriction<Country> restriction =
                Restrict.any(Restrict.all(_Country.name.notNull(),
                                          _Country.name.between("Tunisia", "Turkmenistan")),
                             Restrict.all(_Country.region.equalTo(Region.EUROPE),
                                          _Country.population.greaterThanEqual(5000000L),
                                          _Country.population.lessThan(10000000L)),
                             Restrict.all(_Country.region.in(Region.AFRICA, Region.ASIA),
                                          _Country.area.greaterThan(2000000L)));

        List<Country> found;
        try {
            found = countries.filter(restriction);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThan() &&
                type.capableOfGreaterThanEqual() &&
                type.capableOfLessThan() &&
                type.capableOfNotNull() &&
                type.capableOfOr() &&
                type.capableOfParentheses()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("Algeria",
                             "Austria",
                             "Belarus",
                             "Bulgaria",
                             "China",
                             "Congo",
                             "Denmark",
                             "Finland",
                             "Hungary",
                             "India",
                             "Ireland",
                             "Kazakhstan",
                             "Norway",
                             "Russia",
                             "Saudi Arabia",
                             "Serbia",
                             "Slovakia",
                             "Switzerland",
                             "Tunisia",
                             "Turkey",
                             "Turkmenistan"),
                     found.stream()
                          .map(Country::getName)
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Supply a between Restriction to a repository
            find method.
            """)
    public void testBetweenRestriction() {
        try {
            List<String> found = countries
                    .filter(_Country.code.between("LB", "LT"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
            assertEquals(List.of("LB: Lebanon",
                                 "LC: Saint Lucia",
                                 "LI: Liechtenstein",
                                 "LK: Sri Lanka",
                                 "LR: Liberia",
                                 "LS: Lesotho",
                                 "LT: Lithuania"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween()) {
                throw x;
            } else {
                return;
            }
        }
    }

    @Assertion(id = "829", strategy = """
            Supply a contains Restriction to a repository
            find method.
            """)
    public void testContainsRestriction() {
        try {
            List<String> found = countries
                    .filter(_Country.code.contains("Q"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
            assertEquals(List.of("GQ: Equatorial Guinea",
                                 "IQ: Iraq",
                                 "QA: Qatar"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLike()) {
                throw x;
            } else {
                return;
            }
        }
    }

    @Assertion(id = "829", strategy = """
            Supply an endsWith Restriction to a repository
            find method.
            """)
    public void testEndsWithRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.endsWith("J"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLike()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("BJ: Benin",
                             "DJ: Djibouti",
                             "FJ: Fiji",
                             "TJ: Tajikistan"),
                     found);
    }

    @Assertion(id = "829", strategy = """
            Supply an equalTo Restriction to a repository find method.
            """)
    public void testEqualToRestriction() {

        List<Country> found = countries.filter(_Country.code.equalTo("PL"));

        assertEquals(1,
                     found.size());

        Country country = found.get(0);

        assertEquals("PL",
                     country.getCode());
        assertEquals("Poland",
                     country.getName());
        assertEquals(Region.EUROPE,
                     country.getRegion());
        assertEquals(304255L,
                     country.getArea());
        assertEquals(38364679L,
                     country.getPopulation());
        assertEquals(1628000000000L,
                     country.getGdp());
        assertEquals(823768000000L,
                     country.getDebt());
        assertEquals(LocalDate.of(2025, Month.MARCH, 30),
                     country.getDaylightTimeBegins());
        assertEquals(LocalDate.of(2025, Month.OCTOBER, 26),
                     country.getDaylightTimeEnds());
        assertEquals("Warsaw",
                     country.getCapital().getName());
        assertEquals(1863056,
                     country.getCapital().getPopulation());
    }

    @Assertion(id = "829", strategy = """
            Supply a greaterThanEqual Restriction to a repository
            find method.
            """)
    public void testGreaterThanEqualRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.greaterThanEqual("ZA"))
                            .stream()
                            .map(c -> c.getCode() + ": " + c.getName())
                            .sorted()
                            .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThanEqual()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("ZA: South Africa",
                             "ZM: Zambia",
                             "ZW: Zimbabwe"),
                     found);
    }

    @Assertion(id = "829", strategy = """
                    Supply a greaterThan Restriction to a repository
                    find method.
                    """)
    public void testGreaterThanRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.greaterThan("ZL"))
                            .stream()
                            .map(c -> c.getCode() + ": " + c.getName())
                            .sorted()
                            .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThan()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("ZM: Zambia",
                             "ZW: Zimbabwe"),
                     found);
    }

    @Assertion(id = "829", strategy = """
            Supply an in Restriction to a repository find method.
            """)
    public void testInRestriction() {

        List<Country> found = countries
                .filter(_Country.code.in("UG", "SG", "PE"));

        Map<String, Country> map = new TreeMap<>();
        for (Country country : found) {
            map.put(country.getCode(), country);
        }

        assertEquals(List.of("PE", "SG", "UG"),
                     map.keySet()
                        .stream()
                        .collect(Collectors.toList()));

        Country country = map.get("PE");

        assertEquals("PE",
                     country.getCode());
        assertEquals("Peru",
                     country.getName());
        assertEquals(Region.SOUTH_AMERICA,
                     country.getRegion());
        assertEquals(1279996L,
                     country.getArea());
        assertEquals(32768614L,
                     country.getPopulation());
        assertEquals(517644000000L,
                     country.getGdp());
        assertEquals(182210688000L,
                     country.getDebt());
        assertEquals(null,
                     country.getDaylightTimeBegins());
        assertEquals(null,
                     country.getDaylightTimeEnds());
        assertEquals("Lima",
                     country.getCapital().getName());
        assertEquals(10151000,
                     country.getCapital().getPopulation());

        country = map.get("SG");

        assertEquals("SG",
                     country.getCode());
        assertEquals("Singapore",
                     country.getName());
        assertEquals(Region.ASIA,
                     country.getRegion());
        assertEquals(709L,
                     country.getArea());
        assertEquals(6080545L,
                     country.getPopulation());
        assertEquals(754758000000L,
                     country.getGdp());
        assertEquals(1335921660000L,
                     country.getDebt());
        assertEquals(null,
                     country.getDaylightTimeBegins());
        assertEquals(null,
                     country.getDaylightTimeEnds());
        assertEquals("Singapore",
                     country.getCapital().getName());
        assertEquals(5917600,
                     country.getCapital().getPopulation());

        country = map.get("UG");

        assertEquals("UG",
                     country.getCode());
        assertEquals("Uganda",
                     country.getName());
        assertEquals(Region.AFRICA,
                     country.getRegion());
        assertEquals(197100L,
                     country.getArea());
        assertEquals(50863850L,
                     country.getPopulation());
        assertEquals(135803000000L,
                     country.getGdp());
        assertEquals(72111393000L,
                     country.getDebt());
        assertEquals(null,
                     country.getDaylightTimeBegins());
        assertEquals(null,
                     country.getDaylightTimeEnds());
        assertEquals("Kampala",
                     country.getCapital().getName());
        assertEquals(1680600,
                     country.getCapital().getPopulation());
    }

    @Assertion(id = "829", strategy = """
            Supply an isNull Restriction to a repository
            find method, where no entities in the database
            match and are returned.
            """)
    public void testIsNullRestrictionNoneFound() {
        List<Country> found;
        try {
            found = countries.filter(_Country.code.isNull());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNull()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(0,
                     found.size());
    }

    @Assertion(id = "829", strategy = """
            Supply an isNull Restriction to a repository
            find method, where some entities in the database
            match and are returned.
            """)
    public void testIsNullRestrictionSomeFound() {
        List<String> found;
        try {
            found = countries
                            .filter(_Country.daylightTimeEnds.isNull())
                            .stream()
                            .map(Country::getName)
                            .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfNull()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(150,
                     found.size());

        assertEquals(true,
                     found.contains("Burundi"));
        assertEquals(false,
                     found.contains("Bulgaria"));

    }

    @Assertion(id = "829", strategy = """
            Supply a lessThanEqual Restriction to a repository
            find method.
            """)
    public void testLessThanEqualRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.lessThanEqual("AL"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLessThanEqual()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("AD: Andorra",
                             "AE: United Arab Emirates",
                             "AF: Afghanistan",
                             "AG: Antigua and Barbuda",
                             "AL: Albania"),
                     found);
    }

    @Assertion(id = "829", strategy = """
            Supply a lessThan Restriction to a repository
            find method.
            """)
    public void testLessThanRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.lessThan("AO"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLessThan()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("AD: Andorra",
                             "AE: United Arab Emirates",
                             "AF: Afghanistan",
                             "AG: Antigua and Barbuda",
                             "AL: Albania",
                             "AM: Armenia"),
                     found);
    }

    @Assertion(id = "829", strategy = """
            Supply a negated composite ALL restriction to a repository
            find method, where the negated ALL restriction combines two
            restrictions such that results must not satisfy both of the
            two restrictions.
            """)
    public void testNotAllRestrictions() {
        Restriction<Country> restriction =
                Restrict.all(_Country.code.greaterThanEqual("B"),
                             _Country.name.greaterThanEqual("B"))
                        .negate();

        List<Country> found;
        try {
            found = countries.filter(restriction);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("AD: Andorra",
                             "AE: United Arab Emirates",
                             "AF: Afghanistan",
                             "AG: Antigua and Barbuda",
                             "AL: Albania",
                             "AM: Armenia",
                             "AO: Angola",
                             "AR: Argentina",
                             "AS: American Samoa",
                             "AT: Austria",
                             "AU: Australia",
                             "AW: Aruba",
                             "AZ: Azerbaijan",
                             "DZ: Algeria"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Supply a negated composite ANY restriction to a repository
            find method, where the negated ANY restriction combines two
            restrictions such that results must not match either of the
            two restrictions.
            """)
    public void testNotAnyRestriction() {
        Restriction<Country> restriction =
                Restrict.any(_Country.code.lessThan("CA"),
                             _Country.code.greaterThan("CZ"))
                        .negate();

        List<Country> found;
        try {
            found = countries.filter(restriction);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThan() &&
                type.capableOfLessThan() &&
                type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("CA: Canada",
                             "CD: Congo",
                             "CF: Central African Republic",
                             "CH: Switzerland",
                             "CI: Ivory Coast",
                             "CL: Chile",
                             "CM: Cameroon",
                             "CN: China",
                             "CO: Colombia",
                             "CR: Costa Rica",
                             "CU: Cuba",
                             "CV: Cape Verde",
                             "CY: Cyprus",
                             "CZ: Czech Republic"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .sorted()
                          .toList());
    }

    @Assertion(id = "829", strategy = """
            Supply a notBetween Restriction to a repository
            find method.
            """)
    public void testNotBetweenRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.notBetween("AF", "ZA"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNotBetween()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("AD: Andorra",
                             "AE: United Arab Emirates",
                             "ZM: Zambia",
                             "ZW: Zimbabwe"),
                     found);
    }

    @Assertion(id = "829", strategy = """
            Supply a notContains Restriction to a repository
            find method.
            """)
    public void testNotContainsRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.notContains("L"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNotLike()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(CountryPopulator.EXPECTED_TOTAL - 20,
                     found.size());

        assertEquals(false,
                     found.contains("LV: Latvia"));

        assertEquals(false,
                     found.contains("LS: Sierra Leone"));

        assertEquals(true,
                     found.contains("SK: Slovakia"));
    }

    @Assertion(id = "829", strategy = """
            Supply a notEndsWith Restriction to a repository
            find method.
            """)
    public void testNotEndsWithRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.notEndsWith("N"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNotLike()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(CountryPopulator.EXPECTED_TOTAL - 10,
                     found.size());

        assertEquals(false,
                     found.contains("CN: China"));

        assertEquals(true,
                     found.contains("NA: Namibia"));
    }

    @Assertion(id = "829", strategy = """
            Supply a notEqualTo Restriction to a repository
            find method.
            """)
    public void testNotEqualToRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.notEqualTo("US"))
                    .stream()
                    .map(Country::getName)
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNotEqual()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(CountryPopulator.EXPECTED_TOTAL - 1,
                     found.size());

        assertEquals(false,
                     found.contains("United States of America"));
    }

    @Assertion(id = "829", strategy = """
            Supply a notIn Restriction to a repository
            find method.
            """)
    public void testNotInRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.notIn("MG", "IS", "ID", "LK", "CU"))
                    .stream()
                    .map(Country::getName)
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNotIn()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(CountryPopulator.EXPECTED_TOTAL - 5,
                     found.size());

        assertEquals(false,
                     found.contains("Cuba"));
        assertEquals(false,
                     found.contains("Iceland"));
        assertEquals(false,
                     found.contains("Indonesia"));
        assertEquals(false,
                     found.contains("Madagascar"));
        assertEquals(false,
                     found.contains("Sri Lanka"));
    }

    @Assertion(id = "829", strategy = """
            Supply a notNull Restriction to a repository
            find method, where all entities in the database
            match and are returned.
            """)
    public void testNotNullRestrictionAllFound() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.notNull())
                    .stream()
                    .map(Country::getName)
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNotNull()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(CountryPopulator.EXPECTED_TOTAL,
                     found.size());

        assertEquals(true,
                     found.contains("France"));
    }

    @Assertion(id = "829", strategy = """
            Supply a notNull Restriction to a repository
            find method, where some entities in the database
            match and are returned.
            """)
    public void testNotNullRestrictionSomeFound() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.daylightTimeEnds.notNull())
                    .stream()
                    .map(Country::getName)
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfNotNull()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(60,
                     found.size());

        assertEquals(true,
                     found.contains("Chile"));
        assertEquals(false,
                     found.contains("Bangladesh"));
    }

    @Assertion(id = "829", strategy = """
            Supply a notStartsWith Restriction to a repository
            find method.
            """)
    public void testNotStartsWithRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.notStartsWith("B"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNotLike()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(CountryPopulator.EXPECTED_TOTAL - 18,
                     found.size());

        assertEquals(false,
                     found.contains("BS: Bahamas"));

        assertEquals(true,
                     found.contains("GB: United Kingdom"));
    }

    @Assertion(id = "829", strategy = """
            Supply a restriction to a repository find method
            where the restriction requires a BooleanAttribute
            to match the isFalse condition.
            """)
    public void testRestrictBooleanAttribute() {
        List<String> found;
        try {
            found = countries
                    .filter(Restrict.all(_Country.unitedNationsMember.isFalse(),
                                         _Country.code.between("TO", "XO")))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("TW: Taiwan",
                             "VA: Vatican City State",
                             "XK: Kosovo"),
                     found);
    }

    @Assertion(id = "829", strategy = """
            Supply a startsWith Restriction to a repository
            find method.
            """)
    public void testStartsWithRestriction() {
        List<String> found;
        try {
            found = countries
                    .filter(_Country.code.startsWith("E"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLike()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("EC: Ecuador",
                             "EE: Estonia",
                             "EG: Egypt",
                             "ER: Eritrea",
                             "ES: Spain",
                             "ET: Ethiopia"),
                     found);
    }

    @Assertion(id = "829", strategy = """
            Supply to a repository find method a greaterThan Restriction
            comparing two LocalDate attributes. Verity that the method
            retrieves all matching entities.
            """)
    public void testTemporalGreaterThan() {
        List<Country> found;
        try {
            found = countries.withDaylightTime(
                    _Country.daylightTimeEnds.greaterThan(
                            _Country.daylightTimeBegins));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThan() &&
                type.capableOfNotNull()) {
                throw x;
            } else {
                return;
            }
        }

        // All data with non-null daylightTimeBegins/Ends has the end date
        // greater than the begin date:
        assertEquals(60,
                     found.size());

        for (Country country : found) {
            assertEquals(true,
                         country.getDaylightTimeEnds()
                                 .isAfter(country.getDaylightTimeBegins()));
        }
    }

    @Assertion(id = "829", strategy = """
            Supply to a repository find method a lessThan Restriction
            comparing a LocalDate attribute against the current date.
            Verity that the method retrieves all matching entities.
            """)
    public void testTemporalLessThanLocalDate() {
        List<Country> found;
        try {
            found = countries.withDaylightTime(
                    _Country.daylightTimeBegins.lessThan(
                            TemporalExpression.localDate()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLessThan() &&
                type.capableOfNotNull()) {
                throw x;
            } else {
                return;
            }
        }

        // All data for daylightTimeBegins is from year 2025, so all entries
        // with a non-null value must match:
        assertEquals(60,
                     found.size());

        for (Country country : found) {
            assertEquals(false,
                         country.getDaylightTimeBegins() == null);
        }
    }

    @Assertion(id = "829", strategy = """
            Supply the negated unrestricted Restriction to a
            repository find method to retrieve no entities.
            """)
    public void testUnmatchable() {
        List<Country> found =
                countries.filter(Restrict.not(Restrict.unrestricted()));

        assertEquals(0,
                     found.size());
    }

    @Assertion(id = "829", strategy = """
            Supply the unrestricted Restriction to a repository
            find method to retrieve all entities.
            """)
    public void testUnrestricted() {
        List<Country> found;
        try {
            found = countries.filter(Restrict.unrestricted());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfQueryWithoutWhere()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(CountryPopulator.EXPECTED_TOTAL,
                     found.size());
    }

}
