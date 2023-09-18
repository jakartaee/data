/*
 * Copyright (c) 023 Contributors to the Eclipse Foundation
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
 * Indicates that an entity cannot be inserted into the database
 * because an entity with same unique identifier already exists in the database.
 */
public class EntityExistsException extends DataException {
    private static final long serialVersionUID = -7275063477464065015L;

    /**
     * Constructs a new {@code EntityExistsException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public EntityExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code EntityExistsException} with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause another exception or error that caused this exception.
     *        Null indicates that no other cause is specified.
     */
    public EntityExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code EntityExistsException} with the specified cause.
     *
     * @param cause the cause.
     */
    public EntityExistsException(Throwable cause) {
        super(cause);
    }
}
