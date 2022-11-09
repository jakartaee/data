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
    @DisplayName("Should include keyset values in next KeysetPageable")
    void shouldCreateKeysetPageableAfterKeyset() {
        KeysetPageable pageable = Pageable.ofSize(20).afterKeyset("First", 2L, 3);

        assertSoftly(softly -> {
            softly.assertThat(pageable.getSize()).isEqualTo(20);
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
        KeysetPageable.Cursor cursor = new KeysetPagination.CursorImpl("me", 200);
        KeysetPageable pageable = Pageable.ofSize(35).sortBy(Sort.asc("name"), Sort.asc("id")).afterKeysetCursor(cursor);

        assertSoftly(softly -> {
            softly.assertThat(pageable.getSize()).isEqualTo(35);
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
        KeysetPageable pageable = Pageable.ofSize(30).sortBy(Sort.desc("yearBorn"), Sort.asc("ssn")).beforeKeyset(1991, "123-45-6789").page(10);

        assertSoftly(softly -> {
            softly.assertThat(pageable.getSize()).isEqualTo(30);
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
        KeysetPageable.Cursor cursor = new KeysetPagination.CursorImpl(900L, 300, "testing", 120, 'T');
        KeysetPageable pageable = Pageable.ofPage(8).beforeKeysetCursor(cursor);

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
        KeysetPageable pageable1 = Pageable.ofSize(15).afterKeyset(1, '1', "1")
                                           .sortBy(Sort.desc("yearHired"), Sort.asc("lastName"), Sort.asc("id"));
        KeysetPageable pageable2a = Pageable.ofSize(15).afterKeyset(2, '2', "2");
        KeysetPageable pageable2b = Pageable.ofSize(15).beforeKeyset(2, '2', "2");
        KeysetPageable pageable2c = Pageable.ofSize(15).beforeKeyset(2, '2', "2");
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
        KeysetPageable pageable = Pageable.ofSize(200).afterKeyset("value1", 1);

        assertSoftly(softly -> softly.assertThat(pageable.toString())
              .isEqualTo("KeysetPageable{page=1, size=200, mode=NEXT, 2 keys}"));

        KeysetPageable pageableWithSorts = Pageable.ofSize(100).sortBy(Sort.desc("name"), Sort.asc("id"))
                                                   .beforeKeyset("Item1", 3456);

        assertSoftly(softly -> softly.assertThat(pageableWithSorts.toString())
              .isEqualTo("KeysetPageable{page=1, size=100, mode=PREVIOUS, 2 keys, name DESC, id ASC}"));
    }

    @Test
    @DisplayName("Should return true from equals if keyset values and other properties are equal")
    void shouldBeEqualWithSameKeysetValues() {
        KeysetPageable pageable25p1s0a1 = Pageable.ofSize(25).afterKeyset("keyval1", '2', 3);
        KeysetPageable pageable25p1s0b1 = Pageable.ofSize(25).beforeKeyset("keyval1", '2', 3);
        KeysetPageable pageable25p1s0a1match = Pageable.ofSize(25).afterKeysetCursor(new KeysetPagination.CursorImpl("keyval1", '2', 3));
        KeysetPageable pageable25p2s0a1 = Pageable.ofPage(2).size(25).afterKeysetCursor(new KeysetPagination.CursorImpl("keyval1", '2', 3));
        KeysetPageable pageable25p1s1a1 = Pageable.ofSize(25).sortBy(Sort.desc("d"), Sort.asc("a"), Sort.asc("id")).afterKeyset("keyval1", '2', 3);
        KeysetPageable pageable25p1s2a1 = Pageable.ofSize(25).sortBy(Sort.desc("d"), Sort.asc("a"), Sort.desc("id")).afterKeyset("keyval1", '2', 3);
        KeysetPageable pageable25p1s0a2 = Pageable.ofSize(25).afterKeyset("keyval2", '2', 3);

        KeysetPageable.Cursor cursor1 = new KeysetPagination.CursorImpl("keyval1", '2', 3);
        KeysetPageable.Cursor cursor2 = new KeysetPagination.CursorImpl("keyval2", '2', 3);
        KeysetPageable.Cursor cursor3 = new KeysetPagination.CursorImpl("keyval1", '2');
        KeysetPageable.Cursor cursor4 = new KeysetPagination.Cursor() {
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
            softly.assertThat(pageable25p1s0a1.equals(Pageable.ofSize(25))).isFalse(); // KeysetPageable vs Pageable
            softly.assertThat(Pageable.ofSize(25).equals(pageable25p1s0a1)).isFalse(); // Pageable vs KeysetPageable
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
    @DisplayName("Keyset should be replaced on new instance of KeysetPageable")
    public void shouldReplaceKeyset() {
        KeysetPageable p1 = Pageable.ofSize(30).sortBy(Sort.asc("lastName"), Sort.asc("firstName"), Sort.asc("id"))
                                    .afterKeyset("last1", "fname1", 100).page(12);
        KeysetPageable p2 = p1.beforeKeyset("lname2", "fname2", 200);

        assertSoftly(softly -> {
            softly.assertThat(p1.getMode()).isEqualTo(KeysetPageable.Mode.NEXT);
            softly.assertThat(p1.getCursor().getKeysetElement(0)).isEqualTo("last1");
            softly.assertThat(p1.getCursor().getKeysetElement(1)).isEqualTo("fname1");
            softly.assertThat(p1.getCursor().getKeysetElement(2)).isEqualTo(100);

            softly.assertThat(p2.getMode()).isEqualTo(KeysetPageable.Mode.PREVIOUS);
            softly.assertThat(p2.getCursor().getKeysetElement(0)).isEqualTo("lname2");
            softly.assertThat(p2.getCursor().getKeysetElement(1)).isEqualTo("fname2");
            softly.assertThat(p2.getCursor().getKeysetElement(2)).isEqualTo(200);

            softly.assertThat(p1.getSorts()).isEqualTo(List.of(Sort.asc("lastName"), Sort.asc("firstName"), Sort.asc("id")));
            softly.assertThat(p2.getSorts()).isEqualTo(List.of(Sort.asc("lastName"), Sort.asc("firstName"), Sort.asc("id")));
            softly.assertThat(p1.getPage()).isEqualTo(12L);
            softly.assertThat(p2.getPage()).isEqualTo(12L);
            softly.assertThat(p1.getSize()).isEqualTo(30);
            softly.assertThat(p2.getSize()).isEqualTo(30);
        });
    }
}

