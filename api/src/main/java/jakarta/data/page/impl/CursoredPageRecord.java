/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package jakarta.data.page.impl;

import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Record type implementing {@link CursoredPage}.
 * This may be used to simplify implementation of a repository interface.
 *
 * @param content The page content, that is, the query results, in order
 * @param cursors A list of {@link PageRequest.Cursor} instances for result,
 *                in order
 * @param totalElements The total number of elements across all pages that
 *                      can be requested for the query
 * @param pageRequest The {@link PageRequest page request} for which this
 *                    slice was obtained
 * @param nextPageRequest A {@link PageRequest page request} for the next
 *                        page of results
 * @param previousPageRequest A {@link PageRequest page request} for the
 *                            previous page of results
 * @param <T> The type of elements on the page
 */
public record CursoredPageRecord<T>
        (List<T> content, List<PageRequest.Cursor> cursors, long totalElements, PageRequest<T> pageRequest,
         PageRequest<T> nextPageRequest, PageRequest<T> previousPageRequest)
        implements CursoredPage<T> {

    @Override
    public boolean hasContent() {
        return !content.isEmpty();
    }

    @Override
    public int numberOfElements() {
        return content.size();
    }

    @Override
    public boolean hasNext() {
        return nextPageRequest != null;
    }

    @Override
    public boolean hasPrevious() {
        return previousPageRequest != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> PageRequest<E> pageRequest(Class<E> entityClass) {
        return (PageRequest<E>) pageRequest;
    }

    @Override
    public PageRequest<T> nextPageRequest() {
        if (nextPageRequest == null)
            throw new NoSuchElementException();
        return nextPageRequest;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> PageRequest<E> nextPageRequest(Class<E> entityClass) {
        return (PageRequest<E>) nextPageRequest();
    }

    @Override
    public PageRequest<T> previousPageRequest() {
        if (previousPageRequest == null)
            throw new NoSuchElementException();
        return previousPageRequest;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> PageRequest<E> previousPageRequest(Class<E> entityClass) {
        return (PageRequest<E>) previousPageRequest();
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

    @Override
    public PageRequest.Cursor getKeysetCursor(int index) {
        return cursors.get(index);
    }

    @Override
    public boolean hasTotals() {
        return totalElements >= 0;
    }

    @Override
    public long totalElements() {
        if (totalElements<0) {
            throw new IllegalStateException("total elements are not available");
        }
        return totalElements;
    }

    @Override
    public long totalPages() {
        if (totalElements<0) {
            throw new IllegalStateException("total elements are not available");
        }
        int size = pageRequest.size();
        return (totalElements + size - 1) / size;
    }
}
