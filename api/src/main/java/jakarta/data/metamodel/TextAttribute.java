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

    default Restriction.Basic<T> contains(String substringPattern) {
        return Restrict.contains(substringPattern, name());
    }

    /**
     * Obtain a request for a descending, case insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending, case insensitive sort on the entity attribute.
     */
    Sort<T> descIgnoreCase();

    default Restriction.Basic<T> endsWith(String suffixPattern) {
        return Restrict.endsWith(suffixPattern, name());
    }

    // TODO once we have Pattern:
    //default Restriction.Basic<T> like(Pattern pattern) {
    //    return Restrict.like(pattern, name());
    //}

    default Restriction.Basic<T> like(String pattern) {
        return Restrict.like(pattern, name());
    }

    default Restriction.Basic<T> notContains(String substringPattern) {
        return Restrict.notContains(substringPattern, name());
    }

    default Restriction.Basic<T> notEndsWith(String suffixPattern) {
        return Restrict.notEndsWith(suffixPattern, name());
    }

    default Restriction.Basic<T> notLike(String pattern) {
        return Restrict.notLike(pattern, name());
    }

    default Restriction.Basic<T> notStartsWith(String prefixPattern) {
        return Restrict.notStartsWith(prefixPattern, name());
    }

    default Restriction.Basic<T> startsWith(String prefixPattern) {
        return Restrict.startsWith(prefixPattern, name());
    }

}
