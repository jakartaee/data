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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel;

import jakarta.data.expression.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;



/**
 * Tests for {@link NavigableAttribute} and {@link NavigableExpression}
 */
class NavigableAttributeTest {

    static class Address {
        String city;
    }

    static class Publisher {
        Address address;
        String name;
        int score;
        LocalDate founded;
    }

    static class Author {
        Publisher publisher;
    }

    interface _Address {
        TextAttribute<Address> city = TextAttribute.of(Address.class, "city");
    }

    interface _Publisher {
        NavigableAttribute<Publisher, Address> address = NavigableAttribute.of(Publisher.class, "address", Address.class);
        TextAttribute<Publisher> name = TextAttribute.of(Publisher.class, "name");
        NumericAttribute<Publisher, Integer> score = NumericAttribute.of(Publisher.class, "score", Integer.class);
        TemporalAttribute<Publisher, LocalDate> founded = TemporalAttribute.of(Publisher.class, "founded", LocalDate.class);
    }

    interface _Author {
        NavigableAttribute<Author, Publisher> publisher = NavigableAttribute.of(Author.class, "publisher", Publisher.class);
    }

    @Test
    @DisplayName("should navigate to text attribute from navigable")
    void shouldNavigateToTextAttribute() {
        var path = _Author.publisher.navigate(_Publisher.name);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(path).isInstanceOf(TextExpression.class);
        });
    }

    @Test
    @DisplayName("should navigate to numeric attribute from navigable")
    void shouldNavigateToNumericAttribute() {
        var path = _Author.publisher.navigate(_Publisher.score);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(path).isInstanceOf(NumericExpression.class);
        });
    }

    @Test
    @DisplayName("should navigate to temporal attribute from navigable")
    void shouldNavigateToTemporalAttribute() {
        var path = _Author.publisher.navigate(_Publisher.founded);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(path).isInstanceOf(TemporalExpression.class);
        });
    }

    @Test
    @DisplayName("should navigate to nested navigable attribute")
    void shouldNavigateToNestedNavigableAttribute() {
        var path = _Author.publisher
                .navigate(_Publisher.address)
                .navigate(_Address.city);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(path).isInstanceOf(TextExpression.class);
        });
    }
}