/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

import java.lang.annotation.*;

/**
 * A hint to the Jakarta Data implementation specifying that
 * an attribute of the entity returned by a parameter-based
 * automatic query method will be accessed by the application
 * program. The repository implementation should ensure that
 * access to the value of the specified attribute is efficient.
 * <pre>{@code
 * @Find
 * @Fetching(_Book.AUTHORS)
 * @Fetching(_Book.PUBLISHER)
 * Book bookWithAuthorsAndPublisher(String isbn);
 * }</pre>
 *
 * @see Find
 * @since 1.1
 */
@Repeatable(Fetching.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Fetching {
    /**
     * An attribute of the entity returned by the parameter-based
     * automatic query method. This attribute will be accessed by
     * the application program.
     */
    String value();

    /**
     * Enables multiple {@code Fetching} annotations on the method.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface List {
        /**
         * Returns a list of annotations.
         *
         * @return list of annotations.
         */
        Fetching[] value();
    }
}
