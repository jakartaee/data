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

import java.util.List;

/**
 * A composite restriction that combines multiple {@link Restriction} instances using logical operators.
 *
 * <p>The {@code MultipleRestriction} interface allows for combining multiple restrictions, enabling complex
 * filtering scenarios where multiple conditions must be satisfied. Each contained {@link Restriction}
 * can be evaluated based on the logical operator specified by the {@link CompositeRestrictionType} type.</p>
 *
 * <p>This interface is useful for defining AND/OR conditions where multiple fields and restrictions
 * are evaluated together in a repository query.</p>
 *
 * @param <T> the type of the entity on which the restriction is applied.
 */
public interface CompositeRestriction<T> extends Restriction<T> {

    /**
     * The list of restrictions that are combined in this composite restriction.
     *
     * @return a list of individual restrictions.
     */
    List<Restriction<? extends T>> restrictions();

    /**
     * The logical operator used to combine the contained restrictions, such as AND or OR.
     *
     * @return the logical combination type for this composite restriction.
     */
    CompositeRestrictionType type();
}
