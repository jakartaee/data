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

    /**
     * Obtain a request for a descending, case insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending, case insensitive sort on the entity attribute.
     */
    Sort<T> descIgnoreCase();

    /**
     * Creates a `LIKE` restriction for a match on the specified text.
     *
     * @param text the text to match.
     * @return a Restriction representing a `LIKE` condition.
     */
    default Pattern<T> like(String text) {
        return Pattern.like(name(), text);
    }

    /**
     * Creates a `LIKE` restriction for a match on the specified text.
     *
     * @param pattern the text to match.
     * @return a Restriction representing a `LIKE` condition.
     */
    default Pattern<T> like(Pattern<T> pattern) {
        return new Pattern<>(name(), pattern.value(), pattern.ignoreCase());
    }

    /**
     * Creates a `LIKE` restriction for values that start with the specified text.
     *
     * @param text the text prefix to match.
     * @return a Restriction representing a prefix `LIKE` condition.
     */
    default Pattern<T> startsWith(String text) {
        return Pattern.prefixed(name(), text);
    }

    /**
     * Creates a `LIKE` restriction for values that contain the specified substring.
     *
     * @param text the substring to match.
     * @return a Restriction representing a substring `LIKE` condition.
     */
    default Pattern<T> contains(String text) {
        return Pattern.contains(name(), text);
    }


    /**
     * Creates a `LIKE` restriction for values that end with the specified text.
     *
     * @param text the text suffix to match.
     * @return a Restriction representing a suffix `LIKE` condition.
     */
    default Pattern<T> endsWith(String text) {
        return Pattern.endsWith(name(), text);
    }

}
