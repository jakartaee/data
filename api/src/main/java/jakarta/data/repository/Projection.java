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
package jakarta.data.repository;

/**
 * Indicates that a {@code record} type is intended to serve as a projection
 * for mapping selected attributes from an entity in a query result.
 *
 * <p>This annotation allows Jakarta Data providers to understand that the
 * annotated record is designed for partial mapping of entity data, especially
 * in queries using {@code @Query} or {@code @Find} annotations.</p>
 *
 * <p>All Jakarta Data providers <strong>must</strong> support the use of records
 * annotated with {@code @Projection}. Providers <strong>may</strong> also choose
 * to support projection mapping on records that are not explicitly annotated
 * with {@code @Projection}.</p>
 *
 * <p>Example usage:</p>
 *
 * <pre>{@code
 * @Projection
 * public record ProductSummary(String name, @Select("price") BigDecimal value) {}
 *
 * @Repository
 * public interface ProductRepository {
 *
 *     @Find
 *     @Select("name")
 *     @Select("price")
 *     List<ProductSummary> fetchSummaries();
 * }
 * }</pre>
 *
 * @see jakarta.data.repository.Select
 */
public @interface Projection {
}
