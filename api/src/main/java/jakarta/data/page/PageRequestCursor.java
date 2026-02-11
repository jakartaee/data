/*
 * Copyright (c) 2022,2026 Contributors to the Eclipse Foundation
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

import jakarta.data.messages.Messages;

/**
 * Built-in implementation of Cursor for cursor-based pagination.
 */
class PageRequestCursor implements PageRequest.Cursor {
    /**
     * Composite key that consists of one or more elements.
     */
    private final Object[] key;

    /**
     * Constructs a cursor with a composite key that is made up of the
     * given elements.
     *
     * @param key elements that together form a composite key.
     * @throws IllegalArgumentException if no elements are provided.
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    PageRequestCursor(Object... key) {
        this.key = key;
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException(
                    Messages.get("006.zero.size.key"));
        }
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null
                && o.getClass() == getClass()
                && Arrays.equals(key, ((PageRequestCursor) o).key);
    }

    @Override
    public Object get(int index) {
        return key[index];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(key);
    }

    @Override
    public int size() {
        return key.length;
    }

    @Override
    public List<?> elements() {
        return List.of(key);
    }

    @Override
    public String toString() {
        return "Cursor@" + Integer.toHexString(hashCode()) +
                " with " + key.length + " values";
    }
}
