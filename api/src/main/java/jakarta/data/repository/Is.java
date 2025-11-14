/*
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.data.constraint.AtLeast;
import jakarta.data.constraint.AtMost;
import jakarta.data.constraint.Constraint;
import jakarta.data.constraint.EqualTo;
import jakarta.data.constraint.GreaterThan;
import jakarta.data.constraint.In;
import jakarta.data.constraint.LessThan;
import jakarta.data.constraint.Like;
import jakarta.data.constraint.NotEqualTo;
import jakarta.data.constraint.NotIn;
import jakarta.data.constraint.NotLike;

/**
 * <p>Annotates a parameter of a repository {@link Find} or {@link Delete}
 * method, indicating how an entity attribute is compared with the parameter's
 * value.</p>
 *
 * <p>The {@code @Is} annotation's {@link #value()} supplies the type of
 * comparison as a subtype of {@link jakarta.data.constraint Constraint}.</p>
 *
 * <p>The {@link By} annotation must annotate the same parameter to indicate
 * the entity attribute name, or otherwise, if the {@code -parameters} compile
 * option is enabled, the persistent field is inferred by matching the name of
 * the method parameter.</p>
 *
 * <p>For example,</p>
 *
 * <pre>{@code
 * @Repository
 * public interface Products extends CrudRepository<Product, Long> {
 *
 *     // Find Product entities where the price attribute is less than a maximum value.
 *     @Find
 *     List<Product> pricedBelow(@By(_Product.PRICE) @Is(LessThan.class) float max);
 *
 *     // Find a page of Product entities where the name field matches a pattern.
 *     @Find
 *     Page<Product> search(@By(_Product.NAME) @Is(Like.class) String pattern,
 *                          PageRequest pagination,
 *                          Order<Product> order);
 *
 *     // Remove Product entities with any of the unique identifiers listed.
 *     @Delete
 *     void remove(@By(ID) @Is(In.class) List<Long> productIds);
 * }
 * }</pre>
 *
 * @since 1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Is {

    /**
     * <p>A subtype of {@link jakarta.data.constraint Constraint} that
     * indicates how the entity attribute is compared with a value.</p>
     *
     * <p>The constraint subtype must have a static method that accepts
     * as its only parameter a value compatible with the type (or if primitive,
     * a wrapper for the type) of the repository method parameter to which the
     * {@code @Is} annotation is applied. The repository method parameter type
     * must also be consistent with the respective entity attribute type. This
     * list indicates the constraint subtypes that can be used and links to the
     * applicable static method for each:</p>
     *
     * <ul>
     * <li>{@link AtLeast#min(Comparable) AtLeast}</li>
     * <li>{@link AtMost#max(Comparable) AtMost}</li>
     * <li>{@link EqualTo#value(Object) EqualTo}</li>
     * <li>{@link GreaterThan#bound(Comparable) GreaterThan}</li>
     * <li>{@link In#values(java.util.Collection) In}</li>
     * <li>{@link LessThan#bound(Comparable) LessThan}</li>
     * <li>{@link Like#pattern(String) Like}</li>
     * <li>{@link NotEqualTo#value(Object) NotEqualTo}</li>
     * <li>{@link NotIn#values(java.util.Collection) NotIn}</li>
     * <li>{@link NotLike#pattern(String) NotLike}</li>
     * </ul>
     *
     * <p>The following example involves a {@code Person} entity that has a
     * {@code birthYear} attribute of type {@code int}. It compares the year in
     * which a person was born against a minimum and maximum year that are
     * supplied as parameters to a repository method:</p>
     *
     * <pre>
     * &#64;Find
     * &#64;OrderBy(_Person.BIRTHYEAR)
     * List&lt;Person&gt; bornWithin(&#64;By(_Person.BIRTHYEAR) &#64;Is(AtLeast.class) int minYear,
     *                         &#64;By(_Person.BIRTHYEAR) &#64;Is(AtMost.class) int maxYear);
     * </pre>
     *
     * <p>The default constraint is the
     * {@linkplain EqualTo#value(Object) equality} comparison.</p>
     *
     * @return the type of comparison operation.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Constraint> value() default EqualTo.class;
}
