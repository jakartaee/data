/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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
package jakarta.data.page;

import jakarta.data.Sort;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class KeysetPageableTest {

    @Test
    @DisplayName("Should include keyset values in next Pageable")
    void shouldCreatePageableAfterKeyset() {
        Pageable pageable = Pageable.ofSize(20).afterKeyset("First", 2L, 3);

        assertSoftly(softly -> {
            softly.assertThat(pageable.size()).isEqualTo(20);
            softly.assertThat(pageable.page()).isEqualTo(1L);
            softly.assertThat(pageable.mode()).isEqualTo(Pageable.Mode.CURSOR_NEXT);
            softly.assertThat(pageable.cursor().size()).isEqualTo(3);
            softly.assertThat(pageable.cursor().getKeysetElement(0)).isEqualTo("First");
            softly.assertThat(pageable.cursor().getKeysetElement(1)).isEqualTo(2L);
            softly.assertThat(pageable.cursor().getKeysetElement(2)).isEqualTo(3);
        });
    }

    @Test
    @DisplayName("Should include keyset values in next Pageable from Cursor")
    void shouldCreatePageableAfterKeysetCursor() {
        Pageable.Cursor cursor = new KeysetCursor("me", 200);
        Pageable pageable = Pageable.ofSize(35).sortBy(Sort.asc("name"), Sort.asc("id")).afterKeysetCursor(cursor);

        assertSoftly(softly -> {
            softly.assertThat(pageable.size()).isEqualTo(35);
            softly.assertThat(pageable.page()).isEqualTo(1L);
            softly.assertThat(pageable.sorts()).isEqualTo(List.of(Sort.asc("name"), Sort.asc("id")));
            softly.assertThat(pageable.mode()).isEqualTo(Pageable.Mode.CURSOR_NEXT);
            softly.assertThat(pageable.cursor().size()).isEqualTo(2);
            softly.assertThat(pageable.cursor().getKeysetElement(0)).isEqualTo("me");
            softly.assertThat(pageable.cursor().getKeysetElement(1)).isEqualTo(200);
        });
    }

    @Test
    @DisplayName("Should include keyset values in previous Pageable")
    void shouldCreatePageableBeforeKeyset() {
        Pageable pageable = Pageable.ofSize(30).sortBy(Sort.desc("yearBorn"), Sort.asc("ssn")).beforeKeyset(1991, "123-45-6789").page(10);

        assertSoftly(softly -> {
            softly.assertThat(pageable.size()).isEqualTo(30);
            softly.assertThat(pageable.page()).isEqualTo(10L);
            softly.assertThat(pageable.sorts()).isEqualTo(List.of(Sort.desc("yearBorn"), Sort.asc("ssn")));
            softly.assertThat(pageable.mode()).isEqualTo(Pageable.Mode.CURSOR_PREVIOUS);
            softly.assertThat(pageable.cursor().size()).isEqualTo(2);
            softly.assertThat(pageable.cursor().getKeysetElement(0)).isEqualTo(1991);
            softly.assertThat(pageable.cursor().getKeysetElement(1)).isEqualTo("123-45-6789");
        });
    }

    @Test
    @DisplayName("Should include keyset values in previous Pageable from Cursor")
    void shouldCreatePageableBeforeKeysetCursor() {
        Pageable.Cursor cursor = new KeysetCursor(900L, 300, "testing", 120, 'T');
        Pageable pageable = Pageable.ofPage(8).beforeKeysetCursor(cursor);

        assertSoftly(softly -> {
            softly.assertThat(pageable.size()).isEqualTo(10);
            softly.assertThat(pageable.page()).isEqualTo(8L);
            softly.assertThat(pageable.sorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(pageable.mode()).isEqualTo(Pageable.Mode.CURSOR_PREVIOUS);
            softly.assertThat(pageable.cursor().size()).isEqualTo(5);
            softly.assertThat(pageable.cursor().getKeysetElement(0)).isEqualTo(900L);
            softly.assertThat(pageable.cursor().getKeysetElement(1)).isEqualTo(300);
            softly.assertThat(pageable.cursor().getKeysetElement(2)).isEqualTo("testing");
            softly.assertThat(pageable.cursor().getKeysetElement(3)).isEqualTo(120);
            softly.assertThat(pageable.cursor().getKeysetElement(4)).isEqualTo('T');
        });
    }

    @Test
    @DisplayName("Should be usable in a hashing structure")
    void shouldHash() {
        Pageable pageable1 = Pageable.ofSize(15).afterKeyset(1, '1', "1")
                                           .sortBy(Sort.desc("yearHired"), Sort.asc("lastName"), Sort.asc("id"));
        Pageable pageable2a = Pageable.ofSize(15).afterKeyset(2, '2', "2");
        Pageable pageable2b = Pageable.ofSize(15).beforeKeyset(2, '2', "2");
        Pageable pageable2c = Pageable.ofSize(15).beforeKeyset(2, '2', "2");
        Map<Pageable, Integer> map = new HashMap<>();

        assertSoftly(softly -> {
            softly.assertThat(pageable2b.hashCode()).isEqualTo(pageable2c.hashCode());
            softly.assertThat(map.put(pageable1, 1)).isNull();
            softly.assertThat(map.put(pageable1, 10)).isEqualTo(1);
            softly.assertThat(map.put(pageable2a, 21)).isNull();
            softly.assertThat(map.put(pageable2b, 22)).isNull();
            softly.assertThat(map.get(pageable1)).isEqualTo(10);
            softly.assertThat(map.get(pageable2a)).isEqualTo(21);
            softly.assertThat(map.get(pageable2b)).isEqualTo(22);
            softly.assertThat(map.put(pageable2c, 23)).isEqualTo(22);
            softly.assertThat(map.get(pageable2b)).isEqualTo(23);
        });
    }

    @Test
    @DisplayName("Should be displayable as String with toString")
    void shouldPageableDisplayAsString() {
        Pageable pageable = Pageable.ofSize(200).afterKeyset("value1", 1);

        assertSoftly(softly -> softly.assertThat(pageable.toString())
              .isEqualTo("Pageable{page=1, size=200, mode=CURSOR_NEXT, 2 keys}"));

        Pageable pageableWithSorts = Pageable.ofSize(100).sortBy(Sort.desc("name"), Sort.asc("id"))
                                             .beforeKeyset("Item1", 3456);

        assertSoftly(softly -> softly.assertThat(pageableWithSorts.toString())
              .isEqualTo("Pageable{page=1, size=100, mode=CURSOR_PREVIOUS, 2 keys, name DESC, id ASC}"));
    }

    @Test
    @DisplayName("Should return true from equals if keyset values and other properties are equal")
    void shouldBeEqualWithSameKeysetValues() {
        Pageable pageable25p1s0a1 = Pageable.ofSize(25).afterKeyset("keyval1", '2', 3);
        Pageable pageable25p1s0b1 = Pageable.ofSize(25).beforeKeyset("keyval1", '2', 3);
        Pageable pageable25p1s0a1match = Pageable.ofSize(25).afterKeysetCursor(new KeysetCursor("keyval1", '2', 3));
        Pageable pageable25p2s0a1 = Pageable.ofPage(2).size(25).afterKeysetCursor(new KeysetCursor("keyval1", '2', 3));
        Pageable pageable25p1s1a1 = Pageable.ofSize(25).sortBy(Sort.desc("d"), Sort.asc("a"), Sort.asc("id")).afterKeyset("keyval1", '2', 3);
        Pageable pageable25p1s2a1 = Pageable.ofSize(25).sortBy(Sort.desc("d"), Sort.asc("a"), Sort.desc("id")).afterKeyset("keyval1", '2', 3);
        Pageable pageable25p1s0a2 = Pageable.ofSize(25).afterKeyset("keyval2", '2', 3);

        Pageable.Cursor cursor1 = new KeysetCursor("keyval1", '2', 3);
        Pageable.Cursor cursor2 = new KeysetCursor("keyval2", '2', 3);
        Pageable.Cursor cursor3 = new KeysetCursor("keyval1", '2');
        Pageable.Cursor cursor4 = new Pagination.Cursor() {
            private final Object[] keyset = new Object[] { "keyval1", '2', 3 };

            @Override
            public Object getKeysetElement(int index) {
                return keyset[index];
            }

            @Override
            public int size() {
                return keyset.length;
            }
        };

        assertSoftly(softly -> {
            softly.assertThat(cursor1.equals(cursor1)).isTrue();
            softly.assertThat(cursor1.equals(null)).isFalse();
            softly.assertThat(cursor1.equals(cursor2)).isFalse(); // different keyset values
            softly.assertThat(cursor1.equals(cursor3)).isFalse(); // different number of keyset values
            softly.assertThat(cursor1.equals(cursor4)).isFalse(); // different classes
            softly.assertThat(cursor4.equals(cursor1)).isFalse(); // different classes

            softly.assertThat(pageable25p1s0a1.cursor()).isEqualTo(cursor1);
            softly.assertThat(pageable25p1s0a1match.cursor()).isEqualTo(cursor1);

            softly.assertThat(pageable25p1s0a1.equals(pageable25p1s0a1)).isTrue();
            softly.assertThat(pageable25p1s0a1.equals(null)).isFalse();
            softly.assertThat(pageable25p1s0a1.equals(pageable25p1s0b1)).isFalse(); // after vs before
            softly.assertThat(pageable25p1s0a1.equals(pageable25p1s0a1match)).isTrue();
            softly.assertThat(pageable25p1s0a1.equals(pageable25p2s0a1)).isFalse(); // different page numbers
            softly.assertThat(pageable25p1s0a1.equals(pageable25p1s1a1)).isFalse(); // with vs without sorting
            softly.assertThat(pageable25p1s2a1.equals(pageable25p1s1a1)).isFalse(); // different sorting
            softly.assertThat(pageable25p1s0a1.equals(pageable25p1s0a2)).isFalse(); // different keyset value
            softly.assertThat(pageable25p1s0a1.equals(Pageable.ofSize(25))).isFalse(); // Pageable with keyset vs Pageable
            softly.assertThat(Pageable.ofSize(25).equals(pageable25p1s0a1)).isFalse(); // Pageable vs Pageable with keyset
        });
    }

    @Test
    @DisplayName("Should raise IllegalArgumentException when keyset values are absent")
    void shouldRaiseErrorForMissingKeysetValues() {
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.ofSize(60).afterKeyset(null));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.ofSize(70).afterKeyset(new Object[0]));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.ofSize(80).beforeKeyset(null));
        assertThatIllegalArgumentException().isThrownBy(() -> Pageable.ofSize(90).beforeKeyset(new Object[0]));
    }

    @Test
    @DisplayName("Keyset should be replaced on new instance of Pageable")
    void shouldReplaceKeyset() {
        Pageable p1 = Pageable.ofSize(30).sortBy(Sort.asc("lastName"), Sort.asc("firstName"), Sort.asc("id"))
                                         .afterKeyset("last1", "fname1", 100).page(12);
        Pageable p2 = p1.beforeKeyset("lname2", "fname2", 200);

        assertSoftly(softly -> {
            softly.assertThat(p1.mode()).isEqualTo(Pageable.Mode.CURSOR_NEXT);
            softly.assertThat(p1.cursor().getKeysetElement(0)).isEqualTo("last1");
            softly.assertThat(p1.cursor().getKeysetElement(1)).isEqualTo("fname1");
            softly.assertThat(p1.cursor().getKeysetElement(2)).isEqualTo(100);

            softly.assertThat(p2.mode()).isEqualTo(Pageable.Mode.CURSOR_PREVIOUS);
            softly.assertThat(p2.cursor().getKeysetElement(0)).isEqualTo("lname2");
            softly.assertThat(p2.cursor().getKeysetElement(1)).isEqualTo("fname2");
            softly.assertThat(p2.cursor().getKeysetElement(2)).isEqualTo(200);

            softly.assertThat(p1.sorts()).isEqualTo(List.of(Sort.asc("lastName"), Sort.asc("firstName"), Sort.asc("id")));
            softly.assertThat(p2.sorts()).isEqualTo(List.of(Sort.asc("lastName"), Sort.asc("firstName"), Sort.asc("id")));
            softly.assertThat(p1.page()).isEqualTo(12L);
            softly.assertThat(p2.page()).isEqualTo(12L);
            softly.assertThat(p1.size()).isEqualTo(30);
            softly.assertThat(p2.size()).isEqualTo(30);
        });
    }
}

