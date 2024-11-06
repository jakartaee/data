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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository;

import jakarta.data.CompositeRestriction;
import jakarta.data.Restriction;
import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;

import java.util.List;
/**
 * Repository interface that supports advanced filtering using {@link Restriction} and
 * {@link CompositeRestriction}, along with type-safe sorting capabilities using {@link Sort}.
 * This enables developers to construct complex queries with multiple restrictions,
 * logical operations, sorting, and pagination.
 *
 * <p>The {@code RestrictionRepository} provides flexible query methods that accept both
 * single and composite restrictions, allowing for various filter conditions with type-safe sorting.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64;Inject
 * DriverLicenses licenses;
 *
 * // Define individual restrictions
 * Restriction&lt;DriverLicense&gt; licenseNumRestriction = Restriction.like("licenseNum", "ABC%");
 * Restriction&lt;DriverLicense&gt; expiryRestriction = Restriction.greaterThan("expiry", LocalDate.now());
 * Restriction&lt;DriverLicense&gt; regionRestriction = Restriction.equal("region", "North");
 *
 * // Combine restrictions with AND logic using CompositeRestriction.all
 * CompositeRestriction&lt;DriverLicense&gt; andRestriction = CompositeRestriction.all(
 *     licenseNumRestriction,
 *     expiryRestriction,
 *     regionRestriction
 * );
 *
 * // Define type-safe sorting criteria
 * Sort&lt;DriverLicense&gt; sortByExpiry = Sort.asc("expiry");
 * Sort&lt;DriverLicense&gt; sortByLicenseNumDesc = Sort.desc("licenseNum");
 *
 * // Use the composite restriction with sorting criteria
 * List&lt;DriverLicense&gt; sortedResults = licenses.findByRestriction(andRestriction, sortByExpiry, sortByLicenseNumDesc);
 *
 * // Paginated query with composite restriction and sorting
 * PageRequest pageRequest = PageRequest.of(0, 10);
 * Page&lt;DriverLicense&gt; pagedResults = licenses.findByRestriction(andRestriction, pageRequest, sortByExpiry, sortByLicenseNumDesc);
 *
 * // Count entities with a single restriction
 * long count = licenses.countByRestriction(expiryRestriction);
 * </pre>
 *
 * @param <T> the type of the primary entity class of the repository.
 * @param <K> the type of the unique identifier field or property of the primary entity.
 */
public interface RestrictionRepository<T, K> extends DataRepository<T, K> {

    /**
     * Finds entities by applying the specified composite restriction, with pagination and sorting.
     *
     * @param restriction the composite restriction containing filter conditions.
     * @param pageRequest the pagination information.
     * @param sorts       the sorting criteria.
     * @return a page of entities that match the provided restriction criteria, sorted as specified.
     */
    Page<T> findByRestriction(CompositeRestriction<T> restriction, PageRequest pageRequest, Sort<T>... sorts);

    /**
     * Finds all entities by applying the specified composite restriction, with sorting but without pagination.
     *
     * @param restriction the composite restriction containing filter conditions.
     * @param sorts       the sorting criteria.
     * @return a list of entities that match the provided restriction criteria, ordered by the specified fields.
     */
    List<T> findByRestriction(CompositeRestriction<T> restriction, Sort<T>... sorts);

    /**
     * Finds all entities by applying the specified composite restriction without pagination or sorting.
     *
     * @param restriction the composite restriction containing filter conditions.
     * @return a list of entities that match the provided restriction criteria.
     */
    List<T> findByRestriction(CompositeRestriction<T> restriction);

    /**
     * Counts entities by applying the specified restriction.
     *
     * @param restriction the restriction containing filter conditions.
     * @return the count of entities that match the restriction criteria.
     */
    long countByRestriction(Restriction<T> restriction);
}
