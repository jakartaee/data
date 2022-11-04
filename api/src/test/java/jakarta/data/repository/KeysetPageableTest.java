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
    @DisplayName("Should create a cursor for the specified values")
    void shouldCreateCursor() {
        KeysetPageable.Cursor cursor1 = new KeysetPageable.Cursor(1);
        KeysetPageable.Cursor cursor2 = new KeysetPageable.Cursor(2, "two");

        assertSoftly(softly -> {
            softly.assertThat(cursor1.size()).isEqualTo(1);
            softly.assertThat(cursor1.getKeysetElement(0)).isEqualTo(1);
            softly.assertThat(cursor2.size()).isEqualTo(2);
            softly.assertThat(cursor2.getKeysetElement(0)).isEqualTo(2);
            softly.assertThat(cursor2.getKeysetElement(1)).isEqualTo("two");
        });
    }

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
    @DisplayName("Should include keyset values in next KeysetPageable from Cursor")
    void shouldCreateKeysetPageableAfterKeysetCursor() {
        KeysetPageable.Cursor cursor = new KeysetPageable.Cursor("me", 200);
        KeysetPageable pageable = Pageable.of(1, 35, Sort.asc("name"), Sort.asc("id")).afterKeysetCursor(cursor);

        assertSoftly(softly -> {
            softly.assertThat(pageable.getSize()).isEqualTo(35L);
            softly.assertThat(pageable.getPage()).isEqualTo(1L);
            softly.assertThat(pageable.getSorts()).isEqualTo(List.of(Sort.asc("name"), Sort.asc("id")));
            softly.assertThat(pageable.getMode()).isEqualTo(KeysetPageable.Mode.NEXT);
            softly.assertThat(pageable.getCursor().size()).isEqualTo(2);
            softly.assertThat(pageable.getCursor().getKeysetElement(0)).isEqualTo("me");
            softly.assertThat(pageable.getCursor().getKeysetElement(1)).isEqualTo(200);
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
    @DisplayName("Should include keyset values in previous KeysetPageable from Cursor")
    void shouldCreateKeysetPageableBeforeKeysetCursor() {
        KeysetPageable.Cursor cursor = new KeysetPageable.Cursor(900L, 300, "testing", 120, 'T');
        KeysetPageable pageable = Pageable.page(8).beforeKeysetCursor(cursor);

        assertSoftly(softly -> {
            softly.assertThat(pageable.getSize()).isEqualTo(10);
            softly.assertThat(pageable.getPage()).isEqualTo(8L);
            softly.assertThat(pageable.getSorts()).isEqualTo(Collections.EMPTY_LIST);
            softly.assertThat(pageable.getMode()).isEqualTo(KeysetPageable.Mode.PREVIOUS);
            softly.assertThat(pageable.getCursor().size()).isEqualTo(5);
            softly.assertThat(pageable.getCursor().getKeysetElement(0)).isEqualTo(900L);
            softly.assertThat(pageable.getCursor().getKeysetElement(1)).isEqualTo(300);
            softly.assertThat(pageable.getCursor().getKeysetElement(2)).isEqualTo("testing");
            softly.assertThat(pageable.getCursor().getKeysetElement(3)).isEqualTo(120);
            softly.assertThat(pageable.getCursor().getKeysetElement(4)).isEqualTo('T');
        });
    }

    @Test
    @DisplayName("Should be usable in a hashing structure")
    void shouldHash() {
        KeysetPageable pageable1 = Pageable.of(1, 15, Sort.desc("yearHired"), Sort.asc("lastName"), Sort.asc("id"))
                                           .afterKeyset(1, '1', "1");
        KeysetPageable pageable2a = Pageable.size(15).afterKeyset(2, '2', "2");
        KeysetPageable pageable2b = Pageable.size(15).beforeKeyset(2, '2', "2");
        KeysetPageable pageable2c = Pageable.size(15).beforeKeyset(2, '2', "2");
        Map<KeysetPageable, Integer> map = new HashMap<>();

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
    @DisplayName("Should return true from equals if keyset values and other properties are equal")
    void shouldBeEqualWithSameKeysetValues() {
        KeysetPageable pageable25p1s0a1 = Pageable.size(25).afterKeyset("keyval1", '2', 3);
        KeysetPageable pageable25p1s0b1 = Pageable.size(25).beforeKeyset("keyval1", '2', 3);
        KeysetPageable pageable25p1s0a1match = Pageable.of(1, 25).afterKeysetCursor(new KeysetPageable.Cursor("keyval1", '2', 3));
        KeysetPageable pageable25p2s0a1 = Pageable.of(2, 25).afterKeysetCursor(new KeysetPageable.Cursor("keyval1", '2', 3));
        KeysetPageable pageable25p1s1a1 = Pageable.of(1, 25, Sort.desc("d"), Sort.asc("a"), Sort.asc("id")).afterKeyset("keyval1", '2', 3);
        KeysetPageable pageable25p1s2a1 = Pageable.of(1, 25, Sort.desc("d"), Sort.asc("a"), Sort.desc("id")).afterKeyset("keyval1", '2', 3);
        KeysetPageable pageable25p1s0a2 = Pageable.size(25).afterKeyset("keyval2", '2', 3);

        KeysetPageable.Cursor cursor1 = new KeysetPageable.Cursor("keyval1", '2', 3);
        KeysetPageable.Cursor cursor2 = new KeysetPageable.Cursor("keyval2", '2', 3);
        KeysetPageable.Cursor cursor3 = new KeysetPageable.Cursor("keyval1", '2');
        KeysetPageable.Cursor cursor4 = new KeysetPageable.Cursor("keyval1", '2', 3) {
        };

        assertSoftly(softly -> {
            softly.assertThat(cursor1.equals(cursor1)).isTrue();
            softly.assertThat(cursor1.equals(null)).isFalse();
            softly.assertThat(cursor1.equals(cursor2)).isFalse(); // different keyset values
            softly.assertThat(cursor1.equals(cursor3)).isFalse(); // different number of keyset values
            softly.assertThat(cursor1.equals(cursor4)).isFalse(); // different classes
            softly.assertThat(cursor4.equals(cursor1)).isFalse(); // different classes

            softly.assertThat(pageable25p1s0a1.getCursor()).isEqualTo(cursor1);
            softly.assertThat(pageable25p1s0a1match.getCursor()).isEqualTo(cursor1);

            softly.assertThat(pageable25p1s0a1.equals(pageable25p1s0a1)).isTrue();
            softly.assertThat(pageable25p1s0a1.equals(null)).isFalse();
            softly.assertThat(pageable25p1s0a1.equals(pageable25p1s0b1)).isFalse(); // after vs before
            softly.assertThat(pageable25p1s0a1.equals(pageable25p1s0a1match)).isTrue();
            softly.assertThat(pageable25p1s0a1.equals(pageable25p2s0a1)).isFalse(); // different page numbers
            softly.assertThat(pageable25p1s0a1.equals(pageable25p1s1a1)).isFalse(); // with vs without sorting
            softly.assertThat(pageable25p1s2a1.equals(pageable25p1s1a1)).isFalse(); // different sorting
            softly.assertThat(pageable25p1s0a1.equals(pageable25p1s0a2)).isFalse(); // different keyset value
            softly.assertThat(pageable25p1s0a1.equals(Pageable.of(1, 25))).isFalse(); // KeysetPageable vs Pageable
            softly.assertThat(Pageable.of(1, 25).equals(pageable25p1s0a1)).isFalse(); // Pageable vs KeysetPageable
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

