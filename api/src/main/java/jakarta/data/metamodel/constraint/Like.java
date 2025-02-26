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
    String pattern();
    Character escape();

    static Like pattern(String pattern) {
        return new LikeRecord(pattern);
    }

    static Like pattern(String pattern, char charWildcard, char stringWildcard) {
        return new LikeRecord(pattern, charWildcard, stringWildcard);
    }

    static Like pattern(String pattern, char charWildcard, char stringWildcard, char escape) {
        return new LikeRecord(pattern, charWildcard, stringWildcard, escape);
    }

    static Like substring(String substring) {
        return LikeRecord.substring(substring);
    }

    static Like prefix(String prefix) {
        return LikeRecord.prefix(prefix);
    }

    static Like suffix(String suffix) {
        return LikeRecord.suffix(suffix);
    }
}
