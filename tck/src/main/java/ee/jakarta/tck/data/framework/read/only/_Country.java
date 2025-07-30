/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.framework.read.only;

import java.time.LocalDate;

import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.NavigableAttribute;
import jakarta.data.metamodel.NumericAttribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.metamodel.TemporalAttribute;
import jakarta.data.metamodel.TextAttribute;

import ee.jakarta.tck.data.framework.read.only.Country.Region;

/**
 * Static metamodel class for the Country entity.
 */
@StaticMetamodel(Country.class)
public interface _Country {

    String AREA = "area";
    String CAPITAL = "capital";
    String CAPITAL_NAME = "capital.name";
    String CAPITAL_POPULATION = "capital.population";
    String CODE = "code";
    String DAYLIGHTTIMEBEGINS = "daylightTimeBegins";
    String DAYLIGHTTIMEENDS = "daylightTimeEnds";
    String DEBT = "debt";
    String GDP = "gdp";
    String NAME = "name";
    String POPULATION = "population";
    String REGION = "region";

    NumericAttribute<Country, Long> area = //
                    NumericAttribute.of(Country.class, AREA, long.class);

    NavigableAttribute<Country, City> capital = //
                    NavigableAttribute.of(Country.class, CAPITAL, City.class);

    TextAttribute<Country> code = //
                    TextAttribute.of(Country.class, CODE);

    TemporalAttribute<Country, LocalDate> daylightTimeBegins = //
                    TemporalAttribute.of(Country.class, DAYLIGHTTIMEBEGINS, LocalDate.class);

    TemporalAttribute<Country, LocalDate> daylightTimeEnds = //
                    TemporalAttribute.of(Country.class, DAYLIGHTTIMEENDS, LocalDate.class);

    NumericAttribute<Country, Long> debt = //
                    NumericAttribute.of(Country.class, DEBT, long.class);

    NumericAttribute<Country, Long> gdp = //
                    NumericAttribute.of(Country.class, GDP, long.class);

    TextAttribute<Country> name = //
                    TextAttribute.of(Country.class, NAME);

    NumericAttribute<Country, Long> poulation = //
                    NumericAttribute.of(Country.class, POPULATION, long.class);

    ComparableAttribute<Country, Region> region = //
                    ComparableAttribute.of(Country.class, REGION, Region.class);
}
