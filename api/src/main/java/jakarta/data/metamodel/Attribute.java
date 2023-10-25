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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel;

import jakarta.data.Sort;

/**
 * Implemented by the Jakarta Data provider to
 * {@link AttributeMetadata#init(Attribute) initialize} an attribute field
 * in the {@link StaticMetamodel}.
 */
public interface Attribute {
    /**
     * Returns a request for an ascending {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending {@link Sort} based on the entity attribute.
     */
    Sort asc();

    /**
     * Returns a request for an ascending, case-insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending, case-insensitive sort on the entity attribute.
     */
    Sort ascIgnoreCase();

    /**
     * Returns a request for a descending {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending {@link Sort} based on the entity attribute.
     */
    Sort desc();

    /**
     * Returns a request for a descending, case-insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending, case-insensitive sort on the entity attribute.
     */
    Sort descIgnoreCase();

    /**
     * Returns the name of the entity attribute, suitable for use wherever the specification requires
     * an entity attribute name. For example, as the parameter to {@link Sort#asc(String)}.
     *
     * @return the entity attribute name.
     */
    String name();
}
