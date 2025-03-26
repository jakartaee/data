/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package jakarta.data.repository;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Determines the attribute or attributes of an entity which are returned
 * by a {@linkplain Find parameter-based} or {@linkplain Query annotated}
 * query method.</p>
 *
 * <p>This annotation may be applied to a repository method annotated
 * {@code @Find} or to a component of a record type.</p>
 *
 * <ul>
 * <li>At most one {@code @Select} annotation may be applied to a repository
 * method that returns an entity attribute type.</li>
 * <li>Multiple {@code @Select} annotations may be applied to a repository
 * method which returns a record type, one annotation for each component of
 * the record type. The number and order of {@code @Select} annotations must
 * match the number and order of record components.</li>
 * <li>At most one {@code @Select} annotation may be applied to a component
 * of a record type. When applied to a record component, the {@code Select}
 * annotation affects every {@link Find @Find} method which returns the
 * record type and which does not have a {@code @Select} annotation, and
 * every {@link Query @Query} method which returns the record type and does
 * not have an explicit {@code SELECT} clause.</li>
 * </ul>
 *
 * <p>This annotation must not be used in other locations.</p>
 *
 * @see Find
 */
@Documented
@Repeatable(Select.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.RECORD_COMPONENT })
public @interface Select {
    /**
     * <p>Name of an entity attribute that has a single-valued
     * basic type. Multiple-valued types such as collections, arrays, and
     * associations cannot be retrieved independently of the entity.</p>
     *
     * <p>The {@code Select} annotation can be used with repository methods
     * that return a single entity attribute or multiple entity attributes.</p>
     *
     * <h2>Method that returns Single Entity Attributes</h2>
     *
     * <p>Place the {@code Select} annotation on a repository find method and
     * assign the annotation value to be the name of a single entity attribute.
     * The result type that is used in the method return type must be the
     * type of the entity attribute.</p>
     *
     * <p>For example, to return only the {@code price} attribute of the
     * {@code Car} entity that has the supplied {@code vin} attribute value,</p>
     *
     * <pre>
     * &#64;Repository
     * public interface Cars extends BasicRepository&lt;Car, String&gt; {
     *     &#64;Find
     *     &#64;Select(_Car.PRICE)
     *     Optional&lt;Float&gt; getPrice(@By(_Car.VIN) String vehicleIdNum);
     * }
     * </pre>
     *
     * <h2>Method that returns Java Records</h2>
     *
     * <p>A repository method can return a subset of entity attributes per result
     * by having the result type be a Java record. The {@code Select} annotation
     * can be used in the following ways to accommodate this.</p>
     *
     * <h3>Annotating a Repository Method</h3>
     *
     * <p>Place one or more {@code Select} annotations on a repository find method
     * and assign the annotation values to be the names of entity attributes,
     * corresponding to the order and types of the components of the Java record
     * that is used for the result type.</p>
     *
     * <p>For example, to return only the {@code model}, {@code make}, and
     * {@code year} attributes of a {@code Car} entity that has the supplied
     * {@code vin} attribute value,</p>
     *
     * <pre>
     * &#64;Repository
     * public interface Cars extends BasicRepository&lt;Car, String&gt; {
     *     record ModelInfo(String model,
     *                      String manufacturer,
     *                      int designYear) {}
     *
     *     &#64;Find
     *     &#64;Select(_Car.MODEL)
     *     &#64;Select(_Car.MAKE)
     *     &#64;Select(_Car.YEAR)
     *     Optional&lt;ModelInfo&gt; getModelInfo(@By(_Car.VIN) String vehicleIdNum);
     * }
     * </pre>
     *
     * <h3>Annotating a Record Component</h3>
     *
     * <p>Place the {@code Select} annotation on each record component of the
     * Java record that is used as the result type of the repository method.
     * Assign the annotation value to be the name of an entity attribute that
     * has the same type as the record component.</p>
     *
     * <pre>
     * &#64;Repository
     * public interface Cars extends BasicRepository&lt;Car, String&gt; {
     *     record ModelInfo(&#64;Select(_Car.MODEL) String model,
     *                      &#64;Select(_Car.MAKE) String manufacturer,
     *                      &#64;Select(_Car.YEAR) int designYear) {}
     *
     *     &#64;Find
     *     Optional&lt;ModelInfo&gt; getModelInfo(@By(_Car.VIN) String vehicleIdNum);
     * }
     * </pre>
     *
     * <p>For more concise code, the {@code Select} annotation can be omitted from
     * record components that have the same name as the entity attribute name,
     * such as {@code model} in the above example.</p>
     *
     * <p>The examples above use the
     * {@linkplain jakarta.data/jakarta.data.metamodel static metamodel},
     * to avoid hard coding String values for the entity attribute names.</p>
     */
    String value();

    /**
     * Enables multiple {@link Select} annotations on a repository {@link Find}
     * method that returns a Java record type. Multiple {@code Select} annotations
     * must not be used on any other types of method.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD) // RECORD_COMPONENT is intentionally omitted
    @interface List {
        /**
         * Returns a list of annotations with the first corresponding to the first
         * record component, the second to the second record component, and so forth.
         *
         * @return list of annotations.
         */
        Select[] value();
    }
}
