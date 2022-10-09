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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LimitTest {

    @Test
    public void shouldRaiseErrorWhenEndAtIsLessThanStartAt() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Limit.range(10, 1));
    }

    @Test
    public void shouldReturnErrorWhenMaxResultsIsNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Limit.of(-1));
    }

    @Test
    public void shouldReturnErrorWhenMaxResultsIsZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Limit.of(0));
    }

    @Test
    public void shouldReturnErrorWhenStartAtIsNegative(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> Limit.range(-1, 10));
    }

    @Test
    public void shouldReturnErrorWhenStartAtIsZero(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> Limit.range(0, 100));
    }
    @Test
    public void shouldCreateLimitWithDefaultStartAt() {
        Limit limit = Limit.of(10);
        Assertions.assertNotNull(limit);
        Assertions.assertEquals(10L, limit.maxResults());
        Assertions.assertEquals(1L, limit.startAt());
    }

    @Test
    public void shouldCreateLimitWithRange() {
        Limit limit = Limit.range(2, 11);
        Assertions.assertNotNull(limit);
        Assertions.assertEquals(10L, limit.maxResults());
        Assertions.assertEquals(2L, limit.startAt());
    }
}