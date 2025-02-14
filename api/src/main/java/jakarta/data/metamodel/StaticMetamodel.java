/*
 * Copyright (c) 2023,2025 Contributors to the Eclipse Foundation
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
 * {@code public static} fields corresponding to attributes of the entity class.
 * The type of each of these fields must be either {@link String}, {@link Attribute},
 * or a subinterface of {@code Attribute} defined in this package.</p>
 *
 * <p>The following subinterfaces of {@code Attribute} are recommended to obtain
 * the full benefit of the static metamodel:</p>
 * <ul>
 * <li>{@link TextAttribute} for entity attributes that represent text,
 *     typically of type {@link String}.</li>
 * <li>{@link ComparableAttribute} for entity attributes that represent other
 *     sortable and comparable values, such as {@code int}, {@link Double},
 *     {@code boolean}, {@link java.time.LocalDateTime}, and enumerations.</li>
 * <li>{@link BasicAttribute} for other types of entity attributes, such as
 *     collections, embeddables, and other relation attributes.</li>
 * </ul>
 *
 * <p>Jakarta Data defines the following conventions for static metamodel classes:</p>
 * <ul>
 * <li>The metamodel class can be an interface or concrete class.</li>
 * <li>The name of the static metamodel class should consist of underscore ({@code _})
 *     followed by the entity class name.</li>
 * <li>Fields of type {@code String} should be named with all upper case.</li>
 * <li>Fields that are subtypes of {@code Attribute} should be named in lower case
 *     or mixed case.</li>
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
 * public interface _Person {
 *     String SSN = "ssn";
 *     String NAME = "name";
 *     String NAME_FIRST = "name.first";
 *     String NAME_LAST = "name.last";
 *     String YEAROFBIRTH = "yearOfBirth";
 *
 *     ComparableAttribute&lt;Person,Long&gt; ssn = new ComparableAttributeRecord&lt;&gt;(SSN);
 *     BasicAttribute&lt;Person,Name&gt; name = new BasicAttributeRecord&lt;&gt;(NAME);
 *     TextAttribute&lt;Person&gt; name_first = new TextAttributeRecord&lt;&gt;(NAME_FIRST);
 *     TextAttribute&lt;Person&gt; name_last = new TextAttributeRecord&lt;&gt;(NAME_LAST);
 *     ComparableAttribute&lt;Person,Integer&gt; yearOfBirth = new ComparableAttributeRecord&lt;&gt;(YEAROFBIRTH);
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
 * initialization. In the first case, the fields are declared {@code final} and the
 * metamodel class can be an interface. In the second case, the fields are declared
 * non-{@code final} and {@code volatile} and the metamodel class must be a concrete
 * class.</p>
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
