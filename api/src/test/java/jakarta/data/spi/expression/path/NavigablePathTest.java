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
package jakarta.data.spi.expression.path;

import jakarta.data.expression.NavigableExpression;
import jakarta.data.metamodel.NavigableAttribute;
import jakarta.data.spi.expression.path.NavigablePath;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link NavigablePath}
 */
class NavigablePathTest {

    static class Publisher {
        String name;
    }

    static class Book {
        Publisher publisher;
    }

    interface _Publisher {
        NavigableAttribute<Publisher, String> name =
                NavigableAttribute.of(Publisher.class, "name", String.class);
    }

    interface _Book {
        NavigableAttribute<Book, Publisher> publisher =
                NavigableAttribute.of(Book.class, "publisher", Publisher.class);
    }

    @Test
    @DisplayName("should create NavigablePath from expression and attribute")
    void shouldCreateNavigablePath() {
        NavigableExpression<Book, Publisher> expr = _Book.publisher;
        NavigableAttribute<Publisher, String> attr = _Publisher.name;

        var path = NavigablePath.of(expr, attr);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(path).isNotNull();
            soft.assertThat(path).isInstanceOf(NavigableExpression.class);
            soft.assertThat(path.toString())
                .isEqualTo("book.publisher.name");
        });
    }
}
