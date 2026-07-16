/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
package jakarta.data.page;

import jakarta.data.page.impl.PageRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Page")
class PageTest {

    @Test
    @DisplayName("should stream page content in iteration order")
    void shouldStreamPageContentInIterationOrder() {
        // given
        Page<String> page = new PageRecord<>(
                PageRequest.ofPage(1, 3, false),
                List.of("Java", "Jakarta Data", "NoSQL"),
                3,
                false);

        // when
        List<String> result = page.stream().toList();

        // then
        assertThat(result)
                .containsExactly(
                        "Java",
                        "Jakarta Data",
                        "NoSQL");
    }

    @Test
    @DisplayName("should create a sequential stream")
    void shouldCreateSequentialStream() {
        // given
        Page<String> page = new PageRecord<>(
                PageRequest.ofPage(1L, 1, false),
                List.of("Jakarta Data"),
                1,
                false);

        // when
        boolean parallel = page.stream().isParallel();

        // then
        assertThat(parallel).isFalse();
    }

    @Test
    @DisplayName("should stream no elements when page content is empty")
    void shouldStreamNoElementsWhenPageContentIsEmpty() {
        // given
        Page<String> page = new PageRecord<>(
                PageRequest.ofPage(1L, 1, false),
                List.of(),
                0,
                false);

        // when
        List<String> result = page.stream().toList();

        // then
        assertThat(result).isEmpty();
    }
}