/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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

import java.util.Arrays;
import java.util.List;

/**
 * Built-in implementation of Cursor for key-based pagination.
 */
class PageRequestCursor implements PageRequest.Cursor {
    /**
     * Key values.
     */
    private final Object[] keyset;

    /**
     * Constructs a cursor with the specified key values.
     *
     * @param keyset key values.
     * @throws IllegalArgumentException if no key values are provided.
     */
    PageRequestCursor(Object... keyset) {
        this.keyset = keyset;
        if (keyset == null || keyset.length == 0)
            throw new IllegalArgumentException("No keyset values were provided.");
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null
                && o.getClass() == getClass()
                && Arrays.equals(keyset, ((PageRequestCursor) o).keyset);
    }

    public Object getKeysetElement(int index) {
        return keyset[index];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(keyset);
    }

    public int size() {
        return keyset.length;
    }

    @Override
    public List<?> elements() {
        return List.of(keyset);
    }

    @Override
    public String toString() {
        return new StringBuilder(27).append("Cursor@").append(Integer.toHexString(hashCode()))
                        .append(" with ").append(keyset.length).append(" keys")
                        .toString();
    }
}
