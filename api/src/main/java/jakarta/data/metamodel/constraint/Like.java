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
package jakarta.data.metamodel.constraint;

public interface Like extends Constraint<String> {
    String string();
    boolean caseSensitive();
    boolean pattern();
    Character escape();
    Like ignoreCase();

    static Like literal(String literal) {
        return new LikeLiteralRecord(literal);
    }

    static Like pattern(String pattern) {
        return new LikePatternRecord(pattern);
    }

    static Like pattern(String pattern, char charWildcard, char stringWildcard) {
        return new LikePatternRecord(pattern, charWildcard, stringWildcard);
    }

    static Like pattern(String pattern, char charWildcard, char stringWildcard, char escape) {
        return new LikePatternRecord(pattern, charWildcard, stringWildcard, escape);
    }

    static Like substring(String substring) {
        return LikePatternRecord.substring(substring);
    }

    static Like prefix(String prefix) {
        return LikePatternRecord.prefix(prefix);
    }

    static Like suffix(String suffix) {
        return LikePatternRecord.suffix(suffix);
    }
}
