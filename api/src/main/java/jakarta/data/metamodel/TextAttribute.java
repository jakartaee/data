/*
 * Copyright (c) 2023,2025 Contributors to the Eclipse Foundation
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

import jakarta.data.Sort;
import jakarta.data.metamodel.constraint.Between;
import jakarta.data.metamodel.constraint.EqualTo;
import jakarta.data.metamodel.constraint.GreaterThan;
import jakarta.data.metamodel.constraint.GreaterThanOrEqual;
import jakarta.data.metamodel.constraint.LessThan;
import jakarta.data.metamodel.constraint.LessThanOrEqual;
import jakarta.data.metamodel.constraint.Like;
import jakarta.data.metamodel.constraint.NotBetween;
import jakarta.data.metamodel.constraint.NotEqualTo;
import jakarta.data.metamodel.constraint.NotLike;
import jakarta.data.metamodel.restrict.TextRestriction;

/**
 * Represents an textual entity attribute in the {@link StaticMetamodel}.
 *
 * @param <T> entity class of the static metamodel.
 */
public interface TextAttribute<T> extends ComparableAttribute<T,String> {

    /**
     * Obtain a request for an ascending, case-insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending, case-insensitive sort on the entity attribute.
     */
    Sort<T> ascIgnoreCase();

    /**
     * Obtain a request for a descending, case insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending, case insensitive sort on the entity attribute.
     */
    Sort<T> descIgnoreCase();

    @Override
    default TextRestriction<T> between(String min, String max) {
        return new TextRestrictionRecord<>(name(), Between.bounds(min, max));
    }

    @Override
    default TextRestriction<T> equalTo(String value) {
        return new TextRestrictionRecord<>(name(), EqualTo.value(value));
    }

    @Override
    default TextRestriction<T> greaterThan(String value) {
        return new TextRestrictionRecord<>(name(), GreaterThan.bound(value));
    }

    @Override
    default TextRestriction<T> greaterThanEqual(String value) {
        return new TextRestrictionRecord<>(name(), GreaterThanOrEqual.min(value));
    }

    @Override
    default TextRestriction<T> lessThan(String value) {
        return new TextRestrictionRecord<>(name(), LessThan.bound(value));
    }

    @Override
    default TextRestriction<T> lessThanEqual(String value) {
        return new TextRestrictionRecord<>(name(), LessThanOrEqual.max(value));
    }

    @Override
    default TextRestriction<T> notBetween(String min, String max) {
        return new TextRestrictionRecord<>(name(), NotBetween.bounds(min, max));
    }

    @Override
    default TextRestriction<T> notEqualTo(String value) {
        return new TextRestrictionRecord<>(name(), NotEqualTo.value(value));
    }

    default TextRestriction<T> like(Like pattern) {
        return new TextRestrictionRecord<>(name(), pattern);

    }

    default TextRestriction<T> like(String pattern) {
        return new TextRestrictionRecord<>(name(), Like.pattern(pattern));
    }

    default TextRestriction<T> like(String pattern, char charWildcard, char stringWildcard) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard);
        return new TextRestrictionRecord<>(name(), constraint);
    }

    default TextRestriction<T> like(String pattern, char charWildcard, char stringWildcard, char escape) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard, escape);
        return new TextRestrictionRecord<>(name(), constraint);
    }

    default TextRestriction<T> notLike(String pattern) {
        return new TextRestrictionRecord<>(name(), NotLike.pattern(pattern));
    }

    default TextRestriction<T> notLike(String pattern, char charWildcard, char stringWildcard) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard);
        return new TextRestrictionRecord<>(name(), constraint);
    }

    default TextRestriction<T> notLike(String pattern, char charWildcard, char stringWildcard, char escape) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard, escape);
        return new TextRestrictionRecord<>(name(), constraint);
    }

    default TextRestriction<T> contains(String substring) {
        return new TextRestrictionRecord<>(name(), Like.substring(substring));
    }

    default TextRestriction<T> notContains(String substring) {
        return new TextRestrictionRecord<>(name(), NotLike.substring(substring));
    }

    default TextRestriction<T> endsWith(String suffix) {
        return new TextRestrictionRecord<>(name(), Like.suffix(suffix));
    }

    default TextRestriction<T> notEndsWith(String suffix) {
        return new TextRestrictionRecord<>(name(), NotLike.suffix(suffix));
    }

    default TextRestriction<T> startsWith(String prefix) {
        return new TextRestrictionRecord<>(name(), Like.prefix(prefix));
    }

    default TextRestriction<T> notStartsWith(String prefix) {
        return new TextRestrictionRecord<>(name(), NotLike.prefix(prefix));
    }

}
