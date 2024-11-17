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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PatternTest {

    @Test
    void shouldCreateExactMatchPattern() {
        Pattern pattern = Pattern.is("Java Guide");
        assertThat(pattern.value()).isEqualTo("Java Guide");
        assertThat(pattern.caseSensitive()).isTrue();
    }

    @Test
    void shouldCreateCustomMatchPattern() {
        Pattern pattern = Pattern.matches("Ja%_a");
        assertThat(pattern.value()).isEqualTo("Ja%_a");
        assertThat(pattern.caseSensitive()).isTrue();
    }

    @Test
    void shouldCreateCustomWildcardPattern() {
        Pattern pattern = Pattern.matches("Ja?a*", '?', '*');
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(pattern.value()).isEqualTo("Ja_a%");
            soft.assertThat(pattern.caseSensitive()).isTrue();
        });
    }

    @Test
    void shouldCreatePrefixMatchPattern() {
        Pattern pattern = Pattern.startsWith("Hibernate");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(pattern.value()).isEqualTo("Hibernate%");
            soft.assertThat(pattern.caseSensitive()).isTrue();
        });
    }

    @Test
    void shouldCreateSuffixMatchPattern() {
        Pattern pattern = Pattern.endsWith("Guide");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(pattern.value()).isEqualTo("%Guide");
            soft.assertThat(pattern.caseSensitive()).isTrue();
        });
    }

    @Test
    void shouldCreateSubstringMatchPattern() {
        Pattern pattern = Pattern.contains("Java");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(pattern.value()).isEqualTo("%Java%");
            soft.assertThat(pattern.caseSensitive()).isTrue();
        });
    }

    @ParameterizedTest
    @CsvSource({
            "Jav_%,Jav\\_\\%",
            "Hello%World,Hello\\%World",
            "Special_Chars,Special\\_Chars"
    })
    void shouldEscapeSpecialCharacters(String input, String expected) {
        Pattern pattern = Pattern.is(input);
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(pattern.value()).isEqualTo(expected);
            soft.assertThat(pattern.caseSensitive()).isTrue();
        });
    }
}
