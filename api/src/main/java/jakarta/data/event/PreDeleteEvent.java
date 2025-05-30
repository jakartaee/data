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
package jakarta.data.event;

/**
 * An event that occurs when a {@link jakarta.data.repository.Delete} lifecycle
 * method is called, but before each record is deleted from the datastore.
 *
 * @param <E> the entity type
 */
public class PreDeleteEvent<E> extends LifecycleEvent<E> {
    public PreDeleteEvent(E entity) {
        super(entity);
    }
}
