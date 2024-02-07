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

public interface Sort {
    /**
     * Create a {@link Sort} instance
     *
     * @param property   the property name to order by
     * @param direction  the direction in which to order.
     * @param ignoreCase whether to request a case insensitive ordering.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when there is a null parameter
     */
    static Sort of(String property, Direction direction, boolean ignoreCase) {
        Objects.requireNonNull(direction, "direction is required");
        return new DefaultSort(property, Direction.ASC.equals(direction), ignoreCase);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#ASC ascending direction}
     * that does not request case insensitive ordering.
     *
     * @param property the property name to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null
     */
    static Sort asc(String property) {
        return new DefaultSort(property, true, false);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#ASC ascending direction}
     * and case insensitive ordering.
     *
     * @param property the property name to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null.
     */
    static Sort ascIgnoreCase(String property) {
        return new DefaultSort(property, true, true);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#DESC descending direction}
     * that does not request case insensitive ordering.
     *
     * @param property the property name to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null
     */
    static Sort desc(String property) {
        return new DefaultSort(property, false, false);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#DESC descending direction}
     * and case insensitive ordering.
     *
     * @param property the property name to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null.
     */
    static Sort descIgnoreCase(String property) {
        return new DefaultSort(property, false, true);
    }

    /**
     * Name of the property to order by.
     *
     * @return The property name to order by; will never be {@code null}.
     */
    String property();

    /**
     * <p>Indicates whether or not to request case insensitive ordering
     * from a database with case sensitive collation.
     * A database with case insensitive collation performs case insensitive
     * ordering regardless of the requested <code>ignoreCase</code> value.</p>
     *
     * @return Returns whether or not to request case insensitive sorting for the property.
     */
    boolean ignoreCase();

    /**
     * Indicates whether to sort the property in ascending order (true) or descending order (false).
     *
     * @return Returns whether sorting for this property shall be ascending.
     */
    boolean isAscending();

    /**
     * Indicates whether to sort the property in descending order (true) or ascending order (false).
     *
     * @return Returns whether sorting for this property shall be descending.
     */
    boolean isDescending();
}
