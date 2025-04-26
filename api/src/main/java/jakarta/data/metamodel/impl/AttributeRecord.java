/*
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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

import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.NumericAttribute;
import jakarta.data.metamodel.TextAttribute;

/**
 * @param name the name of the attribute
 * @deprecated For more complete access to the static metamodel, use the
 * most specific subtype of {@link Attribute} that describes the entity
 * attribute, such as
 * {@link TextAttribute#of(Class, String) TextAttribute} or
 * {@link NumericAttribute#of(Class, String, Class) NumericAttribute}.
 */
@Deprecated(since = "1.1")
public record AttributeRecord<T>(String name)
        implements Attribute<T> {
}

