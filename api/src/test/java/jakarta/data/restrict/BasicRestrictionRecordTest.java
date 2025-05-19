/*
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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
package jakarta.data.restrict;

import jakarta.data.constraint.Between;
import jakarta.data.constraint.EqualTo;
import jakarta.data.constraint.GreaterThan;
import jakarta.data.constraint.GreaterThanOrEqual;
import jakarta.data.constraint.In;
import jakarta.data.constraint.LessThan;
import jakarta.data.constraint.LessThanOrEqual;
import jakarta.data.constraint.Like;
import jakarta.data.constraint.NotBetween;
import jakarta.data.constraint.NotEqualTo;
import jakarta.data.constraint.NotIn;
import jakarta.data.constraint.NotLike;
import jakarta.data.constraint.NotNull;
import jakarta.data.constraint.Null;
import jakarta.data.metamodel.BasicAttribute;
import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity._Book;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@SuppressWarnings("unchecked")
class BasicRestrictionRecordTest {


    @Test
    @DisplayName("should create an EqualTo restriction by default when using 'equalTo' without negation")
    void shouldCreateBasicRestrictionWithDefaultNegation() {
        var restriction = (BasicRestriction<Book, String>) _Book.title.equalTo("Java Guide");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            soft.assertThat(restriction.constraint()).isEqualTo(EqualTo.value("Java Guide"));
            soft.assertThat(restriction.constraint()).isInstanceOf(EqualTo.class);
        });
    }

    @Test
    @DisplayName("should negate an EqualTo restriction into NotEqualTo using 'negate'")
    void shouldCreateBasicRestrictionWithExplicitNegation() {
        var restriction = (BasicRestriction<Book, String>) _Book.title.equalTo("Java Guide").negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            soft.assertThat(restriction.constraint()).isEqualTo(NotEqualTo.value("Java Guide"));
            soft.assertThat(restriction.constraint()).isInstanceOf(NotEqualTo.class);
        });
    }

    @Test
    @DisplayName("should negate LessThanOrEqual into GreaterThan")
    void shouldNegateLTERestriction() {
        var lessThanEqual = (BasicRestriction<Book, Integer>) _Book.numChapters.lessThanEqual(10);
        var negated = (BasicRestriction<Book, Integer>) lessThanEqual.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(lessThanEqual.expression()).isEqualTo(_Book.numChapters);
            soft.assertThat(lessThanEqual.constraint()).isEqualTo(LessThanOrEqual.max(10));
            soft.assertThat(lessThanEqual.constraint()).isInstanceOf(LessThanOrEqual.class);

            soft.assertThat(negated.expression()).isEqualTo(_Book.numChapters);
            soft.assertThat(negated.constraint()).isEqualTo(GreaterThan.bound(10));
            soft.assertThat(negated.constraint()).isInstanceOf(GreaterThan.class);
        });
    }

    @Test
    @DisplayName("should return to the original constraint after double negation")
    void shouldNegateNegatedRestriction() {
        var original = (BasicRestriction<Book, String>)
                _Book.title.equalTo("A Developer's Guide to Jakarta Data");
        var doubleNegated = (BasicRestriction<Book, Integer>)
                original.negate().negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(original.expression()).isEqualTo(_Book.title);
            soft.assertThat(original.constraint()).isEqualTo(EqualTo.value("A Developer's Guide to Jakarta Data"));
            soft.assertThat(original.constraint()).isInstanceOf(EqualTo.class);

            soft.assertThat(doubleNegated.expression()).isEqualTo(_Book.title);
            soft.assertThat(doubleNegated.constraint()).isEqualTo(EqualTo.value("A Developer's Guide to Jakarta Data"));
            soft.assertThat(doubleNegated.constraint()).isInstanceOf(EqualTo.class);
        });
    }

    @Test
    @DisplayName("should format toString output for greaterThan correctly")
    void shouldOutputToString() {
        var restriction = (BasicRestriction<Book, Integer>) _Book.numPages.greaterThan(100);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.numPages);
            soft.assertThat(restriction.constraint()).isEqualTo(GreaterThan.bound(100));
            soft.assertThat(restriction.constraint()).isInstanceOf(GreaterThan.class);
            soft.assertThat(restriction.toString()).isEqualTo("numPages > 100");
        });
    }

    @Test
    @DisplayName("should create NotEqualTo from 'notEqualTo' method directly")
    void shouldSupportNegatedRestrictionUsingDefaultConstructor() {
        var restriction = (BasicRestriction<Book, String>) _Book.author.notEqualTo("Unknown");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.author);
            soft.assertThat(restriction.constraint()).isEqualTo(NotEqualTo.value("Unknown"));
            soft.assertThat(restriction.constraint()).isInstanceOf(NotEqualTo.class);
        });
    }

    @Test
    @DisplayName("should throw NullPointerException when attribute name is null")
    void shouldThrowExceptionWhenAttributeIsNull() {
        assertThatThrownBy(() -> BasicAttribute.of(Book.class, null, Object.class).equalTo("testValue"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("The name is required");
    }

    @Test
    @DisplayName("should throw NullPointerException when value passed to equalTo is null")
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatThrownBy(() -> _Book.title.equalTo((String) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("The value argument is required.");
    }

    @Test
    @DisplayName("should create GreaterThanOrEqual restriction correctly")
    void shouldCreateGreaterThanOrEqualRestriction() {
        var restriction = (BasicRestriction<Book, Integer>) _Book.numPages.greaterThanEqual(200);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.numPages);
            soft.assertThat(restriction.constraint()).isEqualTo(GreaterThanOrEqual.min(200));
            soft.assertThat(restriction.constraint()).isInstanceOf(GreaterThanOrEqual.class);
        });
    }

    @Test
    @DisplayName("should create LessThan restriction correctly")
    void shouldCreateLessThanRestriction() {
        var restriction = (BasicRestriction<Book, Integer>) _Book.numPages.lessThan(50);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.numPages);
            soft.assertThat(restriction.constraint()).isEqualTo(LessThan.bound(50));
            soft.assertThat(restriction.constraint()).isInstanceOf(LessThan.class);
        });
    }

    @Test
    @DisplayName("should create Null constraint correctly")
    void shouldCreateNullRestriction() {
        var restriction = (BasicRestriction<Book, String>) _Book.title.isNull();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            soft.assertThat(restriction.constraint()).isEqualTo(Null.instance());
            soft.assertThat(restriction.constraint()).isInstanceOf(Null.class);
        });
    }

    @DisplayName("should create NotNull constraint correctly")
    @Test
    void shouldCreateNotNullRestriction() {
        var restriction = (BasicRestriction<Book, String>) _Book.title.isNull().negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            soft.assertThat(restriction.constraint()).isEqualTo(NotNull.instance());
            soft.assertThat(restriction.constraint()).isInstanceOf(NotNull.class);
        });
    }

    @DisplayName("should create Between constraint correctly")
    @Test
    void shouldCreateBetweenRestriction() {
        var restriction = (BasicRestriction<Book, Integer>) _Book.numChapters.between(5, 15);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.numChapters);
            soft.assertThat(restriction.constraint()).isEqualTo(Between.bounds(5, 15));
            soft.assertThat(restriction.constraint()).isInstanceOf(Between.class);
        });
    }

    @DisplayName("should create In constraint correctly")
    @Test
    void shouldCreateInRestriction() {
        var restriction = (BasicRestriction<Book, String>) _Book.author.in("Alice", "Bob");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.author);
            soft.assertThat(restriction.constraint()).isEqualTo(In.values("Alice", "Bob"));
            soft.assertThat(restriction.constraint()).isInstanceOf(In.class);
        });
    }

    @DisplayName("should create NotIn constraint correctly")
    @Test
    void shouldCreateNotInRestriction() {
        var restriction = (BasicRestriction<Book, String>) _Book.author.in("Alice", "Bob").negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.author);
            soft.assertThat(restriction.constraint()).isEqualTo(NotIn.values("Alice", "Bob"));
            soft.assertThat(restriction.constraint()).isInstanceOf(NotIn.class);
        });
    }

    @DisplayName("should create In, using Set, constraint correctly")
    @Test
    void shouldCreateInAsSetRestriction() {
        var restriction = (BasicRestriction<Book, String>) _Book.author.in(Set.of("Alice", "Bob"));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.author);
            soft.assertThat(restriction.constraint()).isEqualTo(In.values(Set.of("Alice", "Bob")));
            soft.assertThat(restriction.constraint()).isInstanceOf(In.class);
        });
    }

    @DisplayName("should create NotIn, using Set, constraint correctly")
    @Test
    void shouldCreateNotInAsSetRestriction() {
        var restriction = (BasicRestriction<Book, String>) _Book.author.in(Set.of("Alice", "Bob")).negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.author);
            soft.assertThat(restriction.constraint()).isEqualTo(NotIn.values(Set.of("Alice", "Bob")));
            soft.assertThat(restriction.constraint()).isInstanceOf(NotIn.class);
        });
    }

    @DisplayName("should create Like constraint correctly")
    @Test
    void shouldCreateLikeRestriction() {
        var restriction = (BasicRestriction<Book, String>) _Book.title.like("%Java%");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            soft.assertThat(restriction.constraint()).isEqualTo(Like.pattern("%Java%"));
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
        });

    }

    @DisplayName("should create NotLike constraint correctly")
    @Test
    void shouldCreateNotLikeRestriction() {
        var restriction = (BasicRestriction<Book, String>) _Book.title.like("%Java%").negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            soft.assertThat(restriction.constraint()).isEqualTo(NotLike.pattern("%Java%"));
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
        });
    }

    @DisplayName("should create NotBetween constraint correctly")
    @Test
    void shouldCreateNotBetweenRestriction() {
        var restriction = (BasicRestriction<Book, Integer>) _Book.numChapters.between(5, 15).negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.numChapters);
            soft.assertThat(restriction.constraint()).isEqualTo(NotBetween.bounds(5, 15));
            soft.assertThat(restriction.constraint()).isInstanceOf(NotBetween.class);
        });
    }

}
