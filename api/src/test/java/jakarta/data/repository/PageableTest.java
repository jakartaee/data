/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package jakarta.data.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageableTest {

    @Test
    @DisplayName("Should correctly paginate")
    void shouldCreatePageable() {
        Pageable pageable = Pageable.ofPage(2).newSize(6);

        assertSoftly(softly -> {
            softly.assertThat(pageable.page()).isEqualTo(2L);
            softly.assertThat(pageable.size()).isEqualTo(6);
        });
    }

    @Test
    @DisplayName("Should create pageable with size")
    void shouldCreatePageableWithSize() {
        Pageable pageable = Pageable.ofSize(50);

        assertSoftly(softly -> {
            softly.assertThat(pageable.page()).isEqualTo(1L);
            softly.assertThat(pageable.size()).isEqualTo(50);
        });
    }

    @Test
    @DisplayName("Should navigate next")
    void shouldNext() {
        Pageable pageable = Pageable.ofSize(1).newPage(2);
        Pageable next = pageable.next();

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
        Pageable pageable = Pageable.ofPage(5);

        assertSoftly(softly -> {
            softly.assertThat(pageable.page()).isEqualTo(5L);
            softly.assertThat(pageable.size()).isEqualTo(10);
        });
    }

    @Test
    @DisplayName("Should be displayable as String with toString")
    void shouldPageableDisplayAsString() {

        assertSoftly(softly -> softly.assertThat(Pageable.ofSize(60).toString())
              .isEqualTo("Pageable{page=1, size=60}"));

        assertSoftly(softly -> softly.assertThat(Pageable.ofSize(80).sortBy(Sort.desc("yearBorn"), Sort.asc("monthBorn"),
                        Sort.asc("id")).toString())
              .isEqualTo("Pageable{page=1, size=80, yearBorn DESC, monthBorn ASC, id ASC}"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when page is not present")
    void shouldReturnErrorWhenThereIsIllegalArgument() {
        Pageable p1 = Pageable.ofPage(1);
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.ofPage(0));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.ofPage(-1));
        assertThatIllegalArgumentException().isThrownBy(() -> p1.newSize(-1));
        assertThatIllegalArgumentException().isThrownBy(() -> p1.newSize(0));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.ofSize(0));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.ofSize(-1));
    }

    @Test
    public void shouldHaveEmptySortListWhenSortIsNullOrEmpty() {
        Pageable p = Pageable.ofSize(2).sortBy(Sort.asc("Id"));

        assertSoftly(softly -> {
            softly.assertThat(p.sorts()).isEqualTo(List.of(Sort.asc("Id")));
            softly.assertThat(p.sortBy().sorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(p.sortBy((Iterable<Sort>) null).sorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(p.sortBy((Sort[]) null).sorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(p.sortBy(new Sort[0]).sorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(p.sortBy(List.of()).sorts()).isEqualTo(Collections.EMPTY_LIST);
        });
    }

    @Test
    public void shouldCreatePageableSort() {
        Pageable pageable = Pageable.ofSize(3).sortBy(Sort.asc("name"));
        Assertions.assertNotNull(pageable);
        Assertions.assertEquals(1L, pageable.page());
        Assertions.assertEquals(3, pageable.size());
        assertThat(pageable.sorts())
                .hasSize(1)
                .contains(Sort.asc("name"));
    }

    @Test
    public void shouldNotModifySort() {
        Pageable pageable = Pageable.ofSize(3).sortBy(Sort.asc("name"));
        List<Sort> sorts = pageable.sorts();
        Assertions.assertThrows(UnsupportedOperationException.class, sorts::clear);

    }

    @Test
    public void shouldNotModifySortOnNextPage() {
        Pageable pageable = Pageable.ofSize(3).sortBy(Sort.asc("name"), Sort.desc("age"));
        Pageable next = pageable.next();
        Assertions.assertEquals(1L, pageable.page());
        Assertions.assertEquals(3, pageable.size());
        assertThat(pageable.sorts())
                .hasSize(2)
                .contains(Sort.asc("name"), Sort.desc("age"));
        Assertions.assertEquals(2L, next.page());
        Assertions.assertEquals(3, next.size());

        assertThat(next.sorts())
                .hasSize(2)
                .contains(Sort.asc("name"), Sort.desc("age"));

    }

    @Test
    @DisplayName("Page number should be replaced on new instance of Pageable")
    public void shouldReplacePage() {
        Pageable p6 = Pageable.ofSize(75).newPage(6).sortBy(Sort.desc("price"));
        Pageable p7 = p6.newPage(7);

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
    public void shouldReplaceSize() {
        Pageable s90 = Pageable.ofPage(4).newSize(90);
        Pageable s80 = s90.newSize(80);

        assertSoftly(softly -> {
            softly.assertThat(s80.size()).isEqualTo(80);
            softly.assertThat(s90.size()).isEqualTo(90);
            softly.assertThat(s90.page()).isEqualTo(4L);
            softly.assertThat(s80.page()).isEqualTo(4L);
        });
    }

    @Test
    @DisplayName("Sorts should be replaced on new instance of Pageable")
    public void shouldReplaceSorts() {
        Pageable p1 = Pageable.ofSize(55).sortBy(Sort.desc("lastName"), Sort.asc("firstName"));
        Pageable p2 = p1.sortBy(Sort.asc("firstName"), Sort.asc("lastName"));

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
    public void shouldUseAscAlias() {
        Pageable pageable = Pageable.ofSize(55).asc("name");
        Assertions.assertNotNull(pageable);
        assertSoftly(softly -> {
            softly.assertThat(pageable.sorts()).hasSize(1).contains(Sort.asc("name"));
            softly.assertThat(pageable.page()).isEqualTo(1L);
            softly.assertThat(pageable.size()).isEqualTo(55);
        });
        assertThrows(NullPointerException.class, () -> Pageable.ofSize(55).asc(null));
    }

    @Test
    public void shouldUseDescAlias() {
        Pageable pageable = Pageable.ofSize(55).desc("name");
        Assertions.assertNotNull(pageable);
        assertSoftly(softly -> {
            softly.assertThat(pageable.sorts()).hasSize(1).contains(Sort.desc("name"));
            softly.assertThat(pageable.page()).isEqualTo(1L);
            softly.assertThat(pageable.size()).isEqualTo(55);
        });
        assertThrows(NullPointerException.class, () -> Pageable.ofSize(55).asc(null));
    }
}

