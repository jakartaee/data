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

import jakarta.data.page.PageRequest;
import jakarta.data.page.Slice;

import java.util.Iterator;
import java.util.List;

/**
 * Record type implementing {@link Slice}.
 * This may be used to simplify implementation of a repository interface.
 *
 * @param pageRequest The {@link PageRequest page request} for which this
 *                    slice was obtained
 * @param content The page content
 * @param <T> The type of elements on the page
 */
public record SliceRecord<T>(PageRequest<T> pageRequest, List<T> content, boolean moreResults)
        implements Slice<T> {

    public SliceRecord(PageRequest<T> pageRequest, List<T> content) {
        this( pageRequest, content, content.size() == pageRequest.size() );
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
    @SuppressWarnings("unchecked")
    public <E> PageRequest<E> pageRequest(Class<E> entityClass) {
        return (PageRequest<E>) pageRequest;
    }

    @Override
    public PageRequest<T> nextPageRequest() {
        return moreResults ? pageRequest.next() : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> PageRequest<E> nextPageRequest(Class<E> entityClass) {
        return (PageRequest<E>) nextPageRequest();
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }
}
