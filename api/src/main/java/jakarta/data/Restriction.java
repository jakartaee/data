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

public interface Restriction<T> {
    interface Basic<T> extends Restriction<T> {
        Operator comparison();

        String field();

        Basic<T> ignoreCase();

        boolean isAnyCase();

        boolean isNegated();

        Object value();
    }

    interface Composite<T> extends Restriction<T> {
        List<Restriction<T>> restrictions();

        Restrict type();

        boolean isNegated();
    }

    enum Operator {
        CONTAINS,
        ENDS_WITH,
        EQUAL,
        GREATER_THAN,
        GREATER_THAN_EQUAL,
        IN,
        LESS_THAN,
        LESS_THAN_EQUAL,
        LIKE,
        STARTS_WITH
    }
}
