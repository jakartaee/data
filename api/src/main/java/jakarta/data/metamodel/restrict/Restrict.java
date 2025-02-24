/*
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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
package jakarta.data.metamodel.restrict;

import jakarta.data.metamodel.BasicAttribute;
import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.metamodel.constraint.Constraint;
import jakarta.data.metamodel.constraint.Like;

import java.util.List;
import java.util.Objects;
import java.util.Set;

// TODO document
// This is one of two places from which to obtain restrictions.
// The other place is from static metamodel attributes.
public class Restrict {

    // prevent instantiation
    private Restrict() {
    }

    @SafeVarargs
    public static <T> Restriction<T> all(Restriction<T>... restrictions) {
        return new CompositeRestrictionRecord<>(CompositeRestriction.Type.ALL,
                                                List.of(restrictions));
    }

    @SafeVarargs
    public static <T> Restriction<T> any(Restriction<T>... restrictions) {
        return new CompositeRestrictionRecord<>(CompositeRestriction.Type.ANY,
                                                List.of(restrictions));
    }

    // TODO Need to think more about how to best cover negation of multiple
    // and then make negation of Single consistent with it

    public static <T,V> BasicRestriction<T> restrict(Constraint<V> constraint, BasicAttribute<T,V> attribute) {
        return new BasicRestrictionRecord<>(attribute, constraint);
    }

    public static <T> TextRestriction<T> contains(String substring, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.substring(substring));
    }

    public static <T> TextRestriction<T> startsWith(String prefix, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.prefix(prefix));
    }

    public static <T> TextRestriction<T> endsWith(String suffix, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.suffix(suffix));
    }

    public static <T,V> BasicRestriction<T> equalTo(V value, BasicAttribute<T,V> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.equalTo(value));
    }

    public static <T> TextRestriction<T> equalTo(String value, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.literal(value));
    }

    public static <T> BasicRestriction<T> isNull(BasicAttribute<T,?> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.isNull());
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> greaterThan(V value, ComparableAttribute<T, V> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.greaterThan(value));
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> greaterThanEqual(V value, ComparableAttribute<T,V> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.greaterThanOrEqual(value));
    }

    public static <T> BasicRestriction<T> in(Set<?> values, BasicAttribute<T,?> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.in(values));
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> lessThan(V value, ComparableAttribute<T,V> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.lessThan(value));
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> lessThanEqual(V value, ComparableAttribute<T,V> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.lessThanOrEqual(value));
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> between(V lowerBound, V upperBound, ComparableAttribute<T,V> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.between(lowerBound, upperBound));
    }

    public static <T> TextRestriction<T> like(Like textRange, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, textRange);
    }

    public static <T> TextRestriction<T> like(String pattern, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.pattern(pattern));
    }

    public static <T> TextRestriction<T> like(String pattern, char charWildcard, char stringWildcard, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.pattern(pattern, charWildcard, stringWildcard));
    }

    public static <T> TextRestriction<T> like(String pattern, char charWildcard, char stringWildcard, char escape, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.pattern(pattern, charWildcard, stringWildcard, escape));
    }

    // convenience method for those who would prefer to avoid .negate()
    public static <T> Restriction<T> not(Restriction<T> restriction) {
        Objects.requireNonNull(restriction, "Restriction must not be null");
        return restriction.negate();
    }

    public static <T,V> BasicRestriction<T> notEqualTo(V value, BasicAttribute<T,V> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.equalTo(value), true);
    }

    public static <T> TextRestriction<T> notEqualTo(String value, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.literal(value), true);
    }

    public static <T> BasicRestriction<T> notNull(BasicAttribute<T,?> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.isNull(), true);
    }

    public static <T> TextRestriction<T> notContains(String substring, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.substring(substring), true);
    }

    public static <T> TextRestriction<T> notStartsWith(String prefix, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.prefix(prefix), true);
    }

    public static <T> TextRestriction<T> notEndsWith(String suffix, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.suffix(suffix), true);
    }

    public static <T> BasicRestriction<T> notIn(Set<?> values, BasicAttribute<T,?> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.in(values), true);
    }

    public static <T> TextRestriction<T> notLike(String pattern, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.pattern(pattern), true);
    }

    public static <T> TextRestriction<T> notLike(String pattern, char charWildcard, char stringWildcard, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.pattern(pattern, charWildcard, stringWildcard), true);
    }

    public static <T> TextRestriction<T> notLike(String pattern, char charWildcard, char stringWildcard, char escape, TextAttribute<T> attribute) {
        return new TextRestrictionRecord<>(attribute, Like.pattern(pattern, charWildcard, stringWildcard, escape), true);
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> notBetween(V lowerBound, V upperBound, ComparableAttribute<T,?> attribute) {
        return new BasicRestrictionRecord<>(attribute, Constraint.between(lowerBound, upperBound), true);
    }

    @SuppressWarnings("unchecked")
    public static <T> Restriction<T> unrestricted() {
        return (Restriction<T>) Unrestricted.INSTANCE;
    }
}
