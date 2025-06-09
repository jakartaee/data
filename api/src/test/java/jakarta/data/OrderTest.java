/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package jakarta.data;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class OrderTest {

    @Test
    @DisplayName("should create Order via varargs")
    void shouldCreateOrderFromVarargs() {
        var order = Order.by(Sort.asc("name"), Sort.desc("createdAt"));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(order.sorts()).hasSize(2);
            soft.assertThat(order.sorts().get(0).property()).isEqualTo("name");
            soft.assertThat(order.sorts().get(0).isAscending()).isTrue();
            soft.assertThat(order.sorts().get(1).property()).isEqualTo("createdAt");
            soft.assertThat(order.sorts().get(1).isAscending()).isFalse();
        });
    }

    @Test
    @DisplayName("should create Order via list")
    void shouldCreateOrderFromList() {
        var list = List.of(Sort.desc("priority"));
        var order = Order.by(list);

        SoftAssertions.assertSoftly(soft ->
            soft.assertThat(order.sorts()).containsExactlyElementsOf(list));
    }

    @Test
    @DisplayName("should support equals and hashCode for same sorts")
    void shouldSupportEqualsAndHashCode() {
        var first = Order.by(Sort.asc("updated"));
        var second = Order.by(Sort.asc("updated"));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(first).isEqualTo(second);
            soft.assertThat(first.hashCode()).isEqualTo(second.hashCode());
        });
    }

    @Test
    @DisplayName("should iterate over sort criteria in order")
    void shouldSupportIteration() {
        var order = Order.by(Sort.asc("title"), Sort.desc("author"));

        var iterator = order.iterator();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(iterator.next()).isEqualTo(Sort.asc("title"));
            soft.assertThat(iterator.next()).isEqualTo(Sort.desc("author"));
            soft.assertThat(iterator.hasNext()).isFalse();
        });
    }

    @Test
    @DisplayName("should return expected string from toString")
    void shouldPrintSortsInToString() {
        var order = Order.by(Sort.asc("published"));

        SoftAssertions.assertSoftly(soft -> soft.assertThat(order.toString()).contains("published"));
    }
}