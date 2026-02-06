/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
package jakarta.data.mock.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates mock Book entities for tests.
 */
public class BookSimulator {
    /**
     * Creates {@code amount} number of mock Book entities.
     *
     * @param amount amount of mock Book entities to create.
     * @return a list of mock Book entities.
     */
    public static List<Book> mock(int amount) {
        Instant copyright1 = ZonedDateTime
                .of(2025, 8, 4, 8, 30, 0, 0, ZoneId.of("America/Chicago"))
                .toInstant();
        Instant copyright2 = ZonedDateTime
                .of(2025, 7, 30, 17, 20, 0, 0, ZoneId.of("America/Chicago"))
                .toInstant();

        LocalDate published1 = LocalDate
                .of(2026, 1, 3);
        LocalDate published2 = LocalDate
                .of(2026, 2, 6);

        List<Book> books = new ArrayList<>(amount);
        if (amount > 0) {
            books.add(Book.of(
                    "100", "Discovering Jakarta Data", "Someone",
                    8, 160, copyright1, published2));
        }
        if (amount > 1) {
            books.add(Book.of(
                    "101", "Exploring Jakarta Persistence", "Someone Else",
                    19, 317, copyright1, published1));
        }
        if (amount > 2) {
            books.add(Book.of(
                    "102", "Knowing Jakarta NoSQL", "Another Author",
                    12, 202, copyright2, published2));
        }
        if (amount > 3) {
            books.add(Book.of(
                    "103", "Learning Jakarta Transactions", "Other Author",
                    13, 133, copyright2, published1));
        }
        if (amount > 4) {
            books.add(Book.of(
                    "104", "Studying Jakarta Serlvet", "Some Other Author",
                    14, 148, copyright2, published2));
        }
        if (amount > 5) {
            throw new IllegalArgumentException(
                    "Need to define more mock entities.");
        }

        return books;
    }
}
