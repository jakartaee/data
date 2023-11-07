/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.page;

/**
 * Represents keyset values, which can be a starting point for
 * requesting a next or previous page.
 */
public interface Cursor {
    /**
     * Returns whether or not the keyset values of this instance
     * are equal to those of the supplied instance.
     * Cursor implementation classes must also match to be equal.
     *
     * @return true or false.
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns the keyset value at the specified position.
     *
     * @param index position (0 is first) of the keyset value to obtain.
     * @return the keyset value at the specified position.
     * @throws IndexOutOfBoundsException if the index is negative
     *                                   or greater than or equal to the {@link #size}.
     */
    Object getKeysetElement(int index);

    /**
     * Returns a hash code based on the keyset values.
     *
     * @return a hash code based on the keyset values.
     */
    @Override
    int hashCode();

    /**
     * Returns the number of values in the keyset.
     *
     * @return the number of values in the keyset.
     */
    int size();

    /**
     * String representation of the keyset cursor, including the number of
     * key values in the cursor but not the values themselves.
     *
     * @return String representation of the keyset cursor.
     */
    @Override
    String toString();
}
