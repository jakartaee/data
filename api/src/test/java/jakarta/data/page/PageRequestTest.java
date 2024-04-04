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
        assertSoftly(softly -> {
            softly.assertThat(PageRequest.ofSize(60).toString())
              .isEqualTo("PageRequest{page=1, size=60, mode=OFFSET}");

            softly.assertThat(PageRequest.ofSize(80).toString())
                    .isEqualTo("PageRequest{page=1, size=80, mode=OFFSET}");
        });
    }

    @Test
    @DisplayName("The requestTotal configuration must be preserved when adding subsequent configuration.")
    void shouldRequestTotalConfigBePreserved() {
        PageRequest<?> pageRequest1 = PageRequest.ofPage(1).withTotal().size(70);

        assertSoftly(softly -> {
            softly.assertThat(pageRequest1.page()).isEqualTo(1L);
            softly.assertThat(pageRequest1.size()).isEqualTo(70);
            softly.assertThat(pageRequest1.requestTotal()).isEqualTo(true);
        });

        PageRequest<?> pageRequest2 = PageRequest.ofSize(80).withoutTotal().page(2);

        assertSoftly(softly -> {
            softly.assertThat(pageRequest2.page()).isEqualTo(2L);
            softly.assertThat(pageRequest2.size()).isEqualTo(80);
            softly.assertThat(pageRequest2.requestTotal()).isEqualTo(false);
        });
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
    @DisplayName("Page number should be replaced on new instance of PageRequest")
    void shouldReplacePage() {
        PageRequest<?> p6 = PageRequest.ofSize(75).page(6);
        PageRequest<?> p7 = p6.page(7);

        assertSoftly(softly -> {
            softly.assertThat(p7.page()).isEqualTo(7L);
            softly.assertThat(p6.page()).isEqualTo(6L);
            softly.assertThat(p7.size()).isEqualTo(75);
            softly.assertThat(p6.size()).isEqualTo(75);
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

}
