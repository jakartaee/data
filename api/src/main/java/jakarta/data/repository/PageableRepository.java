/*
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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

import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;

/**
 * <p>A built-in repository supertype with methods that use pagination and sorting to retrieve entities.
 * Methods that are inherited from {@link BasicRepository} provide additional basic built-in save, delete, and find
 * functionality.</p>
 *
 * <p>The type parameters of {@code PageableRepository<T,K>} capture the primary entity type ({@code T})
 * for the repository and the type of the Id entity attribute ({@code K}) that uniquely identifies each entity
 * of that type.</p>
 *
 * <p>The primary entity type is used for repository methods, such as {@code countBy...}
 * and {@code deleteBy...}, which do not explicitly specify an entity type.</p>
 *
 * <p>Example entity:</p>
 *
 * <pre>
 * {@code @Entity}
 * public class Person {
 *     {@code @Id}
 *     public long ssn;
 *     public String firstName;
 *     public String lastName;
 *     ...
 * }
 * </pre>
 *
 * <p>Example repository:</p>
 *
 * <pre>
 * {@code @Repository}
 * public interface People extends PageableRepository{@code <Person, Long>} {
 *
 *     long countByFirstName(String name);
 *
 *     ...
 * }
 * </pre>
 *
 * <p>Example usage:</p>
 *
 * <pre>
 * {@code @Inject}
 * People people;
 *
 * ...
 *
 * Person person1 = ...
 * person1 = people.save(person1);
 *
 * long howMany = people.countByFirstName(person1.firstName);
 *
 * Pagination page1Request = Pageable.of(Person.class).size(25).sortBy(Sort.asc("ssn"));
 * Page{@code <Person>} page1 = people.findAll(page1Request);
 * </pre>
 *
 * <p>The module JavaDoc provides an {@link jakarta.data/ overview} of Jakarta Data.</p>
 *
 * @param <T> the type of the primary entity class of the repository.
 * @param <K> the type of the Id attribute of the primary entity.
 * @see BasicRepository
 */
public interface PageableRepository<T, K> extends BasicRepository<T, K> {

    /**
     * Returns a {@link Page} of entities according to the page request that is provided as the {@link PageRequest} parameter.
     *
     * @param pageRequest the request for a paginated result; must not be {@code null}.
     * @return a page of entities; will never be {@code null}.
     * @throws NullPointerException when {@code pageable} is {@code null}.
     * @throws UnsupportedOperationException for Key-Value and Wide-Column databases when the {@link PageRequest.Mode#CURSOR_NEXT}
     * or {@link PageRequest.Mode#CURSOR_PREVIOUS} pagination mode is selected.
     * @see PageRequest.Mode
     */
    @Find
    Page<T> findAll(PageRequest<T> pageRequest);

}
