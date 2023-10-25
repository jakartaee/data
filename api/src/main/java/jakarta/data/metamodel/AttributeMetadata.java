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
public class AttributeMetadata {
    // This allows the Jakarta Data provider to initialize the static final
    // Attribute instance with the provider's own implementation of it.
    private final AtomicReference<Attribute> info = new AtomicReference<Attribute>();

    private AttributeMetadata() {
    }

    /**
     * Obtain a request for an ascending {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending sort on the entity attribute.
     * @throws MappingException if an entity attribute with this name does not exist or
     *                          if no Jakarta Data provider provides a repository for the entity type.
     */
    public final Sort asc() {
        return attrInfo().asc();
    }

    /**
     * Obtain a request for an ascending, case insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending, case insensitive sort on the entity attribute.
     * @throws MappingException if an entity attribute with this name does not exist or
     *                          if no Jakarta Data provider provides a repository for the entity type.
     */
    public final Sort ascIgnoreCase() {
        return attrInfo().ascIgnoreCase();
    }

    private final Attribute attrInfo() {
        Attribute attrInfo = info.get();
        if (attrInfo == null)
            throw new MappingException("Unable to find a Jakarta Data provider that provides a repository " +
                                       " for an entity with an attribute having this name.");
        return attrInfo;
    }

    /**
     * Obtain a request for a descending {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending sort on the entity attribute.
     * @throws MappingException if an entity attribute with this name does not exist or
     *                          if no Jakarta Data provider provides a repository for the entity type.
     */
    public final Sort desc() {
        return attrInfo().desc();
    }

    /**
     * Obtain a request for a descending, case insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending, case insensitive sort on the entity attribute.
     * @throws MappingException if an entity attribute with this name does not exist or
     *                          if no Jakarta Data provider provides a repository for the entity type.
     */
    public final Sort descIgnoreCase() {
        return attrInfo().descIgnoreCase();
    }

    /**
     * <p>Obtains a new instance for the Jakarta Data provider to initialize.</p>
     *
     * <p>The Jakarta Data provider automatically initializes the instance when
     * used as the value of a {@code public}, {@code static}, {@code final} field
     * of a class that is annotated with {@link StaticMetamodel},
     * where the field name matches the name of an entity attribute.
     * For example, a {@code Vehicle} entity with attributes {@code vin}, {@code make},
     * {@code model}, and {@code year} could have the following static metamodel,</p>
     *
     * <pre>
     * &#64;StaticMetamodel(Vehicle.class)
     * public class Vehicle_ {
     *     public static final Attribute vin = Attribute.get();
     *     public static final Attribute make = Attribute.get();
     *     public static final Attribute model = Attribute.get();
     *     public static final Attribute year = Attribute.get();
     * }
     * </pre>
     *
     * @return a new instance for an entity attribute.
     */
    public static final AttributeMetadata get() {
        return new AttributeMetadata();
    }

    /**
     * Used by the Jakarta Data provider to initialize this {@code Attribute} with implementation.
     *
     * @param attrInfo attribute information provided by the Jakarta Data provider.
     * @return true if initialization was successful; false if previously initialized.
     */
    public final boolean init(Attribute attrInfo) {
        return info.compareAndSet(null, attrInfo);
    }

    /**
     * Obtain the entity attribute name, suitable for use wherever the specification requires
     * an entity attribute name. For example, as the parameter to {@link Sort#asc(String)}.
     *
     * @return the entity attribute name.
     * @throws MappingException if an entity attribute with this name does not exist or
     *                          if no Jakarta Data provider provides a repository for the entity type.
     */
    public final String name() {
        return attrInfo().name();
    }
}
