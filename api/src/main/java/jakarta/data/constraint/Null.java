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
package jakarta.data.constraint;

/**
 * A constraint that checks if the attribute's value is {@code null}.
 *
 * <p>Useful for querying entities where the attribute is not assigned or explicitly set to {@code null}.</p>
 *
 * <pre>{@code
 * List<Customer> customers = repository.findAll(
 *     Restrict.isNull(_Customer.middleName)
 * );
 * }</pre>
 *
 * @param <V> the type of the attribute
 */
public interface Null<V> extends Constraint<V> {

    @SuppressWarnings("unchecked")
    static <V> Null<V> instance() {
        return (Null<V>) NullRecord.INSTANCE;
    }
}
