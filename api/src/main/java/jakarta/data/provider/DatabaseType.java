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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.provider;

/**
 * <p>Types of databases that might be supported by a Jakarta Data provider.
 * These types are used by the {@link DataProvider#supportedDatabaseTypes()}
 * method to indicate which types of databases are supported.</p>
 */
public enum DatabaseType {
    /**
     * NoSQL column database.
     */
    COLUMN,

    /**
     * NoSQL document database.
     */
    DOCUMENT,

    /**
     * NoSQL graph database.
     */
    GRAPH,

    /**
     * NoSQL key-value database.
     */
    KEY_VALUE,

    /**
     * Relational database.
     */
    RELATIONAL
}