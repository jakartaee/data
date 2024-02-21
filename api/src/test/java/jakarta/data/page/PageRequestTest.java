/*
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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

import jakarta.data.Sort;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class PageRequestTest {

    @Test
    @DisplayName("Should correctly paginate")
    void shouldCreatePageRequest() {
        PageRequest<?> pageRequest = PageRequest.ofPage(2).size(6);

        assertSoftly(softly -> {
            softly.assertThat(pageRequest.page()).isEqualTo(2L);
            softly.assertThat(pageRequest.size()).isEqualTo(6);
        });
    }

    @Test
    @DisplayName("Should create PageRequest with size")
    void shouldCreatePageRequestWithSize() {
        PageRequest<?> pageRequest = PageRequest.ofSize(50);

        assertSoftly(softly -> {
            softly.assertThat(pageRequest.page()).isEqualTo(1L);
            softly.assertThat(pageRequest.size()).isEqualTo(50);
        });
    }

    @Test
    @DisplayName("Should navigate next")
    void shouldNext() {
        PageRequest<?> pageRequest = PageRequest.ofSize(1).page(2);
        PageRequest<?> next = pageRequest.next();

        assertSoftly(softly -> {
            softly.assertThat(pageRequest.page()).isEqualTo(2L);
            softly.assertThat(pageRequest.size()).isEqualTo(1);
            softly.assertThat(next.page()).isEqualTo(3L);
            softly.assertThat(next.size()).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("Should create a new PageRequest at the given page with a default size of 10")
    void shouldCreatePage() {
        PageRequest<?> pageRequest = PageRequest.ofPage(5);

        assertSoftly(softly -> {
            softly.assertThat(pageRequest.page()).isEqualTo(5L);
            softly.assertThat(pageRequest.size()).isEqualTo(10);
        });
    }

    @Test
    @DisplayName("Should be displayable as String with toString")
    void shouldPageRequestDisplayAsString() {
        assertSoftly(softly -> softly.assertThat(PageRequest.ofSize(60).toString())
              .isEqualTo("PageRequest{page=1, size=60}"));

        assertSoftly(softly -> softly.assertThat(PageRequest.ofSize(80).sortBy(Sort.desc("yearBorn"), Sort.asc("monthBorn"),
                        Sort.asc("id")).toString())
              .isEqualTo("PageRequest{page=1, size=80, yearBorn DESC, monthBorn ASC, id ASC}"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when page is not present")
    void shouldReturnErrorWhenThereIsIllegalArgument() {
        PageRequest<?> p1 = PageRequest.ofPage(1);

        assertThatIllegalArgumentException().isThrownBy(() -> PageRequest.ofPage(0));
        assertThatIllegalArgumentException().isThrownBy(() -> PageRequest.ofPage(-1));
        assertThatIllegalArgumentException().isThrownBy(() -> p1.size(-1));
        assertThatIllegalArgumentException().isThrownBy(() -> p1.size(0));
        assertThatIllegalArgumentException().isThrownBy(() -> PageRequest.ofSize(0));
        assertThatIllegalArgumentException().isThrownBy(() -> PageRequest.ofSize(-1));
    }

    @Test
    public void shouldHaveEmptySortListWhenSortIsNullOrEmpty() {
        PageRequest<Object> p = PageRequest.ofSize(2).sortBy(Sort.asc("Id"));

        assertSoftly(softly -> {
            softly.assertThat(p.sorts()).isEqualTo(List.of(Sort.asc("Id")));
            softly.assertThat(p.sortBy((Iterable<Sort<Object>>) null).sorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(p.sortBy(List.of()).sorts()).isEqualTo(Collections.EMPTY_LIST);
        });
    }

    @Test
    void shouldCreatePageRequestSort() {
        PageRequest<?> pageRequest = PageRequest.ofSize(3).sortBy(Sort.asc("name"));

        assertSoftly(softly -> {
            softly.assertThat(pageRequest).isNotNull();
            softly.assertThat(pageRequest.page()).isEqualTo(1);
            softly.assertThat(pageRequest.size()).isEqualTo(3);
            softly.assertThat(pageRequest.sorts()).hasSize(1).contains(Sort.asc("name"));
        });
    }

    @Test
    @DisplayName("Should expect UnsupportedOperationException when sort is modified")
    void shouldNotModifySort() {
        assertThatThrownBy( () -> {
            PageRequest<Object> pageRequest = PageRequest.ofSize(3).sortBy(Sort.asc("name"));
            List<Sort<Object>> sorts = pageRequest.sorts();

            sorts.clear();
        }).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should not modify sort on next page")
    void shouldNotModifySortOnNextPage() {
        PageRequest<?> pageRequest = PageRequest.ofSize(3).sortBy(Sort.asc("name"), Sort.desc("age"));
        PageRequest<?> next = pageRequest.next();

        assertSoftly(softly -> {
            softly.assertThat(pageRequest.page()).isEqualTo(1);
            softly.assertThat(pageRequest.size()).isEqualTo(3);

            softly.assertThat(pageRequest.sorts()).hasSize(2).contains(Sort.asc("name"), Sort.desc("age"));

            softly.assertThat(next.page()).isEqualTo(2);
            softly.assertThat(next.size()).isEqualTo(3);

            softly.assertThat(next.sorts()).hasSize(2).contains(Sort.asc("name"), Sort.desc("age"));
        });
    }

    @Test
    @DisplayName("Page number should be replaced on new instance of PageRequest")
    void shouldReplacePage() {
        PageRequest<?> p6 = PageRequest.ofSize(75).page(6).sortBy(Sort.desc("price"));
        PageRequest<?> p7 = p6.page(7);

        assertSoftly(softly -> {
            softly.assertThat(p7.page()).isEqualTo(7L);
            softly.assertThat(p6.page()).isEqualTo(6L);
            softly.assertThat(p7.size()).isEqualTo(75);
            softly.assertThat(p6.size()).isEqualTo(75);
            softly.assertThat(p7.sorts()).isEqualTo(List.of(Sort.desc("price")));
            softly.assertThat(p6.sorts()).isEqualTo(List.of(Sort.desc("price")));
        });
    }

    @Test
    @DisplayName("Size should be replaced on new instance of PageRequest")
    void shouldReplaceSize() {
        PageRequest<?> s90 = PageRequest.ofPage(4).size(90);
        PageRequest<?> s80 = s90.size(80);

        assertSoftly(softly -> {
            softly.assertThat(s80.size()).isEqualTo(80);
            softly.assertThat(s90.size()).isEqualTo(90);
            softly.assertThat(s90.page()).isEqualTo(4L);
            softly.assertThat(s80.page()).isEqualTo(4L);
        });
    }

    @Test
    @DisplayName("Sorts should be replaced on new instance of PageRequest")
    void shouldReplaceSorts() {
        PageRequest<?> p1 = PageRequest.ofSize(55).sortBy(Sort.desc("lastName"), Sort.asc("firstName"));
        PageRequest<?> p2 = p1.sortBy(Sort.asc("firstName"), Sort.asc("lastName"));

        assertSoftly(softly -> {
            softly.assertThat(p1.sorts()).isEqualTo(List.of(Sort.desc("lastName"), Sort.asc("firstName")));
            softly.assertThat(p2.sorts()).isEqualTo(List.of(Sort.asc("firstName"), Sort.asc("lastName")));
            softly.assertThat(p1.page()).isEqualTo(1L);
            softly.assertThat(p2.page()).isEqualTo(1L);
            softly.assertThat(p1.size()).isEqualTo(55);
            softly.assertThat(p2.size()).isEqualTo(55);
        });
    }

    @Test
    @DisplayName("Sorts should be appended by the Sort.asc method")
    void shouldAppendAscendingSort() {
        PageRequest<?> p1 = PageRequest.ofSize(50).asc("first");
        PageRequest<?> p2 = p1.asc("second");
        PageRequest<?> p3 = p2.asc("third");

        assertSoftly(softly -> {
            softly.assertThat(p1.sorts()).isEqualTo(
                    List.of(Sort.asc("first")));
            softly.assertThat(p2.sorts()).isEqualTo(
                    List.of(Sort.asc("first"), Sort.asc("second")));
            softly.assertThat(p3.sorts()).isEqualTo(
                    List.of(Sort.asc("first"), Sort.asc("second"), Sort.asc("third")));
            softly.assertThat(p3.size()).isEqualTo(50);
        });
    }

    @Test
    @DisplayName("Sorts should be appended by the Sort.ascIgnoreCase method")
    void shouldAppendCaseInsensitiveAscendingSort() {
        PageRequest<?> p1 = PageRequest.ofSize(40).ascIgnoreCase("first");
        PageRequest<?> p2 = p1.ascIgnoreCase("second");
        PageRequest<?> p3 = p2.ascIgnoreCase("third");

        assertSoftly(softly -> {
            softly.assertThat(p1.sorts()).isEqualTo(
                    List.of(Sort.ascIgnoreCase("first")));
            softly.assertThat(p2.sorts()).isEqualTo(
                    List.of(Sort.ascIgnoreCase("first"), Sort.ascIgnoreCase("second")));
            softly.assertThat(p3.sorts()).isEqualTo(
                    List.of(Sort.ascIgnoreCase("first"), Sort.ascIgnoreCase("second"), Sort.ascIgnoreCase("third")));
            softly.assertThat(p3.size()).isEqualTo(40);
        });
    }

    @Test
    @DisplayName("Sorts should be appended by the Sort.descIgnoreCase method")
    void shouldAppendCaseInsensitiveDescendingSort() {
        PageRequest<?> p1 = PageRequest.ofSize(30).descIgnoreCase("first");
        PageRequest<?> p2 = p1.descIgnoreCase("second");
        PageRequest<?> p3 = p2.descIgnoreCase("third");

        assertSoftly(softly -> {
            softly.assertThat(p1.sorts()).isEqualTo(
                    List.of(Sort.descIgnoreCase("first")));
            softly.assertThat(p2.sorts()).isEqualTo(
                    List.of(Sort.descIgnoreCase("first"), Sort.descIgnoreCase("second")));
            softly.assertThat(p3.sorts()).isEqualTo(
                    List.of(Sort.descIgnoreCase("first"), Sort.descIgnoreCase("second"), Sort.descIgnoreCase("third")));
            softly.assertThat(p3.size()).isEqualTo(30);
        });
    }

    @Test
    @DisplayName("Sorts should be appended by the Sort.desc method")
    void shouldAppendDescendingSort() {
        PageRequest<?> p1 = PageRequest.ofSize(20).desc("first");
        PageRequest<?> p2 = p1.desc("second");
        PageRequest<?> p3 = p2.desc("third");

        assertSoftly(softly -> {
            softly.assertThat(p1.sorts()).isEqualTo(
                    List.of(Sort.desc("first")));
            softly.assertThat(p2.sorts()).isEqualTo(
                    List.of(Sort.desc("first"), Sort.desc("second")));
            softly.assertThat(p3.sorts()).isEqualTo(
                    List.of(Sort.desc("first"), Sort.desc("second"), Sort.desc("third")));
            softly.assertThat(p3.size()).isEqualTo(20);
        });
    }
}
