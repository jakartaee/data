/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package jakarta.data.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class KeysetPageableTest {

    @Test
    @DisplayName("Should include keyset values in next KeysetPageable")
    void shouldCreateKeysetPageableAfterKeyset() {
        KeysetPageable pageable = Pageable.size(20).afterKeyset("First", 2L, 3);

        assertSoftly(softly -> {
            softly.assertThat(pageable.getSize()).isEqualTo(20L);
            softly.assertThat(pageable.getPage()).isEqualTo(1L);
            softly.assertThat(pageable.getMode()).isEqualTo(KeysetPageable.Mode.NEXT);
            softly.assertThat(pageable.getCursor().size()).isEqualTo(3);
            softly.assertThat(pageable.getCursor().getKeysetElement(0)).isEqualTo("First");
            softly.assertThat(pageable.getCursor().getKeysetElement(1)).isEqualTo(2L);
            softly.assertThat(pageable.getCursor().getKeysetElement(2)).isEqualTo(3);
        });
    }

    @Test
    @DisplayName("Should include keyset values in previous KeysetPageable")
    void shouldCreateKeysetPageableBeforeKeyset() {
        KeysetPageable pageable = Pageable.of(10, 30).beforeKeyset(100, "123-45-6789");

        assertSoftly(softly -> {
            softly.assertThat(pageable.getSize()).isEqualTo(30L);
            softly.assertThat(pageable.getPage()).isEqualTo(10L);
            softly.assertThat(pageable.getMode()).isEqualTo(KeysetPageable.Mode.PREVIOUS);
            softly.assertThat(pageable.getCursor().size()).isEqualTo(2);
            softly.assertThat(pageable.getCursor().getKeysetElement(0)).isEqualTo(100);
            softly.assertThat(pageable.getCursor().getKeysetElement(1)).isEqualTo("123-45-6789");
        });
    }

    @Test
    @DisplayName("Should raise IllegalArgumentException when keyset values are absent")
    void shouldRaiseErrorForMissingKeysetValues() {
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.size(60).afterKeyset(null));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.size(70).afterKeyset(new Object[0]));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.size(80).beforeKeyset(null));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.size(90).beforeKeyset(new Object[0]));
    }

}

