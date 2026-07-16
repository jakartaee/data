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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Page")
class PageTest {

    @Nested
    @DisplayName("When streaming page elements")
    class WhenStreamingPageElements {

        @Test
        @DisplayName("should stream elements in iteration order")
        void shouldStreamElementsInIterationOrder() {
            // given
            Page<String> page = pageOf(
                    "Java",
                    "Jakarta Data",
                    "NoSQL");

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
            Page<String> page = pageOf("Java");

            // when
            boolean parallel = page.stream().isParallel();

            // then
            assertThat(parallel).isFalse();
        }

        @Test
        @DisplayName("should create an empty stream when the iterator is empty")
        void shouldCreateEmptyStreamWhenIteratorIsEmpty() {
            // given
            Page<String> page = pageOf();

            // when
            List<String> result = page.stream().toList();

            // then
            assertThat(result).isEmpty();
        }
    }

    @SafeVarargs
    private static <T> Page<T> pageOf(T... elements) {
        List<T> content = List.of(elements);

        return new Page<>() {

            @Override
            public Iterator<T> iterator() {
                return content.iterator();
            }

            @Override
            public List<T> content() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasContent() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int numberOfElements() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasNext() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasPrevious() {
                throw new UnsupportedOperationException();
            }

            @Override
            public PageRequest pageRequest() {
                throw new UnsupportedOperationException();
            }

            @Override
            public PageRequest nextPageRequest() {
                throw new UnsupportedOperationException();
            }

            @Override
            public PageRequest previousPageRequest() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasTotals() {
                throw new UnsupportedOperationException();
            }

            @Override
            public long totalElements() {
                throw new UnsupportedOperationException();
            }

            @Override
            public long totalPages() {
                throw new UnsupportedOperationException();
            }
        };
    }
}