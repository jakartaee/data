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
package jakarta.data.restrict;

import java.util.List;

class Unmatchable<T> implements CompositeRestriction<T> {
    static final Unmatchable<?> INSTANCE = new Unmatchable<>();

    // prevent instantiation by others
    private Unmatchable() {
    }

    @Override
    public boolean isNegated() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompositeRestriction<T> negate() {
        return (CompositeRestriction<T>) Unrestricted.INSTANCE;
    }

    @Override
    public List<Restriction<T>> restrictions() {
        return List.of();
    }

    /**
     * Textual representation of the unmatchable restriction.
     *
     * @return textual representation of the unmatchable restriction.
     */
    @Override
    public String toString() {
        return "UNMATCHABLE";
    }

    @Override
    public Type type() {
        return Type.ANY;
    }
}
