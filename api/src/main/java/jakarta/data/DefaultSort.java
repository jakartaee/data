/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;

import jakarta.data.page.Pageable;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import java.util.Objects;

/**
 * <p>Requests sorting on a given entity attribute.</p>
 *
 * <p><code>Sort</code> allows the application to dynamically provide
 * sort criteria which includes a case sensitivity request,
 * a {@link Direction} and a property.</p>
 *
 * <p>Dynamic <code>Sort</code> criteria can be specified when
 * {@link Pageable#sortBy(Sort[]) requesting a page of results}
 * or can be optionally specified as
 * parameters to a repository method in any of the positions that are after
 * the query parameters. You can use <code>Sort...</code> to allow a variable
 * number of <code>Sort</code> criteria. For example,</p>
 *
 * <pre>
 * Employee[] findByYearHired(int yearYired, Limit maxResults, Sort... sortBy);
 * ...
 * highestPaidNewHires = employees.findByYearHired(Year.now(),
 *                                                 Limit.of(10),
 *                                                 Sort.desc("salary"),
 *                                                 Sort.asc("lastName"),
 *                                                 Sort.asc("firstName"));
 * </pre>
 *
 * <p>When combined on a method with static sort criteria
 * (<code>OrderBy</code> keyword or {@link OrderBy} annotation or
 * {@link Query} with an <code>ORDER BY</code> clause), the static
 * sort criteria is applied first, followed by the dynamic sort criteria
 * that is defined by <code>Sort</code> instances in the order listed.</p>
 *
 * <p>In the example above, the matching employees are sorted first by salary
 * from highest to lowest. Employees with the same salary are then sorted
 * alphabetically by last name. Employees with the same salary and last name
 * are then sorted alphabetically by first name.</p>
 *
 * <p>A repository method will fail with a
 * {@link jakarta.data.exceptions.DataException DataException}
 * or a more specific subclass if</p>
 * <ul>
 * <li>a <code>Sort</code> parameter is
 *     specified in combination with a {@link Pageable} parameter with
 *     {@link Pageable#sorts()}.</li>
 * <li>the database is incapable of ordering with the requested
 *     sort criteria.</li>
 * </ul>
 *
 * @param property    name of the property to order by.
 * @param isAscending whether ordering for this property is ascending (true) or descending (false).
 * @param ignoreCase  whether or not to request case insensitive ordering from a database with case sensitive collation.
 */
record DefaultSort(String property, boolean isAscending, boolean ignoreCase) implements Sort {

    /**
     * <p>Defines sort criteria for an entity property. For more descriptive code, use:</p>
     * <ul>
     * <li>{@link #asc(String) Sort.asc(propertyName)} for ascending sort on a property.</li>
     * <li>{@link #ascIgnoreCase(String) Sort.ascIgnoreCase(propertyName)} for case insensitive ascending sort on a property.</li>
     * <li>{@link #desc(String) Sort.desc(propertyName)} for descending sort on a property.</li>
     * <li>{@link #descIgnoreCase(String) Sort.descIgnoreCase(propertyName)} for case insensitive descending sort on a property.</li>
     * </ul>
     *
     * @param property    name of the property to order by.
     * @param isAscending whether ordering for this property is ascending (true) or descending (false).
     * @param ignoreCase  whether or not to request case insensitive ordering from a database with case sensitive collation.
     */
    public DefaultSort {
        Objects.requireNonNull(property, "property is required");
    }

    @Override
    public String property() {
        return property;
    }

    @Override
    public boolean ignoreCase() {
        return ignoreCase;
    }

    @Override
    public boolean isAscending() {
        return isAscending;
    }

    @Override
    public boolean isDescending() {
        return !isAscending;
    }

}