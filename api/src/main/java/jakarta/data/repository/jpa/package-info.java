/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
/**
 * <p>JPA is a Java specification that defines a standard way to
 * manage relational data in Java applications.
 * JPA provides a set of interfaces and classes for object-relational mapping (ORM),
 * which allows developers to map Java objects to database tables and vice versa.
 * JPA implementations, such as Hibernate, EclipseLink, and OpenJPA,
 * provide concrete implementations of the JPA specification.</p>
 *
 * <p>This subdomain will handle a specialization of JPA to a repository,
 * meaning it will define classes and interfaces that provide a higher-level
 * abstraction of JPA's persistence capabilities specifically for repository usage in DDD.</p>
 *
 * <p>It might include classes implementing repository interfaces and using JPA's
 * EntityManager to perform CRUD (create, read, update, delete)
 * operations on entities. It might also include custom query methods
 * that use JPA's Criteria API or JPQL (Java Persistence Query Language)
 * to query entities more domain-specific way.</p>
 */
package jakarta.data.repository.jpa;