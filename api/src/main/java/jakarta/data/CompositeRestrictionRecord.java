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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;

import java.util.List;

// Internal implementation class.
// The proper way for users to obtain instances is via
// the Restrict.any(...) or Restrict.all(...) methods

record CompositeRestrictionRecord<T>(
        Type type,
        List<Restriction<T>> restrictions,
        boolean isNegated) implements CompositeRestriction<T> {

    CompositeRestrictionRecord {
        if (restrictions == null || restrictions.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot create a composite restriction without any restrictions to combine.");
        }
    }

    CompositeRestrictionRecord(Type type, List<Restriction<T>> restrictions) {
        this(type, restrictions, false);
    }

    @Override
    public Restriction<T> negate() {
        return new CompositeRestrictionRecord<>(type, restrictions, !isNegated);
    }
}