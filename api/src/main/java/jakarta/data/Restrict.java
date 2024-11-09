/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package jakarta.data;

import java.util.List;
import java.util.Set;

import jakarta.data.Restriction.Composite;
import jakarta.data.Restriction.Operator;

// TODO document
// This is one of two places from which to obtain restrictions.
// The other place is from static metamodel attributes.
public class Restrict {

    // used internally for more readable code
    private static final boolean NOT = true;

    // prevent instantiation
    private Restrict() {
    }

    @SafeVarargs
    public static <T> Restriction<T> all(Restriction<T>... restrictions) {
        return new CompositeRestriction<>(Composite.Type.ALL, List.of(restrictions));
    }

    @SafeVarargs
    public static <T> Restriction<T> any(Restriction<T>... restrictions) {
        return new CompositeRestriction<>(Composite.Type.ANY, List.of(restrictions));
    }

    public static <T> Restriction<T> between(Comparable<Object> min,
                                             Comparable<Object> max,
                                             String field) {
        return all(greaterThanEqual(min, field),
                   lessThanEqual(max, field));
    }

    // TODO Need to think more about how to best cover negation of multiple
    // and then make negation of Single consistent with it

    public static <T> Restriction.Text<T> contains(String substringPattern, String field) {
        return new TextRestriction<>(field, Operator.CONTAINS, substringPattern);
    }

    public static <T> Restriction.Text<T> endsWith(String suffixPattern, String field) {
        return new TextRestriction<>(field, Operator.ENDS_WITH, suffixPattern);
    }

    public static <T> Restriction<T> equalTo(Object value, String field) {
        return new BasicRestriction<>(field, Operator.EQUAL, value);
    }

    public static <T> Restriction.Text<T> equalTo(String value, String field) {
        return new TextRestriction<>(field, Operator.EQUAL, value);
    }

    public static <T> Restriction<T> greaterThan(Comparable<Object> value, String field) {
        return new BasicRestriction<>(field, Operator.GREATER_THAN, value);
    }

    public static <T> Restriction.Text<T> greaterThan(String value, String field) {
        return new TextRestriction<>(field, Operator.GREATER_THAN, value);
    }

    public static <T> Restriction<T> greaterThanEqual(Comparable<Object> value, String field) {
        return new BasicRestriction<>(field, Operator.GREATER_THAN_EQUAL, value);
    }

    public static <T> Restriction.Text<T> greaterThanEqual(String value, String field) {
        return new TextRestriction<>(field, Operator.GREATER_THAN_EQUAL, value);
    }

    public static <T> Restriction<T> in(Set<Object> values, String field) {
        return new BasicRestriction<>(field, Operator.IN, values);
    }

    public static <T> Restriction<T> lessThan(Comparable<Object> value, String field) {
        return new BasicRestriction<>(field, Operator.LESS_THAN, value);
    }

    public static <T> Restriction.Text<T> lessThan(String value, String field) {
        return new TextRestriction<>(field, Operator.LESS_THAN, value);
    }

    public static <T> Restriction<T> lessThanEqual(Comparable<Object> value, String field) {
        return new BasicRestriction<>(field, Operator.LESS_THAN_EQUAL, value);
    }

    public static <T> Restriction.Text<T> lessThanEqual(String value, String field) {
        return new TextRestriction<>(field, Operator.LESS_THAN_EQUAL, value);
    }

    // TODO once Pattern is added
    //public static <T> Restriction.Text<T> like(Pattern pattern, String field) {
    //    return new TextRestriction<>(field, Operator.LIKE, pattern);
    //}

    public static <T> Restriction.Text<T> like(String pattern, String field) {
        return new TextRestriction<>(field, Operator.LIKE, pattern);
    }

    public static <T> Restriction<T> not(Object value, String field) {
        return new BasicRestriction<>(field, NOT, Operator.EQUAL, value);
    }

    public static <T> Restriction.Text<T> not(String value, String field) {
        return new TextRestriction<>(field, NOT, Operator.EQUAL, value);
    }

    public static <T> Restriction.Text<T> notContains(String substringPattern, String field) {
        return new TextRestriction<>(field, NOT, Operator.CONTAINS, substringPattern);
    }

    public static <T> Restriction.Text<T> notEndsWith(String suffixPattern, String field) {
        return new TextRestriction<>(field, NOT, Operator.ENDS_WITH, suffixPattern);
    }

    public static <T> Restriction<T> notIn(Set<Object> values, String field) {
        return new BasicRestriction<>(field, NOT, Operator.IN, values);
    }

    public static <T> Restriction.Text<T> notLike(String pattern, String field) {
        return new TextRestriction<>(field, NOT, Operator.LIKE, pattern);
    }

    public static <T> Restriction.Text<T> notStartsWith(String prefixPattern, String field) {
        return new TextRestriction<>(field, NOT, Operator.STARTS_WITH, prefixPattern);
    }

    public static <T> Restriction.Text<T> startsWith(String prefixPattern, String field) {
        return new TextRestriction<>(field, Operator.STARTS_WITH, prefixPattern);
    }
}
