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

import jakarta.data.constraint.EqualTo;
import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity._Book;
import jakarta.data.restrict.BasicRestriction;
import jakarta.data.restrict.Restriction;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BooleanAttributeTest {

    @DisplayName("Create an isFalse restriction on a BooleanAttribute")
    @Test
    void shouldCompareIsFalse() {
        Restriction<Book> nonfiction = _Book.fiction.isFalse();
        var basic = (BasicRestriction<?, ?>) nonfiction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.expression()).isEqualTo(_Book.fiction);
            soft.assertThat(basic.constraint()).isEqualTo(EqualTo.value(false));
        });
    }

    @DisplayName("Create an isTrue restriction on a BooleanAttribute")
    @Test
    void shouldCompareIsTrue() {
        Restriction<Book> fiction = _Book.fiction.isTrue();
        var basic = (BasicRestriction<?, ?>) fiction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.expression()).isEqualTo(_Book.fiction);
            soft.assertThat(basic.constraint()).isEqualTo(EqualTo.value(true));
        });
    }

    @DisplayName("Create an instance of BooleanAttribute")
    @Test
    void shouldCreateBooleanAttribute() {
        BooleanAttribute<Book> hardcover =
                BooleanAttribute.of(Book.class, "hardcover", Boolean.class);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(hardcover.type())
                .isEqualTo(Boolean.class);
            soft.assertThat(hardcover.declaringType())
                .isEqualTo(Book.class);
            soft.assertThat(hardcover.name())
                .isEqualTo("hardcover");
        });

        // Instance created by Mock metamodel class:
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(_Book.fiction.type())
                .isEqualTo(boolean.class);
            soft.assertThat(_Book.fiction.declaringType())
                .isEqualTo(Book.class);
            soft.assertThat(_Book.fiction.name())
                .isEqualTo("fiction");
        });
    }

}