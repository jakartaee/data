/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package jakarta.data.constraint;

import java.util.Set;


/**
 * A constraint that can be evaluated in a query to filter results based on attribute values.
 * <p>
 * Constraints are typically created from entity attributes using the static metamodel.
 * </p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>{@code
 * List<Product> results = products.findAll(
 *     _Product.price.greaterThan(50.0)
 * );
 * }</pre>
 *
 * @param <V> the type of the attribute this constraint applies to
 */
public interface Constraint<V> {

    /**
     * Returns a constraint that represents the logical negation of this constraint.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.type.equalTo(ProductType.AUTOMOTIVE).negate()
     * );
     * }</pre>
     *
     * @return a constraint representing the logical negation
     */
    Constraint<V> negate();

    /**
     * Creates a constraint that checks if an attribute is equal to the specified value.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.type.equalTo(ProductType.AUTOMOTIVE)
     * );
     * }</pre>
     *
     * @param value the value to compare against
     * @param <V> the value type
     * @return a constraint that checks for equality with the given value
     */
    static <V> EqualTo<V> equalTo(V value) {
        return EqualTo.value(value);
    }

    /**
     * Creates a constraint that checks if an attribute is not equal to the specified value.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.type.notEqualTo(ProductType.AUTOMOTIVE)
     * );
     * }</pre>
     *
     * @param value the value to compare against
     * @param <V> the value type
     * @return a constraint that checks for inequality with the given value
     */
    static <V> NotEqualTo<V> notEqualTo(V value) {
        return NotEqualTo.value(value);
    }

    /**
     * Creates a constraint that checks if an attribute's value is in the specified list.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.type.in(ProductType.AUTOMOTIVE, ProductType.ELECTRONICS)
     * );
     * }</pre>
     *
     * @param values the values to match against
     * @param <V> the value type
     * @return a constraint that checks if the attribute matches one of the specified values
     */
    @SafeVarargs
    static <V> In<V> in(V... values) {
        return In.values(values);
    }

    /**
     * Creates a constraint that checks if an attribute's value is in the specified set.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.type.in(Set.of(ProductType.AUTOMOTIVE, ProductType.ELECTRONICS))
     * );
     * }</pre>
     *
     * @param values the values to match against
     * @param <V> the value type
     * @return a constraint that checks if the attribute matches one of the specified values
     */
    static <V> In<V> in(Set<V> values) {
        return In.values(values);
    }

    /**
     * Creates a constraint that checks if an attribute's value is not in the specified list.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.type.notIn(ProductType.AUTOMOTIVE, ProductType.ELECTRONICS)
     * );
     * }</pre>
     *
     * @param values the values the attribute must not match
     * @param <V> the value type
     * @return a constraint that checks if the attribute does not match any of the specified values
     */
    @SafeVarargs
    static <V> NotIn<V> notIn(V... values) {
        return NotIn.values(values);
    }

    /**
     * Creates a constraint that checks if an attribute's value is not in the specified set.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.type.notIn(Set.of(ProductType.AUTOMOTIVE, ProductType.ELECTRONICS))
     * );
     * }</pre>
     *
     * @param values the values the attribute must not match
     * @param <V> the value type
     * @return a constraint that checks if the attribute does not match any of the specified values
     */
    static <V> NotIn<V> notIn(Set<V> values) {
        return NotIn.values(values);
    }

    static Like like(String pattern) {
        return Like.pattern(pattern);
    }

    static Like like(String pattern, char charWildcard, char stringWildcard) {
        return Like.pattern(pattern, charWildcard, stringWildcard);
    }

    static Like like(String pattern, char charWildcard, char stringWildcard, char escape) {
        return Like.pattern(pattern, charWildcard, stringWildcard, escape);
    }

    static NotLike notLike(String pattern) {
        return NotLike.pattern(pattern);
    }

    static NotLike notLike(String pattern, char charWildcard, char stringWildcard) {
        return NotLike.pattern(pattern, charWildcard, stringWildcard);
    }

    static NotLike notLike(String pattern, char charWildcard, char stringWildcard, char escape) {
        return NotLike.pattern(pattern, charWildcard, stringWildcard, escape);
    }

    /**
     * Creates a constraint that checks if an attribute's value is {@code null}.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.description.isNull()
     * );
     * }</pre>
     *
     * @param <V> the value type
     * @return a constraint that checks if the attribute is {@code null}
     */
    static <V> Null<V> isNull() {
        return Null.instance();
    }

    /**
     * Creates a constraint that checks if an attribute's value is not {@code null}.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.description.notNull()
     * );
     * }</pre>
     *
     * @param <V> the value type
     * @return a constraint that checks if the attribute is not {@code null}
     */
    static <V> NotNull<V> notNull() {
        return NotNull.instance();
    }

    /**
     * Creates a constraint that checks if an attribute's value is greater than the given bound.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.price.greaterThan(100.0)
     * );
     * }</pre>
     *
     * @param <V> the value type, must be {@link Comparable}
     * @param bound the lower exclusive bound
     * @return a constraint representing {@code greaterThan}
     */
    static <V extends Comparable<?>> GreaterThan<V> greaterThan(V bound) {
        return GreaterThan.bound(bound);
    }

    /**
     * Creates a constraint that checks if an attribute's value is less than the given bound.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.price.lessThan(100.0)
     * );
     * }</pre>
     *
     * @param <V> the value type, must be {@link Comparable}
     * @param bound the upper exclusive bound
     * @return a constraint representing {@code lessThan}
     */
    static <V extends Comparable<?>> LessThan<V> lessThan(V bound) {
        return LessThan.bound(bound);
    }

    /**
     * Creates a constraint that checks if an attribute's value is greater than or equal to the given bound.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.price.greaterThanOrEqual(100.0)
     * );
     * }</pre>
     *
     * @param <V> the value type, must be {@link Comparable}
     * @param bound the minimum inclusive bound
     * @return a constraint representing {@code greaterThanOrEqual}
     */
    static <V extends Comparable<?>> GreaterThanOrEqual<V> greaterThanOrEqual(V bound) {
        return GreaterThanOrEqual.min(bound);
    }

    /**
     * Creates a constraint that checks if an attribute's value is less than or equal to the given bound.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.price.lessThanOrEqual(100.0)
     * );
     * }</pre>
     *
     * @param <V> the value type, must be {@link Comparable}
     * @param bound the maximum inclusive bound
     * @return a constraint representing {@code lessThanOrEqual}
     */
    static <V extends Comparable<?>> LessThanOrEqual<V> lessThanOrEqual(V bound) {
        return LessThanOrEqual.max(bound);
    }

    /**
     * Creates a constraint that checks if an attribute's value is between the given lower and upper bounds, inclusive.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.price.between(10.0, 100.0)
     * );
     * }</pre>
     *
     * @param <V> the value type, must be {@link Comparable}
     * @param lowerBound the inclusive lower bound
     * @param upperBound the inclusive upper bound
     * @return a constraint representing {@code between}
     */
    static <V extends Comparable<?>> Between<V> between(V lowerBound, V upperBound) {
        return Between.bounds(lowerBound, upperBound);
    }

    /**
     * Creates a constraint that checks if an attribute's value is outside the given bounds.
     *
     * <pre>{@code
     * List<Product> results = products.findAll(
     *     _Product.price.notBetween(10.0, 100.0)
     * );
     * }</pre>
     *
     * @param <V> the value type, must be {@link Comparable}
     * @param lowerBound the inclusive lower bound
     * @param upperBound the inclusive upper bound
     * @return a constraint representing {@code notBetween}
     */
    static <V extends Comparable<?>> NotBetween<V> notBetween(V lowerBound, V upperBound) {
        return NotBetween.bounds(lowerBound, upperBound);
    }
}
