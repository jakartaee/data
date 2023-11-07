/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultPaginationTest {

    @Test
    @DisplayName("Should throw IllegalArgumentException when no keyset values were provided")
    void shouldThrowExceptionWhenNoKeysetValuesWereProvided() {
        assertThatIllegalArgumentException()
                .as("Mode must be different from OFFSET when cursor is null")
                .isThrownBy(() -> new DefaultPagination(1, 10, Collections.emptyList(), Pagination.Mode.CURSOR_NEXT, null));
    }

    @Test
    @DisplayName("Should throw UnsupportedOperationException when keyset is not supported")
    void shouldThrowExceptionWhenKeysetIsNotSupported() {
        assertThatThrownBy(() -> {
            DefaultPagination pagination = new DefaultPagination(1, 10, Collections.emptyList(), Pagination.Mode.CURSOR_NEXT, new KeysetCursor("me", 200));
            pagination.next();
        }).isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not supported for keyset pagination. Instead use afterKeyset or afterKeysetCursor to provide the next keyset values or obtain the nextPageable from a KeysetAwareSlice.");
    }
}
