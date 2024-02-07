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

import java.util.Objects;


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