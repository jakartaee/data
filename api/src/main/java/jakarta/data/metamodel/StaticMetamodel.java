/*
 * Copyright (c) 2023,2026 Contributors to the Eclipse Foundation
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
import java.util.UUID;

import jakarta.data.Sort;
import jakarta.data.expression.Expression;
import jakarta.data.restrict.Restriction;

/**
 * Annotates a static metamodel class.
 *
 * <p>
 * A static metamodel class holds a representation of the declared attributes of an entity
 * class, an associated class (such as a Jakarta Persistence Embeddable class),
 * or a superclass of an entity or associated class. The static metamodel enables
 * type-safe access to attribute names as well as an {@code Attribute} subclass
 * from which is obtained {@link Expression}s, {@link Restriction}s, and
 * {@link Sort}s on the attribute. A metamodel class contains one or more
 * {@code public static} fields, all corresponding to attributes declared by
 * the class. The type of each of these fields must be either {@link String}
 * (for attribute names) or the most specific subinterface of {@link Attribute}
 * defined in this package or by vendor API.
 *
 * <p>Jakarta Data static metamodel classes must follow the entity model for
 * the respective entity (usually Jakarta Persistence or Jakarta NoSQL)
 * regarding what is and is not an attribute.
 *
 * <p>Some entity models allow inheritance of attributes from a superclass.
 * For example, Jakarta Persistence entity classes inherit attributes of a
 * superclass annotated {@link jakarta.persistence.MappedSuperclass}
 * (from the Jakarta Persistence API) and Jakarta NoSQL entity classes
 * inherit superclass attributes that have a Jakarta NoSQL annotation.
 * In cases where attributes are inherited, the static metamodel class for
 * the entity or association does not include fields for the inherited
 * attributes, but instead inherits the fields by inheriting a static
 * metamodel class for the superclass.
 *
 * <p>The following subinterfaces of {@code Attribute} are provided to
 * obtain the full benefit of the static metamodel:
 * <ul>
 * <li>{@link TextAttribute} for attributes that represent text,
 *     typically of type {@link String}.</li>
 * <li>{@link NumericAttribute} for attributes of numeric types, such as
 *     {@code int}, {@link Double}, and {@link java.math.BigInteger}.</li>
 * <li>{@link BooleanAttribute} for attributes that represent
 *     {@code true} or {@code false} values of type {@code boolean} or
 *     {@link Boolean}.</li>
 * <li>{@link TemporalAttribute} for attributes of temporal types, such as
 *     {@link java.time.LocalDate}, {@link java.time.LocalTime}, and
 *     {@link java.time.Instant}.</li>
 * <li>{@link ComparableAttribute} for attributes that represent other
 *     sortable and comparable values, such as {@code char}, enumerations,
 *     and {@link UUID}</li>
 * <li>{@link NavigableAttribute} for attributes that are associations,
 *     such as Jakarta Persistence embeddables.</li>
 * <li>{@link BasicAttribute} for other types of attributes, such as
 *     collections.</li>
 * </ul>
 *
 * <p>Jakarta Data defines the following conventions for static metamodel
 * classes:
 * <ul>
 * <li>The metamodel class can be an interface or concrete class.</li>
 * <li>The name of the static metamodel class is formed by prefixing
 *     the name of the modeled class with an underscore ({@code _}).</li>
 * <li>Static metamodel classes should have the same package as the modeled
 *     class.</li>
 * <li>Fields of type {@code String} should be named with all upper case.</li>
 * <li>Fields that are subtypes of {@code Attribute} should be named in lower
 *     case or mixed case, matching the name of the modeled attribute.</li>
 * </ul>
 *
 * <p>For example, for the following entity,</p>
 *
 * <pre>{@code
 * @Entity
 * public class Person {
 *     public LocalDate dateOfBirth;
 *
 *     @Embedded
 *     public Name name;
 *
 *     @Id
 *     public long ssn;
 * }
 *
 * @Embeddable
 * public class Name {
 *     public String first;
 *     public String last;
 * }
 * }</pre>
 *
 * <p>An application programmer may define a static metamodel as follows,</p>
 *
 * <pre>{@code
 * @StaticMetamodel(Person.class)
 * public interface _Person {
 *     String DATEOFBIRTH = "dateOfBirth";
 *     String NAME = "name";
 *     String NAME_FIRST = "name.first";
 *     String NAME_LAST = "name.last";
 *     String SSN = "ssn";
 *
 *     TemporalAttribute<Person, LocalDate> dateOfBirth = TemporalAttribute.of(
 *             Person.class, DATEOFBIRTH, LocalDate.class);
 *     NavigableAttribute<Person, Name> name = NavigableAttribute.of(
 *             Person.class, NAME, Name.class);
 *     TextAttribute<Person> name_first = TextAttribute.of(
 *             Person.class, NAME_FIRST);
 *     TextAttribute<Person> name_last = TextAttribute.of(
 *             Person.class, NAME_LAST);
 *     NumericAttribute<Person, Long> ssn = NumericAttribute.of(
 *             Person.class, SSN, long.class);
 * }
 * }</pre>
 *
 * <p>And use it to refer to entity attributes in a type-safe manner,</p>
 *
 * <pre>{@code
 * Order<Person> order =
 *         Order.by(_Person.dateOfBirth.desc(),
 *                  _Person.name_last.asc(),
 *                  _Person.name_first.asc(),
 *                  _Person.ssn.asc());
 * }</pre>
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
 */
// TODO potentially replace  _Person.name_first and _Person.name_last in the above
//      with usage of NavigableAttribute after the design of it is further along
//      and we are more certain there won't be changes to it.
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
