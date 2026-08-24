/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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
package jakarta.data.expression;

import java.time.temporal.Temporal;

import jakarta.data.metamodel.BooleanAttribute;
import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.NavigableAttribute;
import jakarta.data.metamodel.NumericAttribute;
import jakarta.data.metamodel.TemporalAttribute;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.spi.expression.path.BooleanPath;
import jakarta.data.spi.expression.path.ComparablePath;
import jakarta.data.spi.expression.path.NavigablePath;
import jakarta.data.spi.expression.path.NumericPath;
import jakarta.data.spi.expression.path.TemporalPath;
import jakarta.data.spi.expression.path.TextPath;
import jakarta.annotation.Nonnull;

/**
 * An {@linkplain Expression expression} whose type has attributes of its own
 * that can be navigated.
 *
 * <p>
 * The {@code navigate} methods accept a metamodel attribute of an intermediate
 * type {@code U} and return a new expression of a more specific subtype,
 * rooted at the entity type {@code T}, that represents the path from the
 * current expression to the given attribute.
 *
 * <p>
 * For example, given a {@code Customer} entity with an embeddable,
 * {@code shippingAddress}, of type {@code Address},
 *
 * <p>
 * <pre>{@code
 * Restrict.all(_Customer.shippingAddress.navigate(_Address.city)
 *                                       .equalTo("Rochester"),
 *              _Customer.shippingAddress.navigate(_Address.state)
 *                                       .equalTo(StateCode.MN));
 * }</pre>
 *
 * <p>
 * The following entity and embeddables are used in examples throughout the
 * documentation of this class.
 *
 * <pre>{@code
 * @Entity
 * public class Customer {
 *     @Id
 *     long id;
 *     @Embedded
 *     Address shippingAddress;
 * }
 *
 * @Embeddable
 * public class Address {
 *     @Embedded
 *     BuildingInfo building;
 *     String city;
 *     StateCode state; // enum
 *     LocalDate validFrom;
 *     boolean verified;
 *     int zip;
 * }
 *
 * @Embeddable
 * public class BuildingInfo {
 *     String buildingName;
 *     int floor;
 *     String officeNum;
 * }
 * }</pre>
 *
 * <p>
 * The static metamodel would include,
 *
 * <pre>{@code
 * @StaticMetamodel(Customer.class)
 * public interface _Customer {
 *     String ID = "id";
 *     String SHIPPING_ADDRESS = "shippingAddress";
 *
 *     NumericAttribute<Customer, Long> id =
 *             NumericAttribute.of(Customer.class, ID, long.class);
 *     NavigableAttribute<Customer, Address> shippingAddress =
 *             NavigableAttribute.of(Customer.class, SHIPPING_ADDRESS, Address.class);
 * }
 *
 * @StaticMetamodel(Address.class)
 * public interface _Address {
 *     String BUILDING = "building";
 *     String CITY = "city";
 *     String STATE = "state";
 *     String VALID_FROM = "validFrom";
 *     String VERIFIED = "verified";
 *     String ZIP = "zip";
 *
 *     NavigableAttribute<Address, BuildingInfo> building =
 *             NavigableAttribute.of(Address.class, BUILDING, BuildingInfo.class);
 *     TextAttribute<Address> city =
 *             TextAttribute.of(Address.class, CITY);
 *     ComparableAttribute<Address, StateCode> state =
 *             ComparableAttribute.of(Address.class, STATE, StateCode.class);
 *     TemporalAttribute<Address, LocalDate> validFrom =
 *             TemporalAttribute.of(Address.class, VALID_FROM, LocalDate.class);
 *     BooleanAttribute<Address> verified =
 *             BooleanAttribute.of(Address.class, VERIFIED, boolean.class);
 *     NumericAttribute<Address, Integer> zip =
 *             NumericAttribute.of(Address.class, ZIP, int.class);
 * }
 *
 * @StaticMetamodel(BuildingInfo.class)
 * public interface _BuildingInfo {
 *     String BUILDING_NAME = "buildingName";
 *     String FLOOR = "floor";
 *     String OFFICE_NUM = "officeNum";
 *
 *     TextAttribute<BuildingInfo> buildingName =
 *             TextAttribute.of(BuildingInfo.class, BUILDING_NAME);
 *     NumericAttribute<BuildingInfo, Integer> floor =
 *             NumericAttribute.of(BuildingInfo.class, FLOOR, int.class);
 *     TextAttribute<BuildingInfo> officeNum =
 *             TextAttribute.of(BuildingInfo.class, OFFICE_NUM);
 * }
 * }</pre>
 *
 * @param <T> entity type
 * @param <U> type of the intermediate object whose attributes can be
 *            navigated to
 * @since 1.1
 */
public interface NavigableExpression<T, U> {

    /**
     * Represents navigation to an attribute that can be further navigated.
     *
     * <p>The resulting {@link NavigableExpression} represents the path formed
     * by navigating from this expression to the given
     * {@linkplain NavigableAttribute navigable attribute} of type {@code V}.
     * The intermediate type {@code V} must itself have attributes that can be
     * further navigated to.
     *
     * <p>
     * Example:
     * <pre>{@code
     * List<Customer> notOnFirstFloor = customers.search(
     *         _Customer.shippingAddress.navigate(_Address.building)
     *                                  .navigate(_BuildingInfo.floor)
     *                                  .notEqualTo(1));
     * }</pre>
     *
     * @param <V>       type of the attribute, which must itself be navigable
     * @param attribute the navigable attribute to which to navigate
     * @return a {@link NavigableExpression} representing the path to the
     *         given attribute
     * @throws NullPointerException if the attribute is {@code null}
     */
    @Nonnull
    default <V> NavigableExpression<T, V> navigate(
            @Nonnull NavigableAttribute<U, V> attribute) {
        return NavigablePath.of(this, attribute);
    }

    /**
     * Represents navigation to a {@link TextAttribute}.
     *
     * <p>
     * Obtains a {@link TextExpression} representing the path formed by
     * navigating from this expression to the given text {@code attribute}.
     *
     * <p>Example:
     * <pre>{@code
     * List<Customer> found = customers.search(
     *         _Customer.shippingAddress.navigate(_Address.city)
     *                                  .equalTo("Minneapolis"));
     * }</pre>
     *
     * @param attribute the text attribute to which to navigate
     * @return a {@link TextExpression} representing the path to the given
     *         attribute.
     * @throws NullPointerException if the attribute is {@code null}
     */
    @Nonnull
    default TextExpression<T> navigate(@Nonnull TextAttribute<U> attribute) {
        return TextPath.of(this, attribute);
    }

    /**
     * Represents navigation to a {@link ComparableAttribute}.
     *
     * <p>
     * Obtains a {@link ComparableExpression} representing the path formed
     * by navigating from this expression to the given comparable
     * {@code attribute}.
     *
     * <p>
     * Example:
     * <pre>{@code
     * List<Customer> found = customers.search(
     *         _Customer.shippingAddress.navigate(_Address.state)
     *                                  .equalTo(StateCode.MN));
     * }</pre>
     *
     * @param <C>       type of the comparable attribute.
     * @param attribute the comparable attribute to which to navigate
     * @return a {@link ComparableExpression} representing the path to the
     *         given attribute
     * @throws NullPointerException if the attribute is {@code null}
     */
    @Nonnull
    default <C extends Comparable<C>> ComparableExpression<T, C> navigate(
            @Nonnull ComparableAttribute<U, C> attribute) {
        return ComparablePath.of(this, attribute);
    }

    /**
     * Represents navigation to a {@link BooleanAttribute}.
     *
     * <p>Obtains a {@link BooleanExpression} representing the path formed by
     * navigating from this expression to the given boolean {@code attribute}.
     *
     * <p>
     * Example:
     * <pre>{@code
     * List<Customer> found = customers.search(
     *         _Customer.shippingAddress.navigate(_Address.verified)
     *                                  .isTrue());
     * }</pre>
     *
     * @param attribute the boolean attribute to which to navigate
     * @return a {@link BooleanExpression} representing the path to the given
     *         attribute
     * @throws NullPointerException if the attribute is {@code null}
     */
    @Nonnull
    default BooleanExpression<T> navigate(
            @Nonnull BooleanAttribute<U> attribute) {
        return BooleanPath.of(this, attribute);
    }

    /**
     * Represents navigation to a {@link NumericAttribute}.
     *
     * <p>Obtains a {@link NumericExpression} representing the path formed by
     * navigating from this expression to the given numeric {@code attribute}.
     *
     * <p>
     * Example:
     * <pre>{@code
     * List<Customer> found = customers.search(
     *         _Customer.shippingAddress.navigate(_Address.zip)
     *                                  .between(55901, 55906));
     * }</pre>
     *
     * @param <N>       type of the numeric attribute
     * @param attribute the numeric attribute to which to navigate
     * @return a {@link NumericExpression} representing the path to the given
     *         attribute.
     * @throws NullPointerException if the attribute is {@code null}
     */
    @Nonnull
    default <N extends Number & Comparable<N>>
            NumericExpression<T, N> navigate(
                    @Nonnull NumericAttribute<U, N> attribute) {
        return NumericPath.of(this, attribute);
    }

    /**
     * Represents navigation to a {@link TemporalAttribute}.
     *
     * <p>
     * Obtains a {@link TemporalExpression} representing the path formed by
     * navigating from this expression to the given temporal {@code attribute}.
     *
     * <p>
     * Example:
     * <pre>{@code
     * List<Customer> found = customers.search(
     *         _Customer.shippingAddress.navigate(_Address.validFrom)
     *                                  .lessThanEqual(CurrentDate.now()));
     * }</pre>
     *
     * @param <V>       type of the temporal attribute
     * @param attribute the temporal attribute to which to navigate
     * @return a {@link TemporalExpression} representing the path to the given
     *         attribute
     * @throws NullPointerException if the attribute is {@code null}
     */
    @Nonnull
    default <V extends Temporal & Comparable<? extends Temporal>>
            TemporalExpression<T, V> navigate(
                    @Nonnull TemporalAttribute<U, V> attribute) {
        return TemporalPath.of(this, attribute);
    }

}
