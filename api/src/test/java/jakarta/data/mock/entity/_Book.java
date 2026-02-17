/*
 * Copyright (c) 2024,2026 Contributors to the Eclipse Foundation
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

import jakarta.data.metamodel.BasicAttribute;
import jakarta.data.metamodel.BooleanAttribute;
import jakarta.data.metamodel.NumericAttribute;
import jakarta.data.metamodel.TemporalAttribute;
import jakarta.data.metamodel.TextAttribute;

/**
 * A mock static metamodel class for tests
 */
public interface _Book {
    String AUTHOR = "author";
    String COPYRIGHTDATE = "copyrightDate";
    String FICTION = "fiction";
    String ID = "id";
    String NUMCHAPTERS = "numChapters";
    String NUMPAGES = "numPages";
    String PUBLICATIONDATE = "publicationDate";
    String TITLE = "title";

    BasicAttribute<Book, String> author = BasicAttribute.of(
            Book.class, AUTHOR, String.class);
    TemporalAttribute<Book, Instant> copyrightDate = TemporalAttribute.of(
            Book.class, COPYRIGHTDATE, Instant.class);
    BooleanAttribute<Book> fiction = BooleanAttribute.of(
            Book.class, FICTION, boolean.class);
    TextAttribute<Book> id = TextAttribute.of(
            Book.class, ID);
    NumericAttribute<Book, Integer> numChapters = NumericAttribute.of(
            Book.class, NUMCHAPTERS, int.class);
    NumericAttribute<Book, Integer> numPages = NumericAttribute.of(
            Book.class, NUMPAGES, int.class);
    TemporalAttribute<Book, LocalDate> publicationDate = TemporalAttribute.of(
            Book.class, PUBLICATIONDATE, LocalDate.class);
    TextAttribute<Book> title = TextAttribute.of(
            Book.class, TITLE);
}