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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.exceptions;

/**
 * This exception is raised when the data store is not capable of performing a particular function.
 * For example, some NoSQL databases are not capable of counting a total number of results,
 * and therefore raise this exception from repository methods that are declared to return
 * {@link jakarta.data.repository.Page Page}, which requires the total number of results.
 */
public class FunctionalityNotSupportedException extends MappingException {
    private static final long serialVersionUID = 4093503320173860847L;

    /**
     * Constructs a new FunctionalityNotSupportedException exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public FunctionalityNotSupportedException(String message) {
        super(message);
    }

    /**
     * Constructs a new FunctionalityNotSupportedException exception with the specified detail message.
     *
     * @param message the detail message.
     * @param cause another exception or error that caused this exception.
     *        Null indicates that no other cause is specified.
     */
    public FunctionalityNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FunctionalityNotSupportedException exception with the specified cause.
     *
     * @param cause the cause.
     */
    public FunctionalityNotSupportedException(Throwable cause) {
        super(cause);
    }
}
