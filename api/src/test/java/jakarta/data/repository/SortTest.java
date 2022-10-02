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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SortTest {

    @Test
    public void shouldReturnErrorWhenPropertyDirectionNull() {
        Assertions.assertThrows(NullPointerException.class, () ->
                Sort.of(null, null));

        Assertions.assertThrows(NullPointerException.class, () ->
                Sort.of("name", null));

        Assertions.assertThrows(NullPointerException.class, () ->
                Sort.of(null, Direction.ASC));
    }

    @Test
    public void shouldCreateSort() {
        Sort order = Sort.of("name", Direction.ASC);
        Assertions.assertNotNull(order);
        Assertions.assertEquals("name", order.getProperty());
        Assertions.assertTrue(order.isAscending());
        Assertions.assertFalse(order.isDescending());
    }

    @Test
    public void shouldCreateAsc(){
        Sort order = Sort.asc("name");
        Assertions.assertNotNull(order);
        Assertions.assertEquals("name", order.getProperty());
        Assertions.assertTrue(order.isAscending());
        Assertions.assertFalse(order.isDescending());
    }

    @Test
    public void shouldCreateDesc(){
        Sort order = Sort.desc("name");
        Assertions.assertNotNull(order);
        Assertions.assertEquals("name", order.getProperty());
        Assertions.assertTrue(order.isDescending());
        Assertions.assertFalse(order.isAscending());
    }

    @Test
    public void shouldCreateOrder() {
        Sort order = Sort.of("name",Direction.ASC);
        Assertions.assertNotNull(order);
        Assertions.assertEquals("name", order.getProperty());
    }

    @Test
    public void shouldAscending(){
        Sort order = Sort.of("name",Direction.ASC);
        Assertions.assertEquals("name", order.getProperty());
        Assertions.assertTrue(order.isAscending());
        Assertions.assertFalse(order.isDescending());
    }

    @Test
    public void shouldDescending(){
        Sort order = Sort.of("name",Direction.DESC);
        Assertions.assertEquals("name", order.getProperty());
        Assertions.assertFalse(order.isAscending());
        Assertions.assertTrue(order.isDescending());
    }
}