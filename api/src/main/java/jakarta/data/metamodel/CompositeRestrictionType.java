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
package jakarta.data.metamodel;


/**
 * Represents the logical operators used to combine multiple restrictions in a query.
 *
 * <p>The `Restrict` enum defines two types of logical combinations:</p>
 * <ul>
 *   <li><b>ALL</b> - Requires that all contained restrictions are satisfied (logical AND).</li>
 *   <li><b>ANY</b> - Requires that at least one of the contained restrictions is satisfied (logical OR).</li>
 * </ul>
 *
 * <p>This enum is typically used in {@link CompositeRestriction} to specify how its list of
 * restrictions should be combined, allowing for flexible and complex query conditions.</p>
 */
public enum CompositeRestrictionType {
    /**
     * Requires that all contained restrictions must be satisfied (logical AND).
     */
    ALL,

    /**
     * Requires that at least one of the contained restrictions must be satisfied (logical OR).
     */
    ANY
}
