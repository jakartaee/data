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

import jakarta.data.repository.BasicRepository;

/**
 * Indicates a failure that is due to inconsistent state between the entity and the database.
 * For example, {@link BasicRepository#delete(Object) delete(entity)}
 * or {@link BasicRepository#deleteAll(java.util.List) deleteAll(entities)}
 * where the entity Id no longer exists in the database or the entity is versioned and the
 * version no longer matches the version in the database.
 */
public class OptimisticLockingFailureException extends DataException {
    private static final long serialVersionUID = 1982179693469903341L;

    /**
     * Constructs a new OptimisticLockingFailureException exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public OptimisticLockingFailureException(String message) {
        super(message);
    }

    /**
     * Constructs a new OptimisticLockingFailureException exception with the specified detail message.
     *
     * @param message the detail message.
     * @param cause another exception or error that caused this exception.
     *        Null indicates that no other cause is specified.
     */
    public OptimisticLockingFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new OptimisticLockingFailureException exception with the specified cause.
     *
     * @param cause the cause.
     */
    public OptimisticLockingFailureException(Throwable cause) {
        super(cause);
    }
}
