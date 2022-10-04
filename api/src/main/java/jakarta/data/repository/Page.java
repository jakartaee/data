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

import java.util.stream.Stream;


/**
 * A page is a sublist of a list of objects. It allows gain information about the position of it in the containing entire list.
 *
 * @param <T> the entity type
 */
public interface Page<T> {


    /**
     * Returns the page content as {@link Stream}
     *
     * @return the page content as {@link Stream}.
     */
    Stream<T> getContent();


    /**
     * Returns the current page {@link Pageable#getPage()} of the page
     *
     * @return the current page
     */
    long getPage();

    /**
     * Returns the next page
     *
     * @return the next page that can return a Page with {@link Stream} or {@link Stream#empty()}
     */
    Page<T> next();
}
