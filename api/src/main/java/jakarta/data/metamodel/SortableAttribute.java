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

import java.util.concurrent.atomic.AtomicReference;

import jakarta.data.Sort;
import jakarta.data.exceptions.MappingException;

/**
 * Represents a sortable entity attribute in the {@link StaticMetamodel}.
 * Entity attribute types that are sortable include:
 *
 * <ul>
 * <li>numeric attributes</li>
 * <li>enum attributes</li>
 * <li>time attributes</li>
 * <li>boolean attributes</li>
 * <li>{@link TextAttribute textual attributes}</li>
 * </ul>
 */
public interface SortableAttribute extends Attribute {

    /**
     * Obtain a request for an ascending {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending sort on the entity attribute.
     * @throws MappingException if an entity attribute with this name does not exist or
     *                          if no Jakarta Data provider provides a repository for the entity type.
     */
    Sort asc();

    /**
     * Obtain a request for a descending {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending sort on the entity attribute.
     * @throws MappingException if an entity attribute with this name does not exist or
     *                          if no Jakarta Data provider provides a repository for the entity type.
     */
    Sort desc();

    /**
     * <p>Obtains a new instance for the Jakarta Data provider to initialize.</p>
     *
     * <p>The Jakarta Data provider automatically initializes the instance when
     * used as the value of a {@code public}, {@code static}, {@code final} field
     * of a class or interface that is annotated with {@link StaticMetamodel},
     * where the field name matches the name of an entity attribute.
     * For example, a {@code Product} entity with attributes {@code id}, {@code price},
     * {@code quantity}, and {@code name} could have the following static metamodel,</p>
     *
     * <pre>
     * &#64;StaticMetamodel(Product.class)
     * public interface Product_ {
     *     SortableAttribute id = SortableAttribute.get();
     *     SortableAttribute price = SortableAttribute.get();
     *     SortableAttribute quantity = SortableAttribute.get();
     *     TextAttribute name = TextAttribute.get();
     * }
     * </pre>
     *
     * @return a new instance for an entity attribute.
     */
    public static SortableAttribute get() {
        return new SortableAttribute() {
            // This allows the Jakarta Data provider to initialize the static final
            // Attribute instance with the provider's own implementation of it.
            private final AtomicReference<SortableAttribute> impl = new AtomicReference<SortableAttribute>();

            private final SortableAttribute attr() {
                SortableAttribute attr = impl.get();
                if (attr == null) {
                    throw new MappingException("The static metamodel for this attribute has not been initialized by a Jakarta Data provider.");
                }
                return attr;

            }

            @Override
            public Sort asc() {
                return attr().asc();
            }

            @Override
            public Sort desc() {
                return attr().desc();
            }

            @Override
            public boolean init(Attribute attr) {
                if (attr instanceof SortableAttribute) {
                    return impl.compareAndSet(null, (SortableAttribute) attr);
                } else if (attr == null) {
                    throw new NullPointerException();
                } else {
                    throw new IllegalArgumentException(attr.getClass().getName() + " is not an instance of " +
                                                       getClass().getName());
                }
            }

            @Override
            public String name() {
                return attr().name();
            }

            @Override
            public String toString() {
                SortableAttribute attr = impl.get();
                String attrName = attr == null ? "[uninitialized]" : attr.name();
                String className = getClass().getSimpleName();
                return new StringBuilder(className.length() + 1 + attrName.length())
                                .append(className).append(':').append(attrName)
                                .toString();
            }
        };
    }
}
