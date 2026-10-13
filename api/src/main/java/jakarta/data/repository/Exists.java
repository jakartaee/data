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
 * that determines whether any entity satisfies the conditions expressed by its
 * parameters. A method annotated {@code Exists} is the parameter-based
 * alternative to an {@code existsBy} method of <em>Query by Method Name</em>.
 * For example, the following methods are equivalent:</p>
 *
 * {@snippet lang="java":
 * @Repository
 * public interface Products extends BasicRepository<Product, Long> {
 *     boolean existsByName(String name);
 *
 *     @Exists
 *     boolean isListed(@By(_Product.NAME) String name);
 * }
 * }
 *
 * <p>The annotated method must return {@code boolean}. The value returned is
 * {@code true} if at least one entity satisfies every condition, and
 * {@code false} otherwise. A method without parameters determines whether
 * any entity of the queried type exists.</p>
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
 * <p>For example, the following method determines whether a product is in
 * stock in at least the requested quantity:</p>
 *
 * {@snippet lang="java":
 * @Exists
 * boolean isAvailable(@By(_Product.ID) long productNumber,
 *                     @By(_Product.QUANTITY_IN_STOCK) @Is(AtLeast.class) int minAvailable);
 * }
 *
 * <p>The queried entities are of the {@linkplain #value entity class specified
 * by the annotation}, if any, or otherwise of the primary entity type of the
 * repository.</p>
 *
 * <p>Annotations such as {@code @Find}, {@code @Count}, {@code @Exists},
 * {@code @Query}, {@code @Insert}, {@code @Update}, {@code @Delete}, and
 * {@code @Save} are mutually-exclusive. A given method of a repository
 * interface may have at most one {@code @Find}, {@code @Count}, or
 * {@code @Exists} annotation, lifecycle annotation, or query annotation.</p>
 *
 * @see Count
 * @see By
 * @see Is
 * @since 1.2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Exists {
    /**
     * <p>Optionally specifies the type of entity to query.</p>
     *
     * <p>The default value, {@code void.class}, has the special meaning of
     * querying entities of the primary entity type of the repository.</p>
     *
     * <p>An entity class must be specified if the repository does not define
     * a primary entity type, such as a repository that operates on several
     * types of entity. For example,</p>
     *
     * {@snippet lang="java":
     * @Repository
     * public interface Vehicles {
     *     @Exists(Car.class)
     *     boolean isCarRegistered(@By(_Car.VIN) String vehicleIdNum);
     *
     *     @Exists(Truck.class)
     *     boolean isTruckRegistered(@By(_Truck.VIN) String vehicleIdNum);
     * }
     * }
     */
    Class<?> value() default void.class;
}
