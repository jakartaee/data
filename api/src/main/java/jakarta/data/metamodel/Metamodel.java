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


/**
 * Provides access to the metamodel of the entity.
 */
public interface Metamodel {

    /**
     * Returns the metamodel entity type representing the specified entity.
     *
     * @param entity the type of the represented entity
     * @return the metamodel entity type
     * @throws IllegalArgumentException if the specified type is not an entity
     * @throws NullPointerException if the entity is null
     */
    <X> EntityType<X> entity(Class<X> entity);
}