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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class SortsTest {

    @Test
    public void shouldCreatePropertyDirector(){
        Sorts sorts = Sorts.of("name", Direction.ASC);
        Assertions.assertNotNull(sorts);
        MatcherAssert.assertThat(sorts.getOrderBy(), Matchers.contains(Sort.asc("name")));
    }

    @Test
    public void shouldCreateAsc(){
        Sorts sorts = Sorts.asc("name");
        Assertions.assertNotNull(sorts);
        MatcherAssert.assertThat(sorts.getOrderBy(), Matchers.contains(Sort.asc("name")));
    }

    @Test
    public void shouldCreateDesc(){
        Sorts sorts = Sorts.desc("name");
        Assertions.assertNotNull(sorts);
        MatcherAssert.assertThat(sorts.getOrderBy(), Matchers.contains(Sort.desc("name")));
    }

    @Test
    public void shouldCreateOfIterable(){
        Sorts sorts = Sorts.of(List.of(Sort.asc("name"), Sort.desc("age")));
        Assertions.assertNotNull(sorts);
        MatcherAssert.assertThat(sorts.getOrderBy(), Matchers.contains(Sort.asc("name"),
                Sort.desc("age")));
    }

    @Test
    public void shouldCreateOfVarArgs(){
        Sorts sorts = Sorts.of(Sort.asc("name"), Sort.desc("age"));
        Assertions.assertNotNull(sorts);
        MatcherAssert.assertThat(sorts.getOrderBy(), Matchers.contains(Sort.asc("name"),
                Sort.desc("age")));
    }

    @Test
    public void shouldReturnErrorWhenOrderIsNull() {
        Sorts sorts = Sorts.of();
        Assertions.assertThrows(NullPointerException.class, () -> sorts.sort((Sort) null));
    }

    @Test
    public void shouldAddOrder() {
        Sorts sorts = Sorts.of();
        Assertions.assertTrue(sorts.isEmpty());
        Sorts name = sorts.sort(Sort.asc("name"));
        Assertions.assertNotNull(name);
        Assertions.assertFalse(name.isEmpty());
        Assertions.assertNotEquals(sorts, name);
        assertThat(name.getOrderBy(), contains(Sort.asc("name")));
    }

    @Test
    public void shouldReturnErrorWhenPropertyIsNull() {
        Sorts sorts = Sorts.of();
        Assertions.assertThrows(NullPointerException.class, () -> sorts.sort((String) null));
    }

    @Test
    public void shouldAddProperty() {
        Sorts sorts = Sorts.of();
        Assertions.assertTrue(sorts.isEmpty());
        Sorts name = sorts.sort("name");
        Assertions.assertNotNull(name);
        Assertions.assertFalse(name.isEmpty());
        Assertions.assertNotEquals(sorts, name);
        assertThat(name.getOrderBy(), contains(Sort.asc("name")));
    }

    @Test
    public void shouldReturnErrorWhenPropertyDirectionIsNull() {
        Sorts sorts = Sorts.of();
        Assertions.assertThrows(NullPointerException.class, () -> sorts.sort(null, null));
        Assertions.assertThrows(NullPointerException.class, () -> sorts.sort("name", null));
        Assertions.assertThrows(NullPointerException.class, () -> sorts.sort(null, Direction.ASC));
    }

    @Test
    public void shouldAddPropertyDirection() {
        Sorts sorts = Sorts.of();
        Assertions.assertTrue(sorts.isEmpty());
        Sorts name = sorts.sort("name", Direction.ASC);
        Assertions.assertNotNull(name);
        Assertions.assertFalse(name.isEmpty());
        Assertions.assertNotEquals(sorts, name);
        assertThat(name.getOrderBy(), contains(Sort.asc("name")));
    }

    @Test
    public void shouldReturnErrorWhenSortIsNull(){
        Sorts sorts = Sorts.of();
        Assertions.assertThrows(NullPointerException.class, () -> sorts.add(null));
    }
    @Test
    public void shouldAddSort(){
        Sorts sort = Sorts.of().sort("name", Direction.ASC);
        Sorts sorts = sort.add(Sorts.of(Sort.desc("age")));
        Assertions.assertNotNull(sorts);
        Assertions.assertFalse(sorts.isEmpty());
        Assertions.assertNotEquals(sort, sorts);
        assertThat(sorts.getOrderBy(), contains(Sort.asc("name"), Sort.desc("age")));
    }

}