/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.data.Sort;

/**
 * <p>Annotates a class to serve as a static metamodel for an entity,
 * enabling type-safe access to entity attribute names and related objects,
 * such as {@link Sort}s for an attribute.</p>
 *
 * <p>For example, for the following entity,</p>
 *
 * <pre>
 * &#64;Entity
 * public class Person {
 *     &#64;Id
 *     public long ssn;
 *
 *     &#64;Embedded
 *     public Name name;
 *
 *     public int yearOfBirth;
 * }
 *
 * &#64;Embeddable
 * public class Name {
 *     public String first;
 *     public String last;
 * }
 * </pre>
 *
 * <p>You can define a static metamodel as follows,</p>
 *
 * <pre>
 * &#64;StaticMetamodel(Person.class)
 * public class _Person {
 *     // These can be uninitialized and non-final if you don't need to access them from annotations.
 *     public static final String SSN = "ssn";
 *     public static final String NAME = "name";
 *     public static final String NAME_FIRST = "name.first";
 *     public static final String NAME_LAST = "name.last";
 *     public static final String YEAROFBIRTH = "yearOfBirth";
 *
 *     public static volatile {@code SortableAttribute<Person>} ssn; // ssn or id
 *     public static volatile Attribute name;
 *     public static volatile {@code TextAttribute<Person>} name_first;
 *     public static volatile {@code TextAttribute<Person>} name_last;
 *     public static volatile {@code SortableAttribute<Person>} yearOfBirth;
 * }
 * </pre>
 *
 * <p>And use it to refer to entity attributes in a type-safe manner,</p>
 *
 * <pre>
 * {@code Pageable<Product>} pageRequest = Order.by(_Person.yearOfBirth.desc(),
 *                                          _Person.name_last.asc(),
 *                                          _Person.name_first.asc(),
 *                                          _Person.ssn.asc())
 *                                      .page(1)
 *                                      .size(20);
 * </pre>
 *
 * <p>When a class is annotated with {@code StaticMetamodel} and the
 * {@link jakarta.annotation.Generated} annotation is not present, Jakarta Data providers
 * that provide a repository for the entity type must assign the value of each field
 * that meets the following criteria:</p>
 *
 * <ul>
 * <li>The field is {@code public}, {@code static}, and not {@code final}.</li>
 * <li>The field type is {@link String}, {@link Attribute}, or an {@code Attribute} subclass
 *     from the {@link jakarta.data.metamodel} package.</li>
 * <li>The name of the field, ignoring case, matches the name of an entity attribute,
 *     where the {@code _} character delimits the attribute names of hierarchical structures
 *     such as embedded classes.</li>
 * <li>The value of the field is uninitialized or {@code null}.</li>
 * </ul>
 *
 * <p>Additionally, a field that meets the above criteria except for the name
 * and is named {@code id} or {@code ID} must be assigned by the Jakarta Data provider for the
 * unique identifier entity attribute if a single entity attribute represents the
 * unique identifier.</p>
 *
 * <p>In cases where multiple Jakarta Data providers provide repositories for the same
 * entity type, no guarantees are made of the order in which the Jakarta Data providers attempt to
 * initialize the fields of the class that is annotated with {@code StaticMetamodel}.
 * It is recommended to include the {@code volatile} modifier on metamodel fields in case the
 * initialize attempt overlaps between multiple providers.</p>
 *
 * <p>You can include a mixture of {@code final} and non-{@code final} fields, in which case
 * the latter are initialized by the Jakarta Data provider and the former are ignored by it.</p>
 *
 * <p>Alternatively, an annotation processor might generate fully implemented
 * static metamodel classes for your entities during compile time. The generated
 * classes must be annotated with the {@link jakarta.annotation.Generated} annotation,
 * which signals the Jakarta Data provider to avoid attempting to initialize any fields in the class
 * at run time.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StaticMetamodel {
    /**
     * An entity class.
     *
     * @return the entity class.
     */
    Class<?> value();
}
