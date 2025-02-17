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
package jakarta.data.metamodel.range;

import jakarta.data.metamodel.range.impl.Literal;
import jakarta.data.metamodel.range.impl.Pattern;

public sealed interface TextRange extends Range<String>
        permits Pattern, Literal {
    String pattern();
    boolean caseSensitive();
    TextRange ignoreCase();

    static TextRange literal(String literal) {
        return new Literal(literal);
    }

    static TextRange pattern(String pattern) {
        return new Pattern(pattern);
    }

    static TextRange pattern(String pattern, char charWildcard, char stringWildcard) {
        return new Pattern(pattern, charWildcard, stringWildcard);
    }

    static TextRange substring(String substring) {
        return Pattern.substring(substring);
    }

    static TextRange prefix(String substring) {
        return Pattern.prefix(substring);
    }

    static TextRange suffix(String substring) {
        return Pattern.suffix(substring);
    }
}
