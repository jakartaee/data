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
package jakarta.data.spi.expression.path;

import jakarta.data.expression.BooleanExpression;
import jakarta.data.expression.ComparableExpression;
import jakarta.data.expression.Expression;
import jakarta.data.expression.NavigableExpression;
import jakarta.data.expression.NumericExpression;
import jakarta.data.expression.TemporalExpression;
import jakarta.data.expression.TextExpression;
import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.BooleanAttribute;
import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.NavigableAttribute;
import jakarta.data.metamodel.NumericAttribute;
import jakarta.data.metamodel.TemporalAttribute;
import jakarta.data.metamodel.TextAttribute;
import jakarta.annotation.Nonnull;

/**
 * Supertype of all path expressions in the Jakarta Data SPI.
 *
 * <p>
 * A path expression represents navigation from a navigable expression to
 * one of its attributes, as produced by the {@code navigate} methods of
 * {@link NavigableExpression}.
 *
 * <p>
 * The two type parameters identify the ends of the navigation step:
 * <ul>
 * <li>{@code T} — the root entity type, carried unchanged through any
 *     number of navigation steps.</li>
 * <li>{@code U} — the intermediate type that owns the attribute being
 *     navigated to, i.e. the type of the {@link #expression()}.</li>
 * </ul>
 *
 * <p>Each concrete subtype additionally captures the type of the attribute
 * that was navigated to and extends the corresponding
 * {@linkplain Expression expression} interface:</p>
 * <ul>
 * <li>{@link BooleanPath} — navigates to a {@link BooleanAttribute},
 *     extending {@link BooleanExpression}.</li>
 * <li>{@link ComparablePath} — navigates to a {@link ComparableAttribute},
 *     extending {@link ComparableExpression}.</li>
 * <li>{@link NavigablePath} — navigates to a {@link NavigableAttribute},
 *     extending {@link NavigableExpression} so that further navigation is
 *     possible.</li>
 * <li>{@link NumericPath} — navigates to a {@link NumericAttribute},
 *     extending {@link NumericExpression}.</li>
 * <li>{@link TemporalPath} — navigates to a {@link TemporalAttribute},
 *     extending {@link TemporalExpression}.</li>
 * <li>{@link TextPath} — navigates to a {@link TextAttribute},
 *     extending {@link TextExpression}.</li>
 * </ul>
 *
 * <p>
 * This interface is part of the Jakarta Data SPI for Jakarta Data providers.
 * Applications must not use the SPI directly and can instead obtain path
 * expressions from the {@code navigate} methods of
 * {@link NavigableExpression}:
 * <ul>
 * <li>{@link NavigableExpression#navigate(BooleanAttribute)}</li>
 * <li>{@link NavigableExpression#navigate(ComparableAttribute)}</li>
 * <li>{@link NavigableExpression#navigate(NavigableAttribute)}</li>
 * <li>{@link NavigableExpression#navigate(NumericAttribute)}</li>
 * <li>{@link NavigableExpression#navigate(TemporalAttribute)}</li>
 * <li>{@link NavigableExpression#navigate(TextAttribute)}</li>
 * </ul>
 *
 * @param <T> entity type
 * @param <U> type of the intermediate object that owns the navigated
 *            attribute
 * @since 1.1
 */
public interface Path<T, U> {

    /**
     * An expression representing the navigable path, up to, but not including,
     * the {@link #attribute()}.
     *
     * <p>
     * For the first step in a path, this is a {@linkplain NavigableAttribute
     * metamodel attribute} of the entity. For subsequent steps, this is a
     * {@link NavigablePath}.
     *
     * @return an expression representing the navigable path, up to, but
     *         not including, the {@link #attribute()}.
     */
    @Nonnull
    NavigableExpression<T, U> expression();

    /**
     * The metamodel attribute to which this path navigates.
     * Together with {@link #expression()}, this fully identifies the
     * path step represented by this object.
     *
     * @return the attribute to which this path navigates
     */
    @Nonnull
    Attribute<U> attribute();
}
