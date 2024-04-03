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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.page.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.data.page.PageRequest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class PageRecordTest {

    @Test
    @DisplayName("The custom constructor can create instances where moreResults is inferred from the other record components.")
    void shouldCreateInstanceWithCustomConstructor() {

        PageRequest<String> page2Request = PageRequest.of(String.class).page(2).size(4);
        List<String> page2Content = List.of("E", "F", "G", "H");
        PageRecord<String> page2 = new PageRecord<>(page2Request, page2Content, -1L); // unknown total elements

        assertSoftly(softly -> {
            softly.assertThat(page2.content()).isEqualTo(page2Content);
            softly.assertThat(page2.hasContent()).isEqualTo(true);
            softly.assertThat(page2.hasNext()).isEqualTo(true);
            softly.assertThat(page2.hasPrevious()).isEqualTo(true);
            softly.assertThat(page2.hasTotals()).isEqualTo(false);
            softly.assertThat(page2.moreResults()).isEqualTo(true);
            softly.assertThat(page2.nextPageRequest()).isEqualTo(PageRequest.of(String.class).page(3).size(4));
            softly.assertThat(page2.numberOfElements()).isEqualTo(4);
            softly.assertThat(page2.previousPageRequest()).isEqualTo(PageRequest.of(String.class).page(1).size(4));
        });
        assertThatThrownBy(() -> page2.totalElements()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> page2.totalPages()).isInstanceOf(IllegalStateException.class);

        PageRequest<String> page5Request = PageRequest.of(String.class).page(5).size(4);
        List<String> page5Content = List.of("Q", "R");
        PageRecord<String> page5 = new PageRecord<>(page5Request, page5Content, -999L); // unknown total elements

        assertSoftly(softly -> {
            softly.assertThat(page5.content()).isEqualTo(page5Content);
            softly.assertThat(page5.hasContent()).isEqualTo(true);
            softly.assertThat(page5.hasNext()).isEqualTo(false);
            softly.assertThat(page5.hasPrevious()).isEqualTo(true);
            softly.assertThat(page5.hasTotals()).isEqualTo(false);
            softly.assertThat(page5.moreResults()).isEqualTo(false);
            softly.assertThat(page5.numberOfElements()).isEqualTo(2);
            softly.assertThat(page5.previousPageRequest()).isEqualTo(PageRequest.of(String.class).page(4).size(4));
        });
        assertThatThrownBy(() -> page5.nextPageRequest()).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> page5.totalElements()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> page5.totalPages()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Instances created by the record constructor must adhere to the requirements of the Page interface.")
    void shouldCreateInstanceWithRecordConstructor() {

        PageRequest<String> page1Request = PageRequest.of(String.class).page(1).size(5);
        List<String> page1Content = List.of("A", "B", "C", "D", "E");
        PageRecord<String> page1 = new PageRecord<>(page1Request, page1Content, 18L, true);

        assertSoftly(softly -> {
            softly.assertThat(page1.content()).isEqualTo(page1Content);
            softly.assertThat(page1.hasContent()).isEqualTo(true);
            softly.assertThat(page1.hasNext()).isEqualTo(true);
            softly.assertThat(page1.hasPrevious()).isEqualTo(false);
            softly.assertThat(page1.hasTotals()).isEqualTo(true);
            softly.assertThat(page1.moreResults()).isEqualTo(true);
            softly.assertThat(page1.nextPageRequest()).isEqualTo(PageRequest.of(String.class).page(2).size(5));
            softly.assertThat(page1.numberOfElements()).isEqualTo(5);
            softly.assertThat(page1.totalElements()).isEqualTo(18L);
            softly.assertThat(page1.totalPages()).isEqualTo(4);
        });
        assertThatThrownBy(() -> page1.previousPageRequest()).isInstanceOf(NoSuchElementException.class);

        PageRequest<String> page3Request = PageRequest.of(String.class).page(3).size(5);
        List<String> page3Content = List.of("K", "L", "M", "N", "O");
        PageRecord<String> page3 = new PageRecord<>(page3Request, page3Content, 18L, true);

        assertSoftly(softly -> {
            softly.assertThat(page3.content()).isEqualTo(page3Content);
            softly.assertThat(page3.hasContent()).isEqualTo(true);
            softly.assertThat(page3.hasNext()).isEqualTo(true);
            softly.assertThat(page3.hasPrevious()).isEqualTo(true);
            softly.assertThat(page3.hasTotals()).isEqualTo(true);
            softly.assertThat(page3.moreResults()).isEqualTo(true);
            softly.assertThat(page3.nextPageRequest()).isEqualTo(PageRequest.of(String.class).page(4).size(5));
            softly.assertThat(page3.numberOfElements()).isEqualTo(5);
            softly.assertThat(page3.previousPageRequest()).isEqualTo(PageRequest.of(String.class).page(2).size(5));
            softly.assertThat(page3.totalElements()).isEqualTo(18L);
            softly.assertThat(page3.totalPages()).isEqualTo(4);
        });
    }

}
