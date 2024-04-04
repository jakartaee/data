/*
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class KeysetPageRequestTest {

    @Test
    @DisplayName("Should include key values in next PageRequest")
    void shouldCreatePageRequestAfterKeyset() {
        PageRequest<?> pageRequest = PageRequest.ofSize(20).afterKey("First", 2L, 3);

        assertSoftly(softly -> {
            softly.assertThat(pageRequest.size()).isEqualTo(20);
            softly.assertThat(pageRequest.page()).isEqualTo(1L);
            softly.assertThat(pageRequest.mode()).isEqualTo(PageRequest.Mode.CURSOR_NEXT);
            softly.assertThat(pageRequest.cursor()).get().extracting(PageRequest.Cursor::size).isEqualTo(3);
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(0)).isEqualTo("First");
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(1)).isEqualTo(2L);
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(2)).isEqualTo(3);
        });
    }

    @Test
    @DisplayName("Should include key values in next PageRequest from Cursor")
    void shouldCreatePageRequestAfterKeysetCursor() {
        PageRequest.Cursor cursor = new PageRequestCursor("me", 200);
        PageRequest<?> pageRequest = PageRequest.ofSize(35).afterCursor(cursor);

        assertSoftly(softly -> {
            softly.assertThat(pageRequest.size()).isEqualTo(35);
            softly.assertThat(pageRequest.page()).isEqualTo(1L);
            softly.assertThat(pageRequest.mode()).isEqualTo(PageRequest.Mode.CURSOR_NEXT);
            softly.assertThat(pageRequest.cursor()).get().extracting(PageRequest.Cursor::size).isEqualTo(2);
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(0)).isEqualTo("me");
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(1)).isEqualTo(200);
        });
    }

    @Test
    @DisplayName("Should include key values in previous PageRequest")
    void shouldCreatePageRequestBeforeKeyset() {
        PageRequest<?> pageRequest = PageRequest.ofSize(30).beforeKey(1991, "123-45-6789").page(10);

        assertSoftly(softly -> {
            softly.assertThat(pageRequest.size()).isEqualTo(30);
            softly.assertThat(pageRequest.page()).isEqualTo(10L);
            softly.assertThat(pageRequest.mode()).isEqualTo(PageRequest.Mode.CURSOR_PREVIOUS);
            softly.assertThat(pageRequest.cursor()).get().extracting(PageRequest.Cursor::size).isEqualTo(2);
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(0)).isEqualTo(1991);
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(1)).isEqualTo("123-45-6789");
        });
    }

    @Test
    @DisplayName("Should include key values in previous PageRequest from Cursor")
    void shouldCreatePageRequestBeforeKeysetCursor() {
        PageRequest.Cursor cursor = new PageRequestCursor(900L, 300, "testing", 120, 'T');
        PageRequest<?> pageRequest = PageRequest.ofPage(8).beforeCursor(cursor);

        assertSoftly(softly -> {
            softly.assertThat(pageRequest.size()).isEqualTo(10);
            softly.assertThat(pageRequest.page()).isEqualTo(8L);
            softly.assertThat(pageRequest.mode()).isEqualTo(PageRequest.Mode.CURSOR_PREVIOUS);
            softly.assertThat(pageRequest.cursor()).get().extracting(PageRequest.Cursor::size).isEqualTo(5);
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(0)).isEqualTo(900L);
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(1)).isEqualTo(300);
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(2)).isEqualTo("testing");
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(3)).isEqualTo(120);
            softly.assertThat(pageRequest.cursor()).get().extracting(c -> c.get(4)).isEqualTo('T');
        });
    }

    @Test
    @DisplayName("Should be usable in a hashing structure")
    void shouldHash() {
        PageRequest<?> pageRequest1 = PageRequest.ofSize(15).afterKey(1, '1', "1");
        PageRequest<?> pageRequest2A = PageRequest.ofSize(15).afterKey(2, '2', "2");
        PageRequest<?> pageRequest2B = PageRequest.ofSize(15).beforeKey(2, '2', "2");
        PageRequest<?> pageRequest2C = PageRequest.ofSize(15).beforeKey(2, '2', "2");
        Map<PageRequest<?>, Integer> map = new HashMap<>();

        assertSoftly(softly -> {
            softly.assertThat(pageRequest2B.hashCode()).isEqualTo(pageRequest2C.hashCode());
            softly.assertThat(map.put(pageRequest1, 1)).isNull();
            softly.assertThat(map.put(pageRequest1, 10)).isEqualTo(1);
            softly.assertThat(map.put(pageRequest2A, 21)).isNull();
            softly.assertThat(map.put(pageRequest2B, 22)).isNull();
            softly.assertThat(map.get(pageRequest1)).isEqualTo(10);
            softly.assertThat(map.get(pageRequest2A)).isEqualTo(21);
            softly.assertThat(map.get(pageRequest2B)).isEqualTo(22);
            softly.assertThat(map.put(pageRequest2C, 23)).isEqualTo(22);
            softly.assertThat(map.get(pageRequest2B)).isEqualTo(23);
        });
    }

    @Test
    @DisplayName("Should be displayable as String with toString")
    void shouldPageRequestDisplayAsString() {
        var afterKeySet = PageRequest.ofSize(200).afterKey("value1", 1);
        var beforeKeySet = PageRequest.ofSize(100).beforeKey("Item1", 3456);

        assertSoftly(softly -> {

            softly.assertThat(afterKeySet.toString())
              .isEqualTo("PageRequest{page=1, size=200, mode=CURSOR_NEXT, 2 keys}");

            softly.assertThat(beforeKeySet.toString())
                    .isEqualTo("PageRequest{page=1, size=100, mode=CURSOR_PREVIOUS, 2 keys}");

        });
    }

    @Test
    @DisplayName("Should return true from equals if key values and other properties are equal")
    void shouldBeEqualWithSameKeysetValues() {
        PageRequest<?> pageRequest25P1S0A1 = PageRequest.ofSize(25).afterKey("keyval1", '2', 3);
        PageRequest<?> pageRequest25P1S0B1 = PageRequest.ofSize(25).beforeKey("keyval1", '2', 3);
        PageRequest<?> pageRequest25P1S0A1Match = PageRequest.ofSize(25).afterCursor(new PageRequestCursor("keyval1", '2', 3));
        PageRequest<?> pageRequest25P2S0A1 = PageRequest.ofPage(2).size(25).afterCursor(new PageRequestCursor("keyval1", '2', 3));
        PageRequest<?> pageRequest25P1S1A1 = PageRequest.ofSize(25).afterKey("keyval1", '2', 3);
        PageRequest<?> pageRequest25P1S2A1 = PageRequest.ofSize(25).afterKey("keyval1", '2', 3);
        PageRequest<?> pageRequest25P1S0A2 = PageRequest.ofSize(25).afterKey("keyval2", '2', 3);

        PageRequest.Cursor cursor1 = new PageRequestCursor("keyval1", '2', 3);
        PageRequest.Cursor cursor2 = new PageRequestCursor("keyval2", '2', 3);
        PageRequest.Cursor cursor3 = new PageRequestCursor("keyval1", '2');
        PageRequest.Cursor cursor4 = new PageRequest.Cursor() {
            private final Object[] keyset = new Object[] { "keyval1", '2', 3 };

            @Override
            public Object get(int index) {
                return keyset[index];
            }

            @Override
            public int size() {
                return keyset.length;
            }

            @Override
            public List<?> elements() {
                return List.of(keyset);
            }
        };

        assertSoftly(softly -> {
            softly.assertThat(cursor1.equals(cursor1)).isTrue();
            softly.assertThat(cursor1.equals(null)).isFalse();
            softly.assertThat(cursor1.equals(cursor2)).isFalse(); // different keyset values
            softly.assertThat(cursor1.equals(cursor3)).isFalse(); // different number of keyset values
            softly.assertThat(cursor1.equals(cursor4)).isFalse(); // different classes
            softly.assertThat(cursor4.equals(cursor1)).isFalse(); // different classes

            softly.assertThat(pageRequest25P1S0A1.cursor()).get().isEqualTo(cursor1);
            softly.assertThat(pageRequest25P1S0A1Match.cursor()).get().isEqualTo(cursor1);

            softly.assertThat(pageRequest25P1S0A1.equals(pageRequest25P1S0A1)).isTrue();
            softly.assertThat(pageRequest25P1S0A1.equals(null)).isFalse();
            softly.assertThat(pageRequest25P1S0A1.equals(pageRequest25P1S0B1)).isFalse(); // after vs before
            softly.assertThat(pageRequest25P1S0A1.equals(pageRequest25P1S0A1Match)).isTrue();
            softly.assertThat(pageRequest25P1S0A1.equals(pageRequest25P2S0A1)).isFalse(); // different page numbers
            softly.assertThat(pageRequest25P1S0A1.equals(pageRequest25P1S0A2)).isFalse(); // different keyset value
            softly.assertThat(pageRequest25P1S0A1.equals(PageRequest.ofSize(25))).isFalse(); // PageRequest with keyset vs PageRequest
            softly.assertThat(PageRequest.ofSize(25).equals(pageRequest25P1S0A1)).isFalse(); // PageRequest vs PageRequest with keyset
        });
    }

    @Test
    @DisplayName("Should raise IllegalArgumentException when key values are absent")
    void shouldRaiseErrorForMissingKeysetValues() {
        assertThatIllegalArgumentException().isThrownBy(() -> PageRequest.ofSize(60).afterKey((Object[]) null));
        assertThatIllegalArgumentException().isThrownBy(() -> PageRequest.ofSize(70).afterKey(new Object[0]));
        assertThatIllegalArgumentException().isThrownBy(() -> PageRequest.ofSize(80).beforeKey((Object[]) null));
        assertThatIllegalArgumentException().isThrownBy(() -> PageRequest.ofSize(90).beforeKey(new Object[0]));
    }

    @Test
    @DisplayName("Key should be replaced on new instance of PageRequest")
    void shouldReplaceKeyset() {
        PageRequest<?> p1 = PageRequest.ofSize(30).afterKey("last1", "fname1", 100).page(12);
        PageRequest<?> p2 = p1.beforeKey("lname2", "fname2", 200);

        assertSoftly(softly -> {
            softly.assertThat(p1.mode()).isEqualTo(PageRequest.Mode.CURSOR_NEXT);
            softly.assertThat(p1.cursor()).get().extracting(c -> c.get(0)).isEqualTo("last1");
            softly.assertThat(p1.cursor()).get().extracting(c -> c.get(1)).isEqualTo("fname1");
            softly.assertThat(p1.cursor()).get().extracting(c -> c.get(2)).isEqualTo(100);

            softly.assertThat(p2.mode()).isEqualTo(PageRequest.Mode.CURSOR_PREVIOUS);
            softly.assertThat(p2.cursor()).get().extracting(c -> c.get(0)).isEqualTo("lname2");
            softly.assertThat(p2.cursor()).get().extracting(c -> c.get(1)).isEqualTo("fname2");
            softly.assertThat(p2.cursor()).get().extracting(c -> c.get(2)).isEqualTo(200);

            softly.assertThat(p1.page()).isEqualTo(12L);
            softly.assertThat(p2.page()).isEqualTo(12L);
            softly.assertThat(p1.size()).isEqualTo(30);
            softly.assertThat(p2.size()).isEqualTo(30);
        });
    }
}

