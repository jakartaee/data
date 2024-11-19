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
 * Represents an textual entity attribute in the {@link StaticMetamodel}.
 *
 * @param <T> entity class of the static metamodel.
 */
public interface TextAttribute<T> extends SortableAttribute<T> {

    /**
     * Obtain a request for an ascending, case insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending, case insensitive sort on the entity attribute.
     */
    Sort<T> ascIgnoreCase();

    default Restriction.Text<T> contains(String substring) {
        return Restrict.contains(substring, name());
    }

    /**
     * Obtain a request for a descending, case insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending, case insensitive sort on the entity attribute.
     */
    Sort<T> descIgnoreCase();

    default Restriction.Text<T> endsWith(String suffix) {
        return Restrict.endsWith(suffix, name());
    }

    default Restriction.Text<T> equalTo(String value) {
        return Restrict.equalTo(value, name());
    }

    default Restriction.Text<T> greaterThan(String value) {
        return Restrict.greaterThan(value, name());
    }

    default Restriction.Text<T> greaterThanEqual(String value) {
        return Restrict.greaterThanEqual(value, name());
    }

    default Restriction.Text<T> lessThan(String value) {
        return Restrict.lessThan(value, name());
    }

    default Restriction.Text<T> lessThanEqual(String value) {
        return Restrict.lessThanEqual(value, name());
    }

    // TODO once we have Pattern:
    //default Restriction.Text<T> like(Pattern pattern) {
    //    return Restrict.like(pattern, name());
    //}

    default Restriction.Text<T> like(String pattern) {
        return Restrict.like(pattern, name());
    }

    default Restriction.Text<T> like(String pattern,
                                     char charWildcard,
                                     char stringWildcard) {
        return Restrict.like(pattern, charWildcard, stringWildcard, name());
    }

    default Restriction.Text<T> not(String value) {
        return Restrict.not(value, name());
    }

    default Restriction.Text<T> notContains(String substring) {
        return Restrict.notContains(substring, name());
    }

    default Restriction.Text<T> notEndsWith(String suffix) {
        return Restrict.notEndsWith(suffix, name());
    }

    default Restriction.Text<T> notLike(String pattern) {
        return Restrict.notLike(pattern, name());
    }

    default Restriction.Text<T> notLike(String pattern,
                                        char charWildcard,
                                        char stringWildcard) {
        return Restrict.notLike(pattern, charWildcard, stringWildcard, name());
    }

    default Restriction.Text<T> notStartsWith(String prefix) {
        return Restrict.notStartsWith(prefix, name());
    }

    default Restriction.Text<T> startsWith(String prefix) {
        return Restrict.startsWith(prefix, name());
    }

}
