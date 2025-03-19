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

import java.util.Objects;

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
import jakarta.data.metamodel.impl.TextAttributeRecord;
import jakarta.data.metamodel.restrict.BasicRestriction;

/**
 * Represents an textual entity attribute in the {@link StaticMetamodel}.
 *
 * @param <T> entity class of the static metamodel.
 */
public interface TextAttribute<T> extends ComparableAttribute<T,String>, TextExpression<T> {

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
    default BasicRestriction<T,String> between(String min, String max) {
        return new BasicRestrictionRecord<>(name(), Between.bounds(min, max));
    }

    @Override
    default BasicRestriction<T,String> equalTo(String value) {
        return new BasicRestrictionRecord<>(name(), EqualTo.value(value));
    }

    @Override
    default BasicRestriction<T,String> greaterThan(String value) {
        return new BasicRestrictionRecord<>(name(), GreaterThan.bound(value));
    }

    @Override
    default BasicRestriction<T,String> greaterThanEqual(String value) {
        return new BasicRestrictionRecord<>(name(), GreaterThanOrEqual.min(value));
    }

    @Override
    default BasicRestriction<T,String> lessThan(String value) {
        return new BasicRestrictionRecord<>(name(), LessThan.bound(value));
    }

    @Override
    default BasicRestriction<T,String> lessThanEqual(String value) {
        return new BasicRestrictionRecord<>(name(), LessThanOrEqual.max(value));
    }

    @Override
    default BasicRestriction<T,String> notBetween(String min, String max) {
        return new BasicRestrictionRecord<>(name(), NotBetween.bounds(min, max));
    }

    @Override
    default BasicRestriction<T,String> notEqualTo(String value) {
        return new BasicRestrictionRecord<>(name(), NotEqualTo.value(value));
    }

    @Override
    default BasicRestriction<T,String> like(Like pattern) {
        return new BasicRestrictionRecord<>(name(), pattern);

    }

    @Override
    default BasicRestriction<T,String> like(String pattern) {
        return new BasicRestrictionRecord<>(name(), Like.pattern(pattern));
    }

    @Override
    default BasicRestriction<T,String> like(String pattern, char charWildcard, char stringWildcard) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard);
        return new BasicRestrictionRecord<>(name(), constraint);
    }

    @Override
    default BasicRestriction<T,String> like(String pattern, char charWildcard, char stringWildcard, char escape) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard, escape);
        return new BasicRestrictionRecord<>(name(), constraint);
    }

    @Override
    default BasicRestriction<T,String> notLike(String pattern) {
        return new BasicRestrictionRecord<>(name(), NotLike.pattern(pattern));
    }

    @Override
    default BasicRestriction<T,String> notLike(String pattern, char charWildcard, char stringWildcard) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard);
        return new BasicRestrictionRecord<>(name(), constraint);
    }

    @Override
    default BasicRestriction<T,String> notLike(String pattern, char charWildcard, char stringWildcard, char escape) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard, escape);
        return new BasicRestrictionRecord<>(name(), constraint);
    }

    @Override
    default BasicRestriction<T,String> contains(String substring) {
        return new BasicRestrictionRecord<>(name(), Like.substring(substring));
    }

    @Override
    default BasicRestriction<T,String> notContains(String substring) {
        return new BasicRestrictionRecord<>(name(), NotLike.substring(substring));
    }

    @Override
    default BasicRestriction<T,String> endsWith(String suffix) {
        return new BasicRestrictionRecord<>(name(), Like.suffix(suffix));
    }

    @Override
    default BasicRestriction<T,String> notEndsWith(String suffix) {
        return new BasicRestrictionRecord<>(name(), NotLike.suffix(suffix));
    }

    /**
     * <p>Creates a static metamodel {@code TextAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T> entity class of the static metamodel.
     * @param entityClass the entity class.
     * @param name        the name of the entity attribute.
     * @return instance of {@code TextAttribute}.
     */
    static <T> TextAttribute<T> of(Class<T> entityClass, String name) {
        Objects.requireNonNull(entityClass, "entity class is required");
        Objects.requireNonNull(name, "entity attribute name is required");

        return new TextAttributeRecord<>(name);
    }

    default BasicRestriction<T,String> startsWith(String prefix) {
        return new BasicRestrictionRecord<>(name(), Like.prefix(prefix));
    }

    default BasicRestriction<T,String> notStartsWith(String prefix) {
        return new BasicRestrictionRecord<>(name(), NotLike.prefix(prefix));
    }

}
