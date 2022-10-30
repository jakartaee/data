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

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Simple interface to ease streamability of {@link Iterable}s.
 * This is interface and can therefore be used as the assignment target for a lambda expression or method reference.
 */
@FunctionalInterface
public interface Streamable<T> extends Iterable<T>, Supplier<Stream<T>> {

    /**
     * Creates a non-parallel {@link Stream} of the underlying {@link Iterable}.
     *
     * @return a non-parallel Stream
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Creates a new, unmodifiable {@link Set}.
     *
     * @return a Set
     */
    default Set<T> toSet() {
        return stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    default Stream<T> get() {
        return stream();
    }

    /**
     * Returns whether the current {@link Streamable} is empty.
     *
     * @return whether the current {@link Streamable} is empty.
     */
    default boolean isEmpty() {
        return !iterator().hasNext();
    }

    /**
     * Creates a new, unmodifiable {@link List}.
     *
     * @return will never be {@literal null}.
     */
    default List<T> toList() {
        return stream().collect(Collectors.toUnmodifiableList());
    }
}
