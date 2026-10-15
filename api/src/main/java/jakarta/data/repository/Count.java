/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.constraint.Constraint;
import jakarta.data.page.PageRequest;
import jakarta.data.restrict.Restriction;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a repository method as a parameter-based automatic query method
 * that counts the entities which satisfy the conditions expressed by its
 * parameters. A method annotated {@code Count} is the parameter-based
 * alternative to a {@link Query @Query} method that selects
 * {@code COUNT(THIS)}, and to a {@code countBy} method of
 * <em>Query by Method Name</em>. For example, the following methods are
 * equivalent:</p>
 *
 * {@snippet lang="java":
 * @Repository
 * public interface Products extends BasicRepository<Product, Long> {
 *     @Query("SELECT COUNT(THIS) FROM Product WHERE department = ?1")
 *     long countIn(Department department);
 *
 *     @Count
 *     long totalIn(@By(_Product.DEPARTMENT) Department department);
 * }
 * }
 *
 * <p>The annotated method must return {@code long} or {@code int}. The value
 * returned is the number of entities which satisfy every condition. A method
 * without parameters counts every entity of the queried type.</p>
 *
 * <p>Each parameter of the annotated method must either:</p>
 * <ul>
 * <li>have the same name (the parameter name in the Java source, or a name
 *     assigned by {@link By @By}) as an attribute of the entity class, in
 *     which case the attribute is compared with the argument for equality,
 *     or according to the {@link Constraint} subtype that is the parameter
 *     type or that is specified by the {@link Is @Is} annotation, or</li>
 * <li>be of type {@link Restriction}, in which case the restriction supplied
 *     by the caller imposes additional conditions.</li>
 * </ul>
 * <p>Parameters of type {@link Limit}, {@link Order}, {@link PageRequest},
 * and {@link Sort} are not permitted.</p>
 *
 * <p>For example, the following methods are equivalent, both counting the
 * products priced below a maximum:</p>
 *
 * {@snippet lang="java":
 * @Query("SELECT COUNT(THIS) FROM Product WHERE price < ?1")
 * long countPricedBelow(float maxPrice);
 *
 * @Count
 * long pricedBelow(@By(_Product.PRICE) @Is(LessThan.class) float maxPrice);
 * }
 *
 * <p>A {@link Restriction} parameter allows the caller to supply additional
 * conditions at runtime. For example, the following method counts the
 * products of a department which satisfy a restriction supplied by the
 * caller:</p>
 *
 * {@snippet lang="java":
 * @Count
 * long totalDiscounted(@By(_Product.DEPARTMENT) Department department,
 *                      Restriction<Product> filter);
 * }
 *
 * {@snippet lang="java":
 * long count = products.totalDiscounted(Department.GARDEN,
 *                                       _Product.discount.greaterThan(0.0f));
 * }
 *
 * <p>The counted entities are of the {@linkplain #value entity class specified
 * by the annotation}, if any, or otherwise of the primary entity type of the
 * repository.</p>
 *
 * <p>Annotations such as {@code @Find}, {@code @Count}, {@code @Exists},
 * {@code @Query}, {@code @Insert}, {@code @Update}, {@code @Delete}, and
 * {@code @Save} are mutually-exclusive. A given method of a repository
 * interface may have at most one {@code @Find}, {@code @Count}, or
 * {@code @Exists} annotation, lifecycle annotation, or query annotation.</p>
 *
 * @see Exists
 * @see By
 * @see Is
 * @since 1.2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Count {
    /**
     * <p>Optionally specifies the type of entity to count.</p>
     *
     * <p>The default value, {@code void.class}, has the special meaning of
     * counting entities of the primary entity type of the repository.</p>
     *
     * <p>An entity class must be specified if the repository does not define
     * a primary entity type, such as a repository that operates on several
     * types of entity. For example,</p>
     *
     * {@snippet lang="java":
     * @Repository
     * public interface Vehicles {
     *     @Count(Car.class)
     *     long carsMadeBy(@By(_Car.MAKE) String make);
     *
     *     @Count(Truck.class)
     *     long trucksMadeBy(@By(_Truck.MAKE) String make);
     * }
     * }
     */
    Class<?> value() default void.class;
}
