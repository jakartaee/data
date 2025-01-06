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
package jakarta.data;

/**
 * Represents a restriction on text-based fields in an entity, providing specialized operations
 * for handling case sensitivity and escaping. This interface is immutable, ensuring that
 * modifications return new instances rather than altering the existing restriction.
 * It brings with additional functionality
 * tailored for text-based fields, such as enabling case-insensitive comparisons and handling
 * escape characters. These restrictions are commonly used for querying string fields, like names
 * or descriptions, in entity classes.
 * Example usage:
 * <pre>{@code
 * TextRestriction<Person> restriction = ...
 * TextRestriction<Person> caseInsensitiveRestriction = restriction.ignoreCase(); // Creates a new instance with case-insensitivity enabled
 * boolean isCaseSensitive = restriction.isCaseSensitive(); // Check case sensitivity of the original restriction
 * }</pre>
 * Immutability ensures thread safety and predictable behavior. Any operation that modifies the
 * state of a restriction, such as enabling case-insensitivity or negating the condition, will return
 * a new {@code TextRestriction} instance without altering the original.
 *
 * @param <T> the entity type being restricted
 */
public interface TextRestriction<T> extends BasicRestriction<T> {

    /**
     * Returns a new {@code TextRestriction} instance with case-insensitive comparisons enabled.
     * Note: If the underlying database does not support case-insensitive comparisons,
     * this setting may be ignored during query execution.
     * TODO we need to define and verify the behavior of case-insensitive comparisons when the database does not support it.
     * @return a new {@code TextRestriction} instance with case-insensitivity enabled
     */
    TextRestriction<T> ignoreCase();

    /**
     * Checks whether case-sensitive comparisons are enabled for this restriction.
     * A return value of {@code true} indicates that comparisons are case-sensitive. If case-insensitive
     * comparisons are enabled (e.g., via {@link #ignoreCase()}), this method returns {@code false}.
     *
     * @return {@code true} if case-sensitive comparisons are enabled, otherwise {@code false}
     */
    boolean isCaseSensitive();

    /**
     * Checks whether the restriction is escaped.
     * Escaping is used to handle special characters in text fields, such as wildcards
     * or reserved characters in query syntax. If escaping is enabled, the value
     * associated with this restriction will be treated literally.
     *
     * @return {@code true} if escaping is enabled, otherwise {@code false}
     */
    boolean isEscaped();

    /**
     * Returns a new {@code TextRestriction} instance with the negated condition.
     * Negating a restriction inverts its logic while retaining the field and other settings.
     * For example:
     * <ul>
     *   <li>A restriction like {@code name = "John"} becomes {@code name != "John"}</li>
     *   <li>A restriction like {@code name LIKE "J%"} becomes {@code name NOT LIKE "J%"}</li>
     * </ul>
     *
     * @return a new negated {@code TextRestriction} instance
     */
    @Override
    TextRestriction<T> negate();

    /**
     * Returns the value associated with this text restriction.
     * The value represents the text used in the comparison, such as the string
     * being matched or filtered. For example, in a restriction like {@code name = "John"},
     * the value would be "John."
     *
     * @return the text value being restricted
     */
    @Override
    String value();
}
