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

/**
 * <p>An {@linkplain Expression expression} represents an
 * {@linkplain Attribute entity attribute}, function, or literal value.
 *
 * <p>Expressions are primarily used by the
 * {@linkplain StaticMetamodel static metamodel} to compose
 * {@linkplain Restriction restrictions} at runtime, but can also compose
 * {@linkplain Sort#asc(ComparableExpression) sorts} and
 * {@linkplain Constraint constraints}.
 *
 * <p>The module Javadoc provides an {@linkplain jakarta.data/ overview} of
 * Jakarta Data.
 *
 * @since 1.1
 */
package jakarta.data.expression;

import jakarta.data.constraint.Constraint;
import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.restrict.Restriction;
import jakarta.data.Sort;