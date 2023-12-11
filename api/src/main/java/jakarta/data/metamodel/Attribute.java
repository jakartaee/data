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
 * Represents an entity attribute in the {@link StaticMetamodel}.
 */
public interface Attribute {
    /**
     * Obtain the entity attribute name, suitable for use wherever the specification requires
     * an entity attribute name. For example, as the parameter to {@link Sort#asc(String)}.
     *
     * @return the entity attribute name.
     * @throws MappingException if an entity attribute with this name does not exist or
     *                          if no Jakarta Data provider provides a repository for the entity type.
     */
    String name();

    /**
     * <p>Used by the Jakarta Data provider to initialize this {@code Attribute} with implementation.
     * When initializing a subtype of {@code Attribute} with this method, the Jakarta Data provider
     * must supply an instance of the subtype.</p>
     *
     * <p>The default value of <code>false</code> is appropriate for instances that are
     * pre-initialized, such as those that are provided by a Jakarta Data provider
     * or are generated in advance with an annotation processor.</p>
     *
     * @param attr attribute implementation provided by the Jakarta Data provider.
     * @return true if initialization was successful; false if already initialized.
     */
    default boolean init(Attribute attr) {
        return false;
    }

    /**
     * <p>Obtains a new instance for the Jakarta Data provider to initialize.</p>
     *
     * <p>The Jakarta Data provider automatically initializes the instance when
     * used as the value of a {@code public}, {@code static}, {@code final} field
     * of a class or interface that is annotated with {@link StaticMetamodel},
     * where the field name matches the name of an entity attribute.
     * For example, a {@code Person} entity with attributes {@code id}, {@code emails},
     * {@code phoneNumbers}, and {@code name} could have the following static metamodel,</p>
     *
     * <pre>
     * &#64;StaticMetamodel(Person.class)
     * public interface Person {
     *     SortableAttribute id = SortableAttribute.get();
     *     Attribute emails = Attribute.get();
     *     Attribute phoneNumbers = Attribute.get();
     *     TextAttribute name = TextAttribute.get();
     * }
     * </pre>
     *
     * @return a new instance for an entity attribute.
     */
    public static Attribute get() {
        return new Attribute() {
            // This allows the Jakarta Data provider to initialize the static final
            // Attribute instance with the provider's own implementation of it.
            private final AtomicReference<Attribute> impl = new AtomicReference<Attribute>();

            private final Attribute attr() {
                Attribute attr = impl.get();
                if (attr == null) {
                    throw new MappingException("The static metamodel for this attribute has not been initialized by a Jakarta Data provider.");
                }
                return attr;

            }

            @Override
            public boolean init(Attribute attr) {
                if (attr == null) {
                    throw new NullPointerException();
                } else {
                    return impl.compareAndSet(null, attr);
                }
            }

            @Override
            public String name() {
                return attr().name();
            }

            @Override
            public String toString() {
                Attribute attr = impl.get();
                String attrName = attr == null ? "[uninitialized]" : attr.name();
                String className = getClass().getSimpleName();
                return new StringBuilder(className.length() + 1 + attrName.length())
                        .append(className).append(':').append(attrName)
                        .toString();
            }
        };
    }
}
