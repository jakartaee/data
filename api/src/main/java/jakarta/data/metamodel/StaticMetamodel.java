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
 * public class Person_ {
 *     public static final SortableAttribute ssn = SortableAttribute.get(); // ssn or id
 *     public static final Attribute name = Attribute.get();
 *     public static final TextAttribute name_first = TextAttribute.get();
 *     public static final TextAttribute name_last = TextAttribute.get();
 *     public static final SortableAttribute yearOfBirth = SortableAttribute.get();
 * }
 * </pre>
 *
 * <p>And use it to refer to entity attributes in a type-safe manner,</p>
 *
 * <pre>
 * pageRequest = Pageable.ofSize(20).sortBy(Person_.yearOfBirth.desc(),
 *                                          Person_.name_last.asc(),
 *                                          Person_.name_first.asc(),
 *                                          Person_.ssn.asc());
 * </pre>
 *
 * <p>When a class is annotated with {@code StaticMetamodel} and the
 * {@link jakarta.annotation.Generated} annotation is not present, Jakarta Data providers
 * that provide a repository for the entity type must assign the value of each field
 * that meets the following criteria:</p>
 *
 * <ul>
 * <li>The field type is {@link Attribute} or a subclass of it
 *     from the {@link jakarta.data.metamodel} package.</li>
 * <li>The field is {@code public}.</li>
 * <li>The field is {@code static}.</li>
 * <li>The field is {@code final}.</li>
 * <li>The name of the field, ignoring case, matches the name of an entity attribute,
 * where the {@code _} character delimits the attribute names of hierarchical structures
 * such as embedded classes.</li>
 * </ul>
 *
 * <p>The Jakarta Data provider must {@link Attribute#init(Attribute) initialize}
 * each {@code Attribute} value that corresponds to the name of an entity attribute.
 * When {@code Attribute} subclasses are used, the value must be initialized with the
 * same type as the subclass.</p>
 *
 * <p>Additionally, a field that meets the above criteria except for the name
 * and is named {@code id} must be assigned by the Jakarta Data provider to the
 * unique identifier entity attribute if a single entity attribute represents the
 * unique identifier.</p>
 *
 * <p>In cases where multiple Jakarta Data providers provide repositories for the same
 * entity type, no guarantees are made of the order in which the Jakarta Data providers
 * initialize the {@code Attribute} fields of the class that is annotated with
 * {@code StaticMetamodel}.</p>
 *
 * <p>Alternatively, an annotation processor might generate fully implemented
 * static metamodel classes for your entities during compile time. The generated
 * classes must be annotated with the {@link jakarta.annotation.Generated} annotation,
 * which signals the Jakarta Data provider to avoid initializing the classes at run time.</p>
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
