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

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
        KeysetPageable pageable = Pageable.of(10, 30, Sort.desc("yearBorn"), Sort.asc("ssn")).beforeKeyset(1991, "123-45-6789");

        assertSoftly(softly -> {
            softly.assertThat(pageable.getSize()).isEqualTo(30L);
            softly.assertThat(pageable.getPage()).isEqualTo(10L);
            softly.assertThat(pageable.getSorts()).isEqualTo(List.of(Sort.desc("yearBorn"), Sort.asc("ssn")));
            softly.assertThat(pageable.getMode()).isEqualTo(KeysetPageable.Mode.PREVIOUS);
            softly.assertThat(pageable.getCursor().size()).isEqualTo(2);
            softly.assertThat(pageable.getCursor().getKeysetElement(0)).isEqualTo(1991);
            softly.assertThat(pageable.getCursor().getKeysetElement(1)).isEqualTo("123-45-6789");
        });
    }

    @Test
    @DisplayName("Should be usable in a hashing structure")
    void shouldHash() {
        KeysetPageable pageable1 = Pageable.of(1, 15, Sort.desc("yearHired"), Sort.asc("lastName"), Sort.asc("id"))
                                           .afterKeyset(1, '1', "1");
        KeysetPageable pageable2a = Pageable.size(15).afterKeyset(2, '2', "2");
        KeysetPageable pageable2b = Pageable.size(15).afterKeyset(2, '2', "2");
        Map<KeysetPageable, Integer> map = new HashMap<>();

        assertSoftly(softly -> {
            softly.assertThat(map.put(pageable1, 1)).isNull();
            softly.assertThat(map.put(pageable1, 10)).isEqualTo(1);
            softly.assertThat(map.put(pageable2a, 21)).isNull();
            softly.assertThat(map.put(pageable2b, 22)).isNull();
            softly.assertThat(map.get(pageable1)).isEqualTo(10);
            softly.assertThat(map.get(pageable2a)).isEqualTo(21);
            softly.assertThat(map.get(pageable2b)).isEqualTo(22);
        });
    }

    @Test
    @DisplayName("Should be displayable as String with toString")
    void shouldKeysetPageableDisplayAsString() {
        KeysetPageable pageable = Pageable.size(200).afterKeyset("value1", 1);

        assertSoftly(softly -> {
            softly.assertThat(pageable.toString())
                  .isEqualTo("KeysetPageable{page=1, size=200, mode=NEXT, 2 keys}");
        });

        KeysetPageable pageableWithSorts = Pageable.of(1, 100, Sort.desc("name"), Sort.asc("id"))
                                                   .beforeKeyset("Item1", 3456);

        assertSoftly(softly -> {
            softly.assertThat(pageableWithSorts.toString())
                  .isEqualTo("KeysetPageable{page=1, size=100, mode=PREVIOUS, 2 keys, name DESC, id ASC}");
        });
    }

    @Test
    @DisplayName("Should return false from equals unless comparing with self")
    void shouldNotBeEqualUnlessSameInstance() {
        KeysetPageable pageable = Pageable.size(25).afterKeyset("keyval1", '2', 3);

        assertSoftly(softly -> {
            softly.assertThat(pageable.equals(pageable)).isTrue();
            softly.assertThat(pageable.equals(null)).isFalse();
            softly.assertThat(pageable.equals(Pageable.size(25).afterKeyset("keyval1", '2', 3))).isFalse(); // different cursor instances
            softly.assertThat(pageable.equals(Pageable.size(25).beforeKeyset("keyval1", '2', 3))).isFalse();
            softly.assertThat(pageable.equals(Pageable.of(1, 25))).isFalse();
            softly.assertThat(Pageable.of(1, 25).equals(pageable)).isFalse();
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

