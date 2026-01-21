/**
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

@jakarta.nosql.Entity
@jakarta.persistence.Entity
public class Country {

    @jakarta.nosql.Column
    @jakarta.persistence.Column(nullable = false)
    private long area; // square kilometers

    @jakarta.persistence.Column(nullable = false)
    @jakarta.persistence.Embedded
    @jakarta.nosql.Column
    private City capital;

    @jakarta.nosql.Id
    @jakarta.persistence.Column(nullable = false)
    @jakarta.persistence.Id
    private String code; // ISO 3166-1 alpha-2 code

    @jakarta.nosql.Column
    private LocalDate daylightTimeBegins;

    @jakarta.nosql.Column
    private LocalDate daylightTimeEnds;

    @jakarta.nosql.Column
    @jakarta.persistence.Column(nullable = false)
    private long debt;

    @jakarta.nosql.Column
    @jakarta.persistence.Column(nullable = false)
    private long gdp;

    @jakarta.nosql.Column
    @jakarta.persistence.Column(nullable = false)
    private String name;

    @jakarta.nosql.Column
    @jakarta.persistence.Column(nullable = false)
    private long population;

    @jakarta.nosql.Column
    @jakarta.persistence.Column(nullable = false)
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private Region region;

    public Country() {
    }

    public long getArea() {
        return area;
    }

    public City getCapital() {
        return capital;
    }

    public String getCode() {
        return code;
    }

    public LocalDate getDaylightTimeBegins() {
        return daylightTimeBegins;
    }

    public LocalDate getDaylightTimeEnds() {
        return daylightTimeEnds;
    }

    public long getDebt() {
        return debt;
    }

    public long getGdp() {
        return gdp;
    }

    public String getName() {
        return name;
    }

    public long getPopulation() {
        return population;
    }

    public Region getRegion() {
        return region;
    }

    public static Country of(String code, String name, Region region,
                             long area, long population, long gdp, long debt,
                             LocalDate daylightTimeBegins,
                             LocalDate daylightTimeEnds,
                             City capital) {
        Country country = new Country();
        country.setCode(code);
        country.setName(name);
        country.setRegion(region);
        country.setArea(area);
        country.setPopulation(population);
        country.setGdp(gdp);
        country.setDebt(debt);
        country.setDaylightTimeBegins(daylightTimeBegins);
        country.setDaylightTimeEnds(daylightTimeEnds);
        country.setCapital(capital);
        return country;
    }

    public void setArea(long value) {
        area = value;
    }

    public void setCapital(City value) {
        capital = value;
    }

    public void setCode(String value) {
        code = value;
    }

    public void setDaylightTimeBegins(LocalDate value) {
        daylightTimeBegins = value;
    }

    public void setDaylightTimeEnds(LocalDate value) {
        daylightTimeEnds = value;
    }

    public void setDebt(long value) {
        debt = value;
    }

    public void setGdp(long value) {
        gdp = value;
    }

    public void setName(String value) {
        name = value;
    }

    public void setPopulation(long value) {
        population = value;
    }

    public void setRegion(Region value) {
        region = value;
    }
}
