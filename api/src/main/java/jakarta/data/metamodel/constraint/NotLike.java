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

import static jakarta.data.metamodel.constraint.LikeRecord.ESCAPE;
import static jakarta.data.metamodel.constraint.LikeRecord.STRING_WILDCARD;
import static jakarta.data.metamodel.constraint.LikeRecord.translate;

public interface NotLike extends Constraint<String> {

    static NotLike pattern(String pattern) {
        return new NotLikeRecord(pattern, null);
    }

    static NotLike pattern(String pattern, char charWildcard, char stringWildcard) {
        return new NotLikeRecord(translate(pattern, charWildcard, stringWildcard, ESCAPE),
                                 ESCAPE);
    }

    static NotLike pattern(String pattern, char charWildcard, char stringWildcard, char escape) {
        return new NotLikeRecord(translate(pattern, charWildcard, stringWildcard, escape),
                                 escape);
    }

    static NotLike prefix(String prefix) {
        return new NotLikeRecord(LikeRecord.escape(prefix) + STRING_WILDCARD,
                                 ESCAPE);
    }

    static NotLike substring(String substring) {
        return new NotLikeRecord(STRING_WILDCARD + LikeRecord.escape(substring) + STRING_WILDCARD,
                                 ESCAPE);
    }

    static NotLike suffix(String suffix) {
        return new NotLikeRecord(STRING_WILDCARD + LikeRecord.escape(suffix),
                                 ESCAPE);
    }

    Character escape();

    String pattern();
}
