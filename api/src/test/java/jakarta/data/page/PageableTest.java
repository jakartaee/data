/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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

class PageableTest {

    @Test
    @DisplayName("Should correctly paginate")
    void shouldCreatePageable() {
        Pagination pageable = Pagination.ofPage(2).size(6);

        assertSoftly(softly -> {
            softly.assertThat(pageable.page()).isEqualTo(2L);
            softly.assertThat(pageable.size()).isEqualTo(6);
        });
    }

    @Test
    @DisplayName("Should create pageable with size")
    void shouldCreatePageableWithSize() {
        Pagination pageable = Pagination.ofSize(50);

        assertSoftly(softly -> {
            softly.assertThat(pageable.page()).isEqualTo(1L);
            softly.assertThat(pageable.size()).isEqualTo(50);
        });
    }

    @Test
    @DisplayName("Should navigate next")
    void shouldNext() {
        Pagination pageable = Pagination.ofSize(1).page(2);
        Pagination next = pageable.next();

        assertSoftly(softly -> {
            softly.assertThat(pageable.page()).isEqualTo(2L);
            softly.assertThat(pageable.size()).isEqualTo(1);
            softly.assertThat(next.page()).isEqualTo(3L);
            softly.assertThat(next.size()).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("Should create a new Pageable at the given page with a default size of 10")
    void shouldCreatePage() {
        Pagination pageable = Pagination.ofPage(5);

        assertSoftly(softly -> {
            softly.assertThat(pageable.page()).isEqualTo(5L);
            softly.assertThat(pageable.size()).isEqualTo(10);
        });
    }

    @Test
    @DisplayName("Should be displayable as String with toString")
    void shouldPageableDisplayAsString() {
        assertSoftly(softly -> softly.assertThat(Pagination.ofSize(60).toString())
              .isEqualTo("Pageable{page=1, size=60}"));

        assertSoftly(softly -> softly.assertThat(Pagination.ofSize(80).sortBy(Sort.desc("yearBorn"), Sort.asc("monthBorn"),
                        Sort.asc("id")).toString())
              .isEqualTo("Pageable{page=1, size=80, yearBorn DESC, monthBorn ASC, id ASC}"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when page is not present")
    void shouldReturnErrorWhenThereIsIllegalArgument() {
        Pagination p1 = Pagination.ofPage(1);

        assertThatIllegalArgumentException().isThrownBy(() -> Pagination.ofPage(0));
        assertThatIllegalArgumentException().isThrownBy(() -> Pagination.ofPage(-1));
        assertThatIllegalArgumentException().isThrownBy(() -> p1.size(-1));
        assertThatIllegalArgumentException().isThrownBy(() -> p1.size(0));
        assertThatIllegalArgumentException().isThrownBy(() -> Pagination.ofSize(0));
        assertThatIllegalArgumentException().isThrownBy(() -> Pagination.ofSize(-1));
    }

    @Test
    public void shouldHaveEmptySortListWhenSortIsNullOrEmpty() {
        Pagination p = Pagination.ofSize(2).sortBy(Sort.asc("Id"));

        assertSoftly(softly -> {
            softly.assertThat(p.sorts()).isEqualTo(List.of(Sort.asc("Id")));
            softly.assertThat(p.sortBy().sorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(p.sortBy((Iterable<Sort>) null).sorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(p.sortBy((Sort[]) null).sorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(p.sortBy().sorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(p.sortBy(List.of()).sorts()).isEqualTo(Collections.EMPTY_LIST);
        });
    }

    @Test
    void shouldCreatePageableSort() {
        Pagination pageable = Pagination.ofSize(3).sortBy(Sort.asc("name"));

        assertSoftly(softly -> {
            softly.assertThat(pageable).isNotNull();
            softly.assertThat(pageable.page()).isEqualTo(1);
            softly.assertThat(pageable.size()).isEqualTo(3);
            softly.assertThat(pageable.sorts()).hasSize(1).contains(Sort.asc("name"));
        });
    }

    @Test
    @DisplayName("Should expect UnsupportedOperationException when sort is modified")
    void shouldNotModifySort() {
        assertThatThrownBy( () -> {
            Pagination pageable = Pagination.ofSize(3).sortBy(Sort.asc("name"));
            List<Sort> sorts = pageable.sorts();

            sorts.clear();
        }).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should not modify sort on next pahe")
    void shouldNotModifySortOnNextPage() {
        Pagination pageable = Pagination.ofSize(3).sortBy(Sort.asc("name"), Sort.desc("age"));
        Pagination next = pageable.next();

        assertSoftly(softly -> {
            softly.assertThat(pageable.page()).isEqualTo(1);
            softly.assertThat(pageable.size()).isEqualTo(3);

            softly.assertThat(pageable.sorts()).hasSize(2).contains(Sort.asc("name"), Sort.desc("age"));

            softly.assertThat(next.page()).isEqualTo(2);
            softly.assertThat(next.size()).isEqualTo(3);

            softly.assertThat(next.sorts()).hasSize(2).contains(Sort.asc("name"), Sort.desc("age"));
        });
    }

    @Test
    @DisplayName("Page number should be replaced on new instance of Pageable")
    void shouldReplacePage() {
        Pagination p6 = Pagination.ofSize(75).page(6).sortBy(Sort.desc("price"));
        Pagination p7 = p6.page(7);

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
    @DisplayName("Size should be replaced on new instance of Pageable")
    void shouldReplaceSize() {
        Pagination s90 = Pagination.ofPage(4).size(90);
        Pagination s80 = s90.size(80);

        assertSoftly(softly -> {
            softly.assertThat(s80.size()).isEqualTo(80);
            softly.assertThat(s90.size()).isEqualTo(90);
            softly.assertThat(s90.page()).isEqualTo(4L);
            softly.assertThat(s80.page()).isEqualTo(4L);
        });
    }

    @Test
    @DisplayName("Sorts should be replaced on new instance of Pageable")
    void shouldReplaceSorts() {
        Pagination p1 = Pagination.ofSize(55).sortBy(Sort.desc("lastName"), Sort.asc("firstName"));
        Pagination p2 = p1.sortBy(Sort.asc("firstName"), Sort.asc("lastName"));

        assertSoftly(softly -> {
            softly.assertThat(p1.sorts()).isEqualTo(List.of(Sort.desc("lastName"), Sort.asc("firstName")));
            softly.assertThat(p2.sorts()).isEqualTo(List.of(Sort.asc("firstName"), Sort.asc("lastName")));
            softly.assertThat(p1.page()).isEqualTo(1L);
            softly.assertThat(p2.page()).isEqualTo(1L);
            softly.assertThat(p1.size()).isEqualTo(55);
            softly.assertThat(p2.size()).isEqualTo(55);
        });
    }
}
