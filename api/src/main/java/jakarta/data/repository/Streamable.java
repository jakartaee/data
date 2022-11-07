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
import java.util.stream.StreamSupport;

/**
 * Simple interface to ease streamability of {@link Iterable}s.
 * This is an interface and can therefore be used as the assignment target for a lambda expression or method reference.
 */
@FunctionalInterface
public interface Streamable<T> extends Iterable<T> {

    /**
     * Returns a sequential stream of results, which follow the order of the sort criteria if specified.
     * This method does not cause data to be re-fetched from the database when used with {@link Pageable pagination}.
     *
     * @return a stream of results.
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }


}
