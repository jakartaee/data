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
package jakarta.data.expression.path;

import jakarta.data.expression.ComparableExpression;
import jakarta.data.expression.NavigableExpression;
import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.NavigableAttribute;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ComparablePath}
 */
class ComparablePathTest {

    static class Publisher {
        Integer rating;
    }

    static class Author {
        Publisher publisher;
    }

    interface _Publisher {
        ComparableAttribute<Publisher, Integer> rating =
                ComparableAttribute.of(Publisher.class, "rating", Integer.class);
    }

    interface _Author {
        NavigableAttribute<Author, Publisher> publisher =
                NavigableAttribute.of(Author.class, "publisher", Publisher.class);
    }

    @Test
    @DisplayName("should create ComparablePath from NavigableExpression and ComparableAttribute")
    void shouldCreateComparablePath() {
        NavigableExpression<Author, Publisher> expr = _Author.publisher;
        ComparableAttribute<Publisher, Integer> attr = _Publisher.rating;

        var path = ComparablePath.of(expr, attr);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(path).isNotNull();
            soft.assertThat(path).isInstanceOf(ComparableExpression.class);
        });
    }
}
