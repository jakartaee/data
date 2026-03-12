/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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
package jakarta.data.spi.expression.path;

import java.time.LocalDate;

import jakarta.data.expression.NavigableExpression;
import jakarta.data.metamodel.BooleanAttribute;
import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.NavigableAttribute;
import jakarta.data.metamodel.NumericAttribute;
import jakarta.data.metamodel.TemporalAttribute;
import jakarta.data.metamodel.TextAttribute;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link NavigablePath}
 */
class NavigablePathTest {

    static enum BusinessType {
        COMMERCIAL,
        GOVERNMENT,
        NONPROFIT
    }

    static class BusinessInfo {
        Boolean active;
        LocalDate founded;
        String name;
        BusinessType type;
        int zipcode;
    }

    static class Publisher {
        BusinessInfo info;
    }

    static class Book {
        Publisher publisher;
    }

    interface _BusinessInfo {
        BooleanAttribute<BusinessInfo> active =
                BooleanAttribute.of(BusinessInfo.class, "active", Boolean.class);

        TemporalAttribute<BusinessInfo, LocalDate> founded =
                TemporalAttribute.of(BusinessInfo.class, "founded", LocalDate.class);

        TextAttribute<BusinessInfo> name =
                TextAttribute.of(BusinessInfo.class, "name");

        ComparableAttribute<BusinessInfo, BusinessType> type =
                ComparableAttribute.of(BusinessInfo.class, "type", BusinessType.class);

        NumericAttribute<BusinessInfo, Integer> zipcode =
                NumericAttribute.of(BusinessInfo.class, "zipcode", int.class);
    }

    interface _Publisher {
        NavigableAttribute<Publisher, BusinessInfo> info =
                NavigableAttribute.of(Publisher.class, "info", BusinessInfo.class);
    }

    interface _Book {
        NavigableAttribute<Book, Publisher> publisher =
                NavigableAttribute.of(Book.class, "publisher", Publisher.class);
    }

    @Test
    @DisplayName("should create BooleanPath from path and attribute")
    void shouldCreateBooleanPath() {
        BooleanAttribute<Book> publisherInfoActive =
                _Book.publisher.navigate(_Publisher.info)
                               .navigate(_BusinessInfo.active);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(publisherInfoActive.name())
                .isEqualTo("publisher.info.active");
            soft.assertThat(publisherInfoActive.asc().property())
                .isEqualTo("publisher.info.active");
            soft.assertThat(publisherInfoActive.desc().property())
                .isEqualTo("publisher.info.active");
            soft.assertThat(publisherInfoActive.toString())
                .isEqualTo("book.publisher.info.active");
        });
    }

    @Test
    @DisplayName("should create ComparablePath from path and attribute")
    void shouldCreateComparablePath() {
        ComparableAttribute<Book, BusinessType> publisherBusinessInfoType =
                _Book.publisher.navigate(_Publisher.info)
                               .navigate(_BusinessInfo.type);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(publisherBusinessInfoType.name())
                .isEqualTo("publisher.info.type");
            soft.assertThat(publisherBusinessInfoType.asc().property())
                .isEqualTo("publisher.info.type");
            soft.assertThat(publisherBusinessInfoType.desc().property())
                .isEqualTo("publisher.info.type");
            soft.assertThat(publisherBusinessInfoType.toString())
                .isEqualTo("book.publisher.info.type");
        });
    }

    @Test
    @DisplayName("should create NavigablePath from path and attribute")
    void shouldCreateNavigablePath() {

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(_Book.publisher.name())
                .isEqualTo("publisher");
            soft.assertThat(_Book.publisher.toString())
                .isEqualTo("book.publisher");
        });

        NavigableExpression<Book, BusinessInfo> publisherInfo =
                _Book.publisher.navigate(_Publisher.info);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(publisherInfo.toString())
                .isEqualTo("book.publisher.info");
        });
    }

    @Test
    @DisplayName("should create NumericPath from path and attribute")
    void shouldCreateNumericPath() {
        NumericAttribute<Book, Integer> publisherInfoZipcode =
                _Book.publisher.navigate(_Publisher.info)
                               .navigate(_BusinessInfo.zipcode);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(publisherInfoZipcode.name())
                .isEqualTo("publisher.info.zipcode");
            soft.assertThat(publisherInfoZipcode.asc().property())
                .isEqualTo("publisher.info.zipcode");
            soft.assertThat(publisherInfoZipcode.desc().property())
                .isEqualTo("publisher.info.zipcode");
            soft.assertThat(publisherInfoZipcode.toString())
                .isEqualTo("book.publisher.info.zipcode");
        });
    }

    @Test
    @DisplayName("should create TemporalPath from path and attribute")
    void shouldCreateTemporalPath() {
        TemporalAttribute<Book, LocalDate> publisherInfoFounded =
                _Book.publisher.navigate(_Publisher.info)
                               .navigate(_BusinessInfo.founded);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(publisherInfoFounded.name())
                .isEqualTo("publisher.info.founded");
            soft.assertThat(publisherInfoFounded.asc().property())
                .isEqualTo("publisher.info.founded");
            soft.assertThat(publisherInfoFounded.desc().property())
                .isEqualTo("publisher.info.founded");
            soft.assertThat(publisherInfoFounded.toString())
                .isEqualTo("book.publisher.info.founded");
        });
    }

    @Test
    @DisplayName("should create TextPath from path and attribute")
    void shouldCreateTextPath() {
        TextAttribute<Book> publisherInfoName =
                _Book.publisher.navigate(_Publisher.info)
                               .navigate(_BusinessInfo.name);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(publisherInfoName.name())
                .isEqualTo("publisher.info.name");
            soft.assertThat(publisherInfoName.asc().property())
                .isEqualTo("publisher.info.name");
            soft.assertThat(publisherInfoName.descIgnoreCase().property())
                .isEqualTo("publisher.info.name");
            soft.assertThat(publisherInfoName.toString())
                .isEqualTo("book.publisher.info.name");
        });
    }
}
