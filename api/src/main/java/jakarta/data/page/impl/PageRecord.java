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
package jakarta.data.page.impl;

import jakarta.data.messages.Messages;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Record type implementing {@link Page}. This may be used to simplify
 * implementation of a repository interface.
 *
 * @param pageRequest   The {@link PageRequest page request} for which this page
 *                      was obtained
 * @param content       The page content
 * @param totalElements The total number of elements across all pages that can
 *                      be requested for the query. A negative value indicates
 *                      that a total count of elements and pages is not
 *                      available.
 * @param moreResults   whether there is a (nonempty) next page of results
 * @param <T>           The type of elements on the page
 */
public record PageRecord<T>(PageRequest pageRequest, List<T> content,
                            long totalElements, boolean moreResults)
        implements Page<T> {

    // Disallow mutation of PageRequest and List fields after creation
    public PageRecord(PageRequest pageRequest,
                      List<T> content,
                      long totalElements,
                      boolean moreResults) {
        this.pageRequest = copy(pageRequest);
        this.content = List.copyOf(content);
        this.totalElements = totalElements;
        this.moreResults = moreResults;
    }

    /**
     * Constructs a new instance, computing the {@link #moreResults} component
     * as {@code true} if the page {@code content} is a full page of results and
     * the {@code totalElements} is either unavailable (indicated by a negative
     * value) or it exceeds the current
     * {@linkplain PageRequest#page() page number} multiplied by the
     * {@link PageRequest#size() size} of a full page.
     *
     * @param pageRequest   The {@link PageRequest page request} for which this
     *                      page was obtained.
     * @param content       The page content.
     * @param totalElements The total number of elements across all pages that
     *                      can be requested for the query. A negative value
     *                      indicates that a total count of elements and pages
     *                      is not available.
     */
    public PageRecord(PageRequest pageRequest, List<T> content, long totalElements) {
        this(pageRequest, content, totalElements,
                content.size() == pageRequest.size()
                        && (totalElements < 0
                        || totalElements > pageRequest.size() * pageRequest.page()));
    }

    /**
     * Copies a PageRequest.
     * This method is used by both PageRecord and CursoredPageRecord.
     *
     * @param request the PageRequest to copy.
     * @return copy of a PageRequest.
     */
    static PageRequest copy(PageRequest request) {
        if (request == null)
            return null;

        return switch (request.mode()) {
            case CURSOR_NEXT -> PageRequest.afterCursor(request.cursor().get(),
                                                        request.page(),
                                                        request.size(),
                                                        request.requestTotal());
            case CURSOR_PREVIOUS -> PageRequest.beforeCursor(request.cursor().get(),
                                                             request.page(),
                                                             request.size(),
                                                             request.requestTotal());
            case OFFSET -> PageRequest.ofPage(request.page(),
                                              request.size(),
                                              request.requestTotal());
        };
    }

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
        return moreResults;
    }

    @Override
    public PageRequest nextPageRequest() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return PageRequest.ofPage(pageRequest.page() + 1,
                pageRequest.size(),
                pageRequest.requestTotal());
    }

    @Override
    public boolean hasPrevious() {
        return pageRequest.page() > 1;
    }

    @Override
    public PageRequest previousPageRequest() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }

        return PageRequest.ofPage(pageRequest.page() - 1,
                pageRequest.size(),
                pageRequest.requestTotal());
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

    @Override
    public boolean hasTotals() {
        return totalElements >= 0;
    }

    @Override
    public long totalElements() {
        if (totalElements < 0) {
            throw new IllegalStateException(Messages.get("010.unknown.total"));
        }
        return totalElements;
    }

    @Override
    public long totalPages() {
        if (totalElements < 0) {
            throw new IllegalStateException(Messages.get("010.unknown.total"));
        }
        int size = pageRequest.size();
        return (totalElements + size - 1) / size;
    }
}
