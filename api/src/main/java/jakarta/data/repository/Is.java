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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository;

import jakarta.data.FilterType;

/**
 * Annotation to specify query conditions on method parameters in Jakarta Data repository interfaces.
 * The {@code @Is} annotation, combined with the {@link FilterType} enum, enables flexible and expressive
 * filtering in repository queries.
 * Usage Example:
 * <pre>{@code
 * @Find
 * Page<Product> search(@Is(Conditions.Equal) String name); // Finds products where the name matches exactly.
 * }</pre>
 *
 * Example for Negation:
 *
 * <pre>{@code
 * @Find
 * Page<Product> search(@Is(value = Conditions.Equal, not = true) String excludedName); // Excludes products with the specified name.
 * }</pre>
 *
 */
public @interface Is {

    /**
     * Defines the condition to apply to the annotated parameter.
     *
     * @return the specified {@link FilterType} value.
     */
    FilterType value();

    /**
     * When set to true, applies the negation of the specified condition.
     * For example, using `not = true` with {@link FilterType#Equal} will search for records
     * where the field is not equal to the specified value.
     *
     * @return whether to negate the specified condition.
     */
    boolean not() default false;
}
