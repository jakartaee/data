/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel;

import jakarta.data.Restrict;
import jakarta.data.Restriction;
import jakarta.data.Sort;

/**
 * Represents a sortable entity attribute in the {@link StaticMetamodel}.
 * Entity attribute types that are sortable include:
 *
 * <ul>
 * <li>numeric attributes</li>
 * <li>enum attributes</li>
 * <li>time attributes</li>
 * <li>boolean attributes</li>
 * <li>{@link TextAttribute textual attributes}</li>
 * </ul>
 *
 *  * <p>For example, given a {@code Product} entity:</p>
 *  *
 *  * <pre>{@code
 *  * Sort<Product> sortByPriceAsc = _Product.price.asc();
 *  * Sort<Product> sortByDateDesc = _Product.creationDate.desc();
 *  *
 *  * Restriction<Product> priceRange = _Product.price.between(50.0, 200.0);
 *  * Restriction<Product> higherPrice = _Product.price.greaterThanEqual(100.0);
 *  * }</pre>
 *
 * @param <T> entity class of the static metamodel.
 */
public interface SortableAttribute<T> extends Attribute<T> {

    /**
     * Obtain a request for an ascending {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending sort on the entity attribute.
     */
    Sort<T> asc();

    /**
     * Obtain a request for a descending {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending sort on the entity attribute.
     */
    Sort<T> desc();

    /**
     * Creates a restriction that the attribute's value must be between the specified minimum and maximum values.
     *
     * <p>For example:</p>
     * <pre>{@code
     * Restriction<Product> priceRange = _Product.price.between(50.0, 200.0);
     * }</pre>
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @param <V> the type of the values, must be comparable
     * @return a {@link Restriction} representing the condition
     */
    default <V extends Comparable<V>> Restriction<T> between(V min, V max) {
        return Restrict.between(min, max, name());
    }

    /**
     * Creates a restriction that the attribute's value must be greater than the specified value.
     *
     * <p>For example:</p>
     * <pre>{@code
     * Restriction<Product> higherPrice = _Product.price.greaterThan(100.0);
     * }</pre>
     *
     * @param value the value to compare against
     * @param <V>   the type of the value, must be comparable
     * @return a {@link Restriction} representing the condition
     */
    default <V extends Comparable<V>> Restriction<T> greaterThan(V value) {
        return Restrict.greaterThan(value, name());
    }

    /**
     * Creates a restriction that the attribute's value must be greater than or equal to the specified value.
     *
     * <p>For example:</p>
     * <pre>{@code
     * Restriction<Product> higherPrice = _Product.price.greaterThanEqual(100.0);
     * }</pre>
     *
     * @param value the value to compare against
     * @param <V>   the type of the value, must be comparable
     * @return a {@link Restriction} representing the condition
     */
    default <V extends Comparable<V>> Restriction<T> greaterThanEqual(V value) {
        return Restrict.greaterThanEqual(value, name());
    }

    /**
     * Creates a restriction that the attribute's value must be less than the specified value.
     *
     * <p>For example:</p>
     * <pre>{@code
     * Restriction<Product> lowerPrice = _Product.price.lessThan(100.0);
     * }</pre>
     *
     * @param value the value to compare against
     * @param <V>   the type of the value, must be comparable
     * @return a {@link Restriction} representing the condition
     */
    default <V extends Comparable<V>> Restriction<T> lessThan(V value) {
        return Restrict.lessThan(value, name());
    }

    /**
     * Creates a restriction that the attribute's value must be less than or equal to the specified value.
     *
     * <p>For example:</p>
     * <pre>{@code
     * Restriction<Product> lowerPrice = _Product.price.lessThanEqual(200.0);
     * }</pre>
     *
     * @param value the value to compare against
     * @param <V>   the type of the value, must be comparable
     * @return a {@link Restriction} representing the condition
     */
    default <V extends Comparable<V>> Restriction<T> lessThanEqual(V value) {
        return Restrict.lessThanEqual(value, name());
    }
}
