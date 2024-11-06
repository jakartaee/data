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
package jakarta.data.repository;

import jakarta.data.Operator;
import jakarta.data.Restriction;



/**
 * Represents a pattern-based restriction for matching operations, encapsulating different
 * options such as prefix, suffix, and substring matching. This implementation
 * allows flexibility in creating pattern-based conditions directly as `Restriction` instances.
 *
 * <p>Example usage:</p>
 * <pre>
 * // Case-sensitive exact match
 * Restriction<Book> exactMatch = Pattern.like("title", "Jakarta");
 *
 * Restriction<Book> prefixIgnoreCase = Pattern.prefixedIgnoreCase("title", "Jak");
 *
 * Restriction<Book> suffixMatch = Pattern.suffixed("title", "Data");
 *
 * Restriction<Book> substringIgnoreCase = Pattern.substringedIgnoreCase("title", "Java");
 * </pre>
 *
 * @param <T> the type of the entity on which the restriction is applied.
 */
public record Pattern<T>(String field, String value, boolean ignoreCase) implements Restriction<T> {

    /**
     * The operator for pattern-based restrictions, fixed as `LIKE`.
     *
     * @return the `LIKE` operator.
     */
    @Override
    public Operator operator() {
        return Operator.LIKE;
    }

    /**
     * The value of the pattern used for matching.
     *
     * @return the pattern value.
     */
    @Override
    public String value() {
        return value;
    }

    /**
     * Creates a case-sensitive pattern match for an exact match.
     *
     * @param field the field to apply the pattern on.
     * @param value the value to match exactly.
     * @return a Pattern for an exact match.
     */
    public static <T> Pattern<T> like(String field, String value) {
        return new Pattern<>(field, value, false);
    }

    /**
     * Creates a case-insensitive pattern match for an exact match.
     *
     * @param field the field to apply the pattern on.
     * @param value the value to match exactly.
     * @return a Pattern for a case-insensitive exact match.
     */
    public static <T> Pattern<T> likeIgnoreCase(String field, String value) {
        return new Pattern<>(field, value, true);
    }

    /**
     * Creates a pattern match for values prefixed with the specified value.
     *
     * @param field the field to apply the pattern on.
     * @param value the prefix to match.
     * @return a Pattern for prefix matching.
     */
    public static <T> Pattern<T> prefixed(String field, String value) {
        return new Pattern<>(field, value + "%", false);
    }

    /**
     * Creates a case-insensitive pattern match for values prefixed with the specified value.
     *
     * @param field the field to apply the pattern on.
     * @param value the prefix to match.
     * @return a Pattern for case-insensitive prefix matching.
     */
    public static <T> Pattern<T> prefixedIgnoreCase(String field, String value) {
        return new Pattern<>(field, value + "%", true);
    }

    /**
     * Creates a pattern match for values containing the specified substring.
     *
     * @param field the field to apply the pattern on.
     * @param value the substring to match.
     * @return a Pattern for substring matching.
     */
    public static <T> Pattern<T> substringed(String field, String value) {
        return new Pattern<>(field, "%" + value + "%", false);
    }

    /**
     * Creates a case-insensitive pattern match for values containing the specified substring.
     *
     * @param field the field to apply the pattern on.
     * @param value the substring to match.
     * @return a Pattern for case-insensitive substring matching.
     */
    public static <T> Pattern<T> substringedIgnoreCase(String field, String value) {
        return new Pattern<>(field, "%" + value + "%", true);
    }

    /**
     * Creates a pattern match for values suffixed with the specified value.
     *
     * @param field the field to apply the pattern on.
     * @param value the suffix to match.
     * @return a Pattern for suffix matching.
     */
    public static <T> Pattern<T> suffixed(String field, String value) {
        return new Pattern<>(field, "%" + value, false);
    }

    /**
     * Creates a case-insensitive pattern match for values suffixed with the specified value.
     *
     * @param field the field to apply the pattern on.
     * @param value the suffix to match.
     * @return a Pattern for case-insensitive suffix matching.
     */
    public static <T> Pattern<T> suffixedIgnoreCase(String field, String value) {
        return new Pattern<>(field, "%" + value, true);
    }
}
