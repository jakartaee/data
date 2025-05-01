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
package jakarta.data.restrict;

import jakarta.data.constraint.Constraint;
import jakarta.data.constraint.GreaterThan;
import jakarta.data.constraint.LessThanOrEqual;
import jakarta.data.constraint.Like;
import jakarta.data.constraint.NotLike;
import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.repository.Find;

/**
 * <p>A restriction imposes constraints on entity attribute values or
 * expressions.</p>
 *
 * <p>Basic restrictions that impose a single
 * {@linkplain Constraint constraint} are obtained from {@linkplain Attribute}
 * subtypes of the {@linkplain StaticMetamodel static metamodel}.</p>
 *
 * <p>Composite restrictions that impose multiple
 * {@linkplain Constraint constraints} are obtained from the
 * {@link Restrict#all(Restriction...)} and
 * {@link Restrict#any(Restriction...)} methods.</p>
 *
 * <p>A repository {@link Find} method can optionally accept a parameter of
 * type {@code Restriction}. For example,</p>
 * <pre>
 * &#64;Repository
 * public interface Cars extends CrudRepository&lt;Car, String&gt; {
 *
 *     &#64;Find
 *     List&lt;Car&gt; search(&#64;By(_Car.MAKE) String manufacturer,
 *                      &#64;By(_Car.MODEL) String model,
 *                      Restriction&lt;Car&gt; restriction,
 *                      Order&lt;Car&gt;... sort);
 * }
 * </pre>
 *
 * <p>Instances of restriction obtained from the static metamodel or the
 * {@link Restrict} class can be supplied as the parameter when invoking the
 * respository method. For example,</p>
 * <pre>
 * List&lt;Car&gt; withinPriceRange =
 *         cars.search(make,
 *                     model,
 *                     _Car.price.between(20000, 30000),
 *                     Order.by(_Car.price.desc()));
 *
 * List&lt;Car&gt; pricedUnder30kWhenDiscounted =
 *         cars.search(make,
 *                     model,
 *                     _Car.price.minus(discount).lessThan(30000),
 *                     Order.by(_Car.price.desc()));
 *
 * List&lt;Car&gt; atLeast2020AndPricedBelow30k =
 *         cars.search(make,
 *                     model,
 *                     Restrict.all(_Car.year.greaterThanEqual(2020),
 *                                  _Car.price.lessThan(30000)),
 *                     Order.by(_Car.price.desc()));
 * </pre>
 *
 * <p>The {@linkplain Attribute example entity and static metamodel} for the
 * above are provided in the {@link Attribute} Javadoc.</p>
 *
 * <p>Restrictions are immutable and do not change after they are created.</p>
 *
 * @param <T> entity type.
 */
public interface Restriction<T> {

    /**
     * <p>Returns the negation of this restriction.</p>
     *
     * <p>For example, a basic restriction that represents an
     * {@linkplain GreaterThan exclusive upper bound} on a value is negated
     * as an {@linkplain LessThanOrEqual inclusive lower bound} on the value.
     * </p>
     *
     * <p>A basic restriction that represents {@linkplain Like matching} a
     * pattern is negated as a restriction to {@linkplain NotLike not match}
     * the pattern.</p>
     *
     * <p>A composite restriction that requires satisfying
     * {@linkplain CompositeRestriction.Type#ANY at least one} restriction
     * is negated as requiring that not any (in other words, none) of the
     * restrictions are satisfied.</p>
     *
     * <p>A composite restriction that requires satisfying
     * {@linkplain CompositeRestriction.Type#ALL all} restrictions
     * is negated as requiring that not all of the restrictions are satisfied
     * (in other words, at least one is unsatisfed).</p>
     *
     * <p>This method does not modify the restriction upon which it is invoked.
     * </p>
     *
     * @return the negated restriction.
     */
    Restriction<T> negate();
}
