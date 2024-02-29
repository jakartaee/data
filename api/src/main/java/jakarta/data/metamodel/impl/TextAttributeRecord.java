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
package jakarta.data.metamodel.impl;

import jakarta.data.Sort;
import jakarta.data.metamodel.TextAttribute;

/**
 * Record type implementing {@link jakarta.data.metamodel.TextAttribute}.
 * This may be used to simplify implementation of the static metamodel.
 *
 * @param name the name of the attribute
 */
public record TextAttributeRecord<T>(String name)
        implements TextAttribute<T> {
    @Override
    public Sort<T> asc() {
        return Sort.asc(name);
    }

    @Override
    public Sort<T> desc() {
        return Sort.desc(name);
    }

    @Override
    public Sort<T> ascIgnoreCase() {
        return Sort.ascIgnoreCase(name);
    }

    @Override
    public Sort<T> descIgnoreCase() {
        return Sort.descIgnoreCase(name);
    }
}
