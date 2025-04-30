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
import jakarta.data.expression.Expression;
import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.StaticMetamodel;

/**
 * <p>A Jakarta Data provider's view of a restriction on a single
 * {@linkplain Attribute entity attribute} or
 * {@linkplain Expression expression}.</p>
 *
 * <p>This class is used by the Jakarta Data provider to interpret a
 * restriction that is supplied by the application. The application should use
 * the instructions in the {@link Restriction} class and the
 * {@linkplain StaticMetamodel static metamodel} to obtain a restriction.</p>
 *
 * @param <T> entity type.
 * @param <V> entity attribute type.
 */
public interface BasicRestriction<T, V> extends Restriction<T> {

    /**
     * <p>Returns the {@linkplain Attribute entity attribute} or
     * {@linkplain Expression expression} to which this restriction applies.</p>
     *
     * <p>This method is intended for Jakarta Data providers.</p>
     *
     * @return the entity attribute or expression.
     */
    Expression<T, V> expression();

    /**
     * <p>Returns the constraint that this restriction applies to the entity
     * attribute or expression.</p>
     *
     * <p>This method is intended for Jakarta Data providers.</p>
     *
     * @return the constraint that is applied by this restriction.
     */
    Constraint<V> constraint();

    /**
     * <p>Creates a new restriction that represents a constraint on the
     * specified {@linkplain Attribute entity attribute} or
     * {@linkplain Expression expression}.</p>
     *
     * <p>Restrictions on {@linkplain Attribute entity attributes} can often be
     * obtained from the {@linkplain StaticMetamodel static metamodel} more
     * conveniently than from this method.</p>
     *
     * @param <T>        entity class.
     * @param <V>        entity attribute class.
     * @param expression the {@linkplain Attribute entity attribute} or
     *                   {@linkplain Expression expression} to which the
     *                   restriction applies.
     * @param constraint the constraint that is applied by the restriction.
     * @return the restriction.
     * @throws NullPointerException if the expression or constraint is 
     *                              {@code null}.
     */
    static <T, V> Restriction<T> of(Expression<T, V> expression,
                                    Constraint<V> constraint) {
        return new BasicRestrictionRecord<>(expression, constraint);
    }
}
