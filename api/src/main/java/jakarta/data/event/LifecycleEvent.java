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
 * <p>Abstract supertype of events relating to lifecycle methods.</p>
 * <p>In Jakarta EE, a bean may observe such events via CDI:</p>
 * <pre>
 * void onInsertBook(&#64;Observes PostInsertEvent&lt;Book&gt; bookInsertion) {
 *     Book book = bookInsertion.entity();
 *     ...
 * }
 * </pre>
 *
 * @param <E> the entity type
 */
public abstract class LifecycleEvent<E> {
    private final E entity;

    public LifecycleEvent(E entity) {
        this.entity = entity;
    }

    /**
     * The entity instance which was passed as an argument to
     * the lifecycle method.
     */
    public E entity() {
        return entity;
    }
}
