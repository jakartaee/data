/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
 * Provides a fluent and type-safe API for expressing constraints in data queries.
 *
 * <p>
 * Each interface in this package represents a constraint that can be applied to an entity attribute,
 * such as equality, comparison, membership in a list, or null checks. These constraints are intended
 * to be used in type-safe, programmatic queries.
 * </p>
 *
 * <p>
 * Constraints can be composed using {@link jakarta.data.restrict.Restrict}:
 * </p>
 *
 * <p>
 * These constraints are evaluated at the query level, enabling dynamic, type-safe query construction
 * without relying on string-based filters.
 * </p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>{@code
 * List<Product> results = products.findAll(
 *     Restrict.all(
 *         _Product.price.greaterThan(10.0),
 *         _Product.price.lessThan(100.0),
 *         _Product.name.like("book").negate()
 *     )
 * );
 * }</pre>
 */
package jakarta.data.constraint;