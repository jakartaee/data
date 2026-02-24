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
import java.util.Objects;

/**
 * A mock entity class for tests
 */
public class Book implements Cloneable {
    String author;
    Instant copyrightDate;
    boolean fiction;
    String id;
    int numChapters;
    int numPages;
    LocalDate publicationDate;
    String title;

    public static Book of(String id,
                          String title,
                          String author,
                          int numChapters,
                          int numPages,
                          Instant copyrightDate,
                          LocalDate publicationDate) {
        Book book = new Book();
        book.id = id;
        book.title = title;
        book.author = author;
        book.numChapters = numChapters;
        book.numPages = numPages;
        book.copyrightDate = copyrightDate;
        book.publicationDate = publicationDate;
        return book;
    }

    @Override
    public Book clone() {
        return Book.of(id,
                title,
                author,
                numChapters,
                numPages,
                copyrightDate,
                publicationDate);
    }

    @Override
    public boolean equals(Object other) {
        return other != null &&
               other instanceof Book book &&
               Objects.equals(book.id, id) &&
               Objects.equals(book.title,title) &&
               Objects.equals(book.author, author) &&
               book.numChapters == numChapters &&
               book.numPages == numPages &&
               Objects.equals(book.copyrightDate, copyrightDate) &&
               Objects.equals(book.publicationDate, publicationDate);
    }

    public String getAuthor() {
        return title;
    }

    public Instant getCopyrightDate() {
        return copyrightDate;
    }

    public String getId() {
        return id;
    }

    public int getNumChapters() {
        return numChapters;
    }

    public int getNumPages() {
        return numPages;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String value) {
        author = value;
    }

    public void setCopyrightDate(Instant value) {
        copyrightDate = value;
    }

    public void setId(String value) {
        id = value;
    }

    public void setId(LocalDate value) {
        publicationDate = value;
    }

    public void setNumChapters(int value) {
        numChapters = value;
    }

    public void setNumPages(int value) {
        numPages = value;
    }

    public void setTitle(String value) {
        title = value;
    }

    @Override
    public String toString() {
        return "Book@" + Integer.toHexString(System.identityHashCode(this)) +
                "#" + id + " " + title + " by " + author + "; " +
                numChapters + " chapters; " + numPages + " pages; ©" +
                copyrightDate + " published " + publicationDate;
    }
}
