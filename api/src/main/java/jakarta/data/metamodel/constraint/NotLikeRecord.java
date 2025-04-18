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
package jakarta.data.metamodel.constraint;

import java.util.Objects;

import static jakarta.data.metamodel.constraint.LikeRecord.ESCAPE;
import static jakarta.data.metamodel.constraint.LikeRecord.translate;

record NotLikeRecord(String pattern, Character escape) implements NotLike {

    NotLikeRecord {
        Objects.requireNonNull(pattern, "pattern must not be null");
    }

    NotLikeRecord(String pattern) {
        this(pattern, null);
    }

    NotLikeRecord(String pattern, char charWildcard, char stringWildcard) {
        this(pattern, charWildcard, stringWildcard, ESCAPE);
    }

    NotLikeRecord(String pattern, char charWildcard, char stringWildcard, char escape) {
        this(translate(pattern, charWildcard, stringWildcard, escape), escape);
    }

    @Override
    public Like negate() {
        return new LikeRecord(pattern, escape);
    }

    @Override
    public String toString() {
        return "NOT LIKE '" + pattern + "'" +
               (escape == null ? "" : " ESCAPE '\\'");
    }
}
