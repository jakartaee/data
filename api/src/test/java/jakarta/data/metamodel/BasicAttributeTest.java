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
package jakarta.data.metamodel;

import java.util.List;
import java.util.Map;

import jakarta.data.constraint.NotNull;
import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity._Book;
import jakarta.data.restrict.BasicRestriction;
import jakarta.data.restrict.Restriction;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BasicAttributeTest {

    @DisplayName("Compare fields of a BasicAttribute")
    @Test
    void shouldCompareFields() {

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(_Book.CHAPTERTITLES)
                .isEqualTo(_Book.chapterTitles.name());
            soft.assertThat(List.class)
                .isEqualTo(_Book.chapterTitles.type());
            soft.assertThat(Book.class)
                .isEqualTo(_Book.chapterTitles.declaringType());
        });
    }

    @DisplayName("Create an instance of BasicAttribute")
    @Test
    void shouldCreateBasicAttribute() {
        BasicAttribute<Book, Map<Integer, Integer>> pagesPerChapter =
                BasicAttribute.of(Book.class,
                                  "pagesPerChapter",
                                  new TypeToken<Map<Integer, Integer>>() {});

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(pagesPerChapter.name())
                .isEqualTo("pagesPerChapter");
            soft.assertThat(pagesPerChapter.type())
                .isEqualTo(Map.class);
            soft.assertThat(pagesPerChapter.declaringType())
                .isEqualTo(Book.class);
        });
    }

    @DisplayName("Create a notNull restriction on a BasicAttribute")
    @Test
    void shouldCompareNotNull() {
        Restriction<Book> hasChapters = _Book.chapterTitles.notNull();
        @SuppressWarnings("unchecked")
        BasicRestriction<Book, List<String>> basic =
                (BasicRestriction<Book, List<String>>) hasChapters;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.expression())
                .isEqualTo(_Book.chapterTitles);
            soft.assertThat(basic.constraint())
                .isEqualTo(NotNull.instance());
        });
    }

}