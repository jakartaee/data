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

import jakarta.data.Restriction.Basic;
import jakarta.data.Restriction.Operator;

// TODO document
// This is one of two places from which to obtain restrictions.
// The other place is from static metamodel attributes.
public enum Restrict {
    ALL,
    ANY;

    // used internally for more readable code
    private static final boolean NOT = true;

    @SafeVarargs
    public static <T> Restriction<T> all(Restriction<T>... restrictions) {
        return new CompositeRestriction<>(ALL, List.of(restrictions));
    }

    @SafeVarargs
    public static <T> Restriction<T> any(Restriction<T>... restrictions) {
        return new CompositeRestriction<>(ANY, List.of(restrictions));
    }

    public static <T> Restriction<T> between(Comparable<Object> min,
                                             Comparable<Object> max,
                                             String field) {
        return all(greaterThanEqual(min, field),
                   lessThanEqual(max, field));
    }

    // TODO Need to think more about how to best cover negation of multiple
    // and then make negation of Single consistent with it

    public static <T> Basic<T> contains(String substringPattern, String field) {
        return new BasicRestriction<>(field, Operator.CONTAINS, substringPattern);
    }

    public static <T> Basic<T> endsWith(String suffixPattern, String field) {
        return new BasicRestriction<>(field, Operator.ENDS_WITH, suffixPattern);
    }

    public static <T> Basic<T> equalTo(Object value, String field) {
        return new BasicRestriction<>(field, Operator.EQUAL, value);
    }

    public static <T> Basic<T> greaterThan(Comparable<Object> value, String field) {
        return new BasicRestriction<>(field, Operator.GREATER_THAN, value);
    }

    public static <T> Basic<T> greaterThanEqual(Comparable<Object> value, String field) {
        return new BasicRestriction<>(field, Operator.GREATER_THAN_EQUAL, value);
    }

    public static <T> Basic<T> in(Set<Object> values, String field) {
        return new BasicRestriction<>(field, Operator.IN, values);
    }

    public static <T> Basic<T> lessThan(Comparable<Object> value, String field) {
        return new BasicRestriction<>(field, Operator.LESS_THAN, value);
    }

    public static <T> Basic<T> lessThanEqual(Comparable<Object> value, String field) {
        return new BasicRestriction<>(field, Operator.LESS_THAN_EQUAL, value);
    }

    // TODO once Pattern is added
    //public static <T> Basic<T> like(Pattern pattern, String field) {
    //    return new BasicRestriction<>(field, Operator.LIKE, pattern);
    //}

    public static <T> Basic<T> like(String pattern, String field) {
        return new BasicRestriction<>(field, Operator.LIKE, pattern);
    }

    public static <T> Basic<T> not(Object value, String field) {
        return new BasicRestriction<>(field, NOT, Operator.EQUAL, value);
    }

    public static <T> Basic<T> notContains(String substringPattern, String field) {
        return new BasicRestriction<>(field, NOT, Operator.CONTAINS, substringPattern);
    }

    public static <T> Basic<T> notEndsWith(String suffixPattern, String field) {
        return new BasicRestriction<>(field, NOT, Operator.ENDS_WITH, suffixPattern);
    }

    public static <T> Basic<T> notIn(Set<Object> values, String field) {
        return new BasicRestriction<>(field, NOT, Operator.IN, values);
    }

    public static <T> Basic<T> notLike(String pattern, String field) {
        return new BasicRestriction<>(field, NOT, Operator.LIKE, pattern);
    }

    public static <T> Basic<T> notStartsWith(String prefixPattern, String field) {
        return new BasicRestriction<>(field, NOT, Operator.STARTS_WITH, prefixPattern);
    }

    public static <T> Basic<T> startsWith(String prefixPattern, String field) {
        return new BasicRestriction<>(field, Operator.STARTS_WITH, prefixPattern);
    }
}
