/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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

/**
 * <p>A built-in repository supertype that is the root of all other built-in repository supertype interfaces.</p>
 *
 * <p>The type parameters of {@code DataRepository<T,K>} capture the primary entity type ({@code T})
 * for the repository and the type ({@code K}) of the field or property of the entity which uniquely
 * identifies each entity of that type.</p>
 *
 * <p>The primary entity type is used for repository methods, such as {@code countBy...}
 * and {@code deleteBy...}, which do not explicitly specify an entity type.</p>
 *
 * <p>Example entity:</p>
 *
 * <pre>
 * &#64;Entity
 * public class DriverLicense {
 *     &#64;Id
 *     public String licenseNum;
 *     public LocalDate expiry;
 *     ...
 * }
 * </pre>
 *
 * <p>Example repository:</p>
 *
 * <pre>
 * &#64;Repository
 * public interface DriverLicenses extends DataRepository&lt;DriverLicense, String&gt; {
 *
 *     boolean existsByLicenseNumAndExpiryGreaterThan(String num, LocalDate minExpiry);
 *
 *     &#64;Insert
 *     DriverLicense register(DriverLicense l);
 *
 *     &#64;Update
 *     boolean renew(DriverLicense l);
 *
 *     ...
 * }
 * </pre>
 *
 * <p>Example usage:</p>
 *
 * <pre>
 * &#64;Inject
 * DriverLicenses licenses;
 *
 * ...
 *
 * DriverLicense license = ...
 * license = licenses.register(license);
 *
 * boolean isValid = licenses.existsByLicenseNumAndExpiryGreaterThan(license.licenseNum,
 *                                                                   LocalDate.now());
 * </pre>
 *
 * <p>The module Javadoc provides an {@link jakarta.data/ overview} of Jakarta Data.</p>
 *
 * @param <T> the type of the primary entity class of the repository.
 * @param <K> the type of the unique identifier field of property of the primary entity.
 */
public interface DataRepository<T, K> {


}
