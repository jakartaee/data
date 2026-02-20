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

import jakarta.data.messages.Messages;
import jakarta.data.metamodel.Attribute;

import java.util.List;

/**
 * <p>Creates composite restrictions.</p>
 *
 * <p>Composite restrictions are created by combining multiple restrictions,
 * which might themselves be composite restrictions or can be obtained from
 * static metamodel {@link Attribute} subtypes.</p>
 *
 * <p>For example, the following constructs a composite restriction (from a
 * basic restriction and another composite restriction) that matches cars that
 * either cost less than $35000 or have newer model years than 2023 and cost
 * less than $40000.</p>
 *
 * <pre>
 * List&lt;Car&gt; found =
 *         cars.search(make,
 *                     model,
 *                     Restrict.any(_Car.price.lessThan(35000),
 *                                  Restrict.all(_Car.year.greaterThan(2023),
 *                                               _Car.price.lessThan(40000))),
 *                     Order.by(_Car.year.desc(),
 *                              _Car.price.desc()));
 * </pre>
 *
 * @since 1.1
 */
public class Restrict {

    // prevent instantiation
    private Restrict() {
    }

    /**
     * <p>Returns a composite restriction that is satisfied when all of the
     * supplied restrictions are satisfied. The order of the restrictions is
     * preserved in the composite restriction.</p>
     *
     * <p>For example,</p>
     * <pre>
     * List&lt;Book&gt; jakartaEEBooksByAuthor =
     *         books.writtenBy(author,
     *                         Restrict.all(_Book.title.notNull(),
     *                                      _Book.title.upper().contains("JAKARTA EE")),
     *                         Order.by(_Book.title.asc()));
     * </pre>
     *
     * @param <T>          entity type.
     * @param restrictions one or more restrictions obtained from a method of
     *                     {@code Restrict} or from a static metamodel
     *                     {@link Attribute} subtype.
     * @return
     * @throws IllegalArgumentException if the supplied restrictions array is
     *                                  empty or {@code null}.
     * @throws NullPointerException     if the supplied restrictions array
     *                                  includes a {@code null} value.
     */
    @SafeVarargs
    public static <T> Restriction<T> all(
            Restriction<? super T>... restrictions) {
        return new CompositeRestrictionRecord<T>(
                CompositeRestriction.Type.ALL,
                List.of(restrictions));
    }

    /**
     * <p>Returns a composite restriction that is satisfied when all of the
     * supplied restrictions are satisfied. The order of the restrictions is
     * preserved in the composite restriction, which keeps a
     * {@link List#copyOf(java.util.Collection) copy} of the supplied
     * {@code List} rather than the original if the list is modifiable.</p>
     *
     * <p>For example,</p>
     * <pre>
     * List&lt;Restriction&lt;Book&gt;&gt; restrictions = new ArrayList&lt;&gt;();
     * restrictions.add(_Book.price.lessThanEqual(10.0f));
     * restrictions.add(_Book.numPages.greaterThanEqual(200));
     *
     * List&lt;Sort&lt;Book&gt;&gt; sorts = new ArrayList&lt;&gt;();
     * sorts.add(_Book.price.desc());
     * sorts.add(_Book.title.asc());
     *
     * List&lt;Book&gt; inexpensiveLongBooks =
     *         books.search(Restrict.all(restrictions),
     *                      Order.by(sorts));
     * </pre>
     *
     * @param <T>          entity type.
     * @param restrictions one or more restrictions obtained from a method of
     *                     {@code Restrict} or from a static metamodel
     *                     {@link Attribute} subtype.
     * @return
     * @throws IllegalArgumentException if the supplied restrictions list is
     *                                  empty or {@code null}.
     * @throws NullPointerException     if the supplied restrictions list
     *                                  includes a {@code null} value.
     */
    public static <T> Restriction<T> all(
            List<? extends Restriction<? super T>> restrictions) {
        return new CompositeRestrictionRecord<>(
                CompositeRestriction.Type.ALL,
                List.copyOf(restrictions));
    }

    /**
     * <p>Returns a composite restriction that is satisfied when at least one
     * of the supplied restrictions is satisfied. The order of the restrictions
     * is preserved in the composite restriction.</p>
     *
     * <p>For example,</p>
     * <pre>
     * List&lt;Product&gt; found =
     *         products.search(productName,
     *                         Restrict.any(_Product.color.equalTo(Color.BLUE),
     *                                      _Product.color.equalTo(Color.GREEN)));
     * </pre>
     *
     * @param <T>          entity type.
     * @param restrictions one or more restrictions obtained from a method of
     *                     {@code Restrict} or from a static metamodel
     *                     {@link Attribute} subtype.
     * @return
     * @throws IllegalArgumentException if the supplied restrictions array is
     *                                  empty or {@code null}.
     * @throws NullPointerException     if the supplied restrictions array
     *                                  includes a {@code null} value.
     */
    @SafeVarargs
    public static <T> Restriction<T> any(
            Restriction<? super T>... restrictions) {
        return new CompositeRestrictionRecord<T>(
                CompositeRestriction.Type.ANY,
                List.of(restrictions));
    }

    /**
     * <p>Returns a composite restriction that is satisfied when at least one
     * of the supplied restrictions is satisfied. The order of the restrictions
     * is preserved in the composite restriction The order of the restrictions is
     * preserved in the composite restriction, which keeps a
     * {@link List#copyOf(java.util.Collection) copy} of the supplied
     * {@code List} rather than the original if the list is modifiable.</p>
     *
     * <p>For example,</p>
     * <pre>
     * List&lt;Restriction&lt;Book&gt;&gt; restrictions = new ArrayList&lt;&gt;();
     * restrictions.add(_Book.price.lessThan(15.0f));
     * restrictions.add(_Book.hardcovered.isEqualTo(true));
     *
     * List&lt;Sort&lt;Book&gt;&gt; sorts = new ArrayList&lt;&gt;();
     * sorts.add(_Book.price.desc());
     * sorts.add(_Book.isbn.asc());
     *
     * List&lt;Book&gt; found =
     *         books.titled(title,
     *                      Restrict.any(restrictions),
     *                      Order.by(sorts));
     * </pre>
     *
     * <p>The example method above requires the book's title to always match
     * and the book to either be priced under $15 or have a hard cover.</p>
     *
     * @param <T>          entity type.
     * @param restrictions one or more restrictions obtained from a method of
     *                     {@code Restrict} or from a static metamodel
     *                     {@link Attribute} subtype.
     * @return
     * @throws IllegalArgumentException if the supplied restrictions array is
     *                                  empty or {@code null}.
     * @throws NullPointerException     if the supplied restrictions array
     *                                  includes a {@code null} value.
     */
    public static <T> Restriction<T>any(
            List<? extends Restriction<? super T>> restrictions) {
        return new CompositeRestrictionRecord<T>(
                CompositeRestriction.Type.ANY,
                List.copyOf(restrictions));
    }

    /**
     * <p>Returns the negation of the specified restriction.</p>
     *
     * <p>This restriction returned by this method is obtained by invoking
     * {@link Restriction#negate()} on the supplied restriction.</p>
     *
     * @return the negated restriction.
     * @throws NullPointerException if the supplied restriction is
     *                              {@code null}.
     */
    public static <T> Restriction<T> not(Restriction<T> restriction) {
        if (restriction == null) {
            throw new NullPointerException(Messages.get("001.arg.required",
                                           "restriction"));
        }

        return restriction.negate();
    }

    /**
     * <p>Returns a restriction that always evaluates to satisfied. This can be
     * used to avoid imposing additional restrictions in places where a
     * {@link Restriction} value is required.</p>
     *
     * @param <T> entity type.
     * @return a restriction that is always considered to be satisfied.
     */
    @SuppressWarnings("unchecked")
    public static <T> Restriction<T> unrestricted() {
        return (Restriction<T>) Unrestricted.INSTANCE;
    }
}
