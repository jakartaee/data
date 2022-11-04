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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class PageableTest {

    @Test
    @DisplayName("Should correctly paginate")
    void shouldCreatePageable() {
        Pageable pageable = Pageable.of(2, 6);

        assertSoftly(softly -> {
            softly.assertThat(pageable.getPage()).isEqualTo(2L);
            softly.assertThat(pageable.getSize()).isEqualTo(6L);
        });
    }

    @Test
    @DisplayName("Should create pageable with size")
    void shouldCreatePageableWithSize() {
        Pageable pageable = Pageable.size(50);

        assertSoftly(softly -> {
            softly.assertThat(pageable.getPage()).isEqualTo(1L);
            softly.assertThat(pageable.getSize()).isEqualTo(50L);
        });
    }

    @Test
    @DisplayName("Should navigate next")
    void shouldNext() {
        Pageable pageable = Pageable.of(2, 1);
        Pageable next = pageable.next();

        assertSoftly(softly -> {
            softly.assertThat(pageable.getPage()).isEqualTo(2L);
            softly.assertThat(pageable.getSize()).isEqualTo(1L);
            softly.assertThat(next.getPage()).isEqualTo(3L);
            softly.assertThat(next.getSize()).isEqualTo(1L);
        });
    }

    @Test
    @DisplayName("Should create a new Pageable at the given page with a default size of 10")
    void shouldCreatePage() {
        Pageable pageable = Pageable.page(5);

        assertSoftly(softly -> {
            softly.assertThat(pageable.getPage()).isEqualTo(5L);
            softly.assertThat(pageable.getSize()).isEqualTo(10L);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when page is not present")
    void shouldReturnErrorWhenThereIsIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.page(0));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.page(-1));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.of(1, -1));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.of(1, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.size(0));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.size(-1));
    }

    @Test
    public void shouldReturnNPEWhenSortIsNull() {
        Assertions.assertThrows(NullPointerException.class, () ->
                Pageable.of(1L, 2L, (Sort) null));
        Assertions.assertThrows(NullPointerException.class, () ->
                Pageable.of(1L, 2L, (Iterable<Sort>) null));

        Assertions.assertThrows(NullPointerException.class, () ->
                Pageable.of(1L, 2L, Sort.asc("name"), null));
    }

    @Test
    public void shouldCreatePageableSort() {
        Pageable pageable = Pageable.of(1L, 3L, Sort.asc("name"));
        Assertions.assertNotNull(pageable);
        Assertions.assertEquals(1L, pageable.getPage());
        Assertions.assertEquals(3L, pageable.getSize());
        assertThat(pageable.getSorts())
                .hasSize(1)
                .contains(Sort.asc("name"));
    }

    @Test
    public void shouldNotModifySort() {
        Pageable pageable = Pageable.of(1L, 3L, Sort.asc("name"));
        List<Sort> sorts = pageable.getSorts();
        Assertions.assertThrows(UnsupportedOperationException.class, ()-> sorts.clear());

    }

    @Test
    public void shouldNotModifySortOnNextPage() {
        Pageable pageable = Pageable.of(1L, 3L, Sort.asc("name"), Sort.desc("age"));
        Pageable next = pageable.next();
        Assertions.assertEquals(1L, pageable.getPage());
        Assertions.assertEquals(3L, pageable.getSize());
        assertThat(pageable.getSorts())
                .hasSize(2)
                .contains(Sort.asc("name"), Sort.desc("age"));
        Assertions.assertEquals(2L, next.getPage());
        Assertions.assertEquals(3L, next.getSize());

        assertThat(next.getSorts())
                .hasSize(2)
                .contains(Sort.asc("name"), Sort.desc("age"));

    }
}

