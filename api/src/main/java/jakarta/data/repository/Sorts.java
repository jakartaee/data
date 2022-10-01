/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package jakarta.data.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Sorts option for queries.
 * It is a {@link Sort} collection
 * Sorted instances are immutable and all mutating operations on this interface return a new instance.
 */
public class Sorts {

    private final List<Sort> sorts;

    private Sorts() {
        this.sorts = Collections.emptyList();
    }

    private Sorts(List<Sort> sorts) {
        this.sorts = sorts;
    }
    /**
     * Adds an sort object.
     *
     * @param sort The sort object
     * @return A new sort with the sort applied
     * @throws NullPointerException when sort is null
     */
    public Sorts sort(Sort sort) {
        Objects.requireNonNull(sort, "sort is required");
        return new Sorts(new ArrayList<>(sorts) {{
            this.add(sort);
        }});
    }

    /**
     * Returns a new Sorts consisting of the Sorts of the current Sort combined with the given ones.
     *
     * @param sorts The sort
     * @return A new sort with the order applied
     * @throws NullPointerException when sort is null
     */
    public Sorts add(Sorts sorts) {
        Objects.requireNonNull(sorts, "sort is required");
        return new Sorts(new ArrayList<>(Sorts.this.sorts) {{
            this.addAll(sorts.getOrderBy());
        }});
    }

    /**
     * Orders by the specified property name (defaults to ascending) {@link Direction#ASC}.
     *
     * @param property The property name to order by
     * @return A new sort with the order applied
     * @throws NullPointerException when property is null
     */
    public Sorts sort(String property) {
        Objects.requireNonNull(property, "property is required");
        return sort(Sort.asc(property));
    }

    /**
     * Orders by the specified property name and direction.
     *
     * @param property  The property name to order by
     * @param direction Either "asc" for ascending or "desc" for descending
     * @return A new sort with the order applied
     * @throws NullPointerException when there is null parameter
     */
    public Sorts sort(String property, Direction direction) {
        Objects.requireNonNull(property, "property is required");
        Objects.requireNonNull(direction, "direction is required");
        return sort(Sort.of(property, direction));
    }

    /**
     * @return The order definitions for this sort.
     */
    public List<Sort> getOrderBy() {
        return Collections.unmodifiableList(sorts);
    }

    /**
     * Returns whether the current {@link Sorts#getOrderBy()} is empty.
     *
     * @return the  {@link Sorts#getOrderBy()} is empty.
     */
    public boolean isEmpty() {
        return this.sorts.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sorts sorts = (Sorts) o;
        return Objects.equals(this.sorts, sorts.sorts);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sorts);
    }

    @Override
    public String toString() {
        return "Sorts{" +
                "sorts=" + sorts +
                '}';
    }

    /**
     * Create a {@link Sorts} instance
     *
     * @param property  the property name to order by
     * @param direction The direction order by
     * @return an {@link Sorts} instance
     * @throws NullPointerException when there are null parameter
     */
    static Sorts of(String property, Direction direction) {
        Objects.requireNonNull(property, "property is required");
        Objects.requireNonNull(direction, "direction is required");
        return new Sorts(Collections.singletonList(Sort.of(property, direction)));
    }

    /**
     * Create a {@link Sorts} instance on ascending direction {@link  Direction#ASC}
     *
     * @param property the property name to order by
     * @return an {@link Sorts} instance
     * @throws NullPointerException when property is null
     */
    static Sorts asc(String property) {
        return of(property, Direction.ASC);
    }

    /**
     * Create a {@link Sorts} instance on descending direction {@link  Direction#DESC}
     *
     * @param property the property name to order by
     * @return an {@link Sorts} instance
     * @throws NullPointerException when property is null
     */
    static Sorts desc(String property) {
        return of(property, Direction.DESC);
    }

    /**
     * Creates a new Sort for the given Orders
     *
     * @param sorts an order list
     * @return The sort
     * @throws NullPointerException when sorts is null
     */
    static Sorts of(Iterable<Sort> sorts) {
        Objects.requireNonNull(sorts, "sorts is required");
        return new Sorts(StreamSupport.stream(sorts.spliterator(), false)
                .peek(s -> Objects.requireNonNull(s, "sort element cannot be null"))
                .collect(Collectors.toUnmodifiableList()));
    }

    /**
     * Creates a new Sort for the given Orders
     *
     * @param sorts an order list
     * @return The sort
     */
    static Sorts of(Sort... sorts) {
        return of(List.of(sorts));
    }


}
