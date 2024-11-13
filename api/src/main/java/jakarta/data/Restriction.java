/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package jakarta.data;


/**
 * Represents a condition used to filter values in repository queries.
 *
 * <p>The `Restriction` interface serves as a general contract for defining filter conditions,
 * supporting various operations such as equality, comparisons, range, null checks,
 * and pattern matching. Implementations of `Restriction` can be used to construct
 * flexible and type-safe filtering logic in repository queries.</p>
 *
 * <p>Subtypes include {@link BasicRestriction}, which handles single-field conditions,
 * and {@link CompositeRestriction}, which combines multiple restrictions
 * using logical operators.</p>
 *
 * @param <T> the type of the entity on which the restriction is applied.
 */
public interface Restriction<T> {


}
