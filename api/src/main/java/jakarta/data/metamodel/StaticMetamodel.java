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
 * <p>Annotates a class which serves as a static metamodel for an entity, enabling
 * type-safe access to entity attribute names and related objects such as instances
 * of {@link Sort}s for an attribute. A metamodel class contains one or more
 * {@code public static} fields corresponding to persistent fields of the entity class.
 * The type of each of these fields must be either {@link String}, {@link Attribute},
 * or a subinterface of {@code Attribute} defined in this package.</p>
 *
 * <p>Jakarta Data defines the following conventions for static metamodel classes:</p>
 * <ul>
 * <li>The name of the static metamodel class should consist of underscore ({@code _})
 *     followed by the entity class name.</li>
 * <li>Fields of type {@code String} should be named with all upper case.</li>
 * <li>Fields of type {@code Attribute} should be named in lower case or mixed case.</li>
 * </ul>
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
 * <p>An application programmer may define a static metamodel as follows,</p>
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
 *     public static final {@code SortableAttribute<Person>} ssn = new SortableAttributeRecord&lt;&gt;("ssn");
 *     public static final {@code Attribute<Person>} name = new AttributeRecord&lt;&gt;("name");
 *     public static final {@code TextAttribute<Person>} name_first = new TextAttributeRecord&lt;&gt;("name.first");
 *     public static final {@code TextAttribute<Person>} name_last = new TextAttributeRecord&lt;&gt;("name.last");
 *     public static final {@code SortableAttribute<Person>} yearOfBirth = new SortableAttributeRecord&lt;&gt;("yearOfBirth");
 * }
 * </pre>
 *
 * <p>And use it to refer to entity attributes in a type-safe manner,</p>
 *
 * <pre>
 * Order&lt;Person&gt; order =
 *         Order.by(_Person.yearOfBirth.desc(),
 *                  _Person.name_last.asc(),
 *                  _Person.name_first.asc(),
 *                  _Person.ssn.asc());
 * </pre>
 *
 * <p>Alternatively, an annotation processor might generate static metamodel classes
 * for entities at compile time. The generated classes must be annotated with the
 * {@link jakarta.annotation.Generated @Generated} annotation. The fields may be
 * statically initialized, or they may be initialized by the provider during system
 * initialization. In the first case, the fields are declared {@code final}. In the
 * second case, the fields are declared non-{@code final} and {@code volatile}.</p>
 *
 * <p>In cases where multiple Jakarta Data providers provide repositories for the same
 * entity type, no guarantees are made of the order in which the Jakarta Data providers
 * attempt to initialize the fields of the static metamodel class for that entity.</p>
 *
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
