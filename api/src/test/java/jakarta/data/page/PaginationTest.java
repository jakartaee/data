/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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
package jakarta.data.page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class PaginationTest {

    @Test
    @DisplayName("Should throw IllegalArgumentException when no key values were provided")
    void shouldThrowExceptionWhenNoKeysetValuesWereProvided() {
        assertThatIllegalArgumentException()
                .as("Mode must be different from OFFSET when cursor is null")
                .isThrownBy(() -> new Pagination(1, 10, PageRequest.Mode.CURSOR_NEXT, null, true));
    }

}
