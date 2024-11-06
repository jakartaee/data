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

import jakarta.data.Operator;

/**
 * Annotation to specify query conditions on method parameters in Jakarta Data repository interfaces.
 * The {@code @Is} annotation, combined with the {@link Operator} enum, enables flexible and expressive
 * filtering in repository queries.
 * Usage Example:
 * <pre>{@code
 * @Find
 * Page<Product> search(@Is(Operator.Equal) String name); // Finds products where the name matches exactly.
 * }</pre>
 *
 */
public @interface Is {

    /**
     * Defines the condition to apply to the annotated parameter.
     *
     * @return the specified {@link Operator} value.
     */
    Operator value();
}
