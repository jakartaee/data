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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.exceptions;

/**
 * This exception is raised when execution of a repository method with a singular
 * return type finds multiple results. To avoid raising an error for this, you can
 * use the {@code findFirst...} method name pattern or supply {@code Limit.of(1)}
 * as a parameter to explicitly request only the first result.
 */
public class NonUniqueResultException extends DataException {
    private static final long serialVersionUID = 7742411163016495764L;

    /**
     * Constructs a new NonUniqueResultException exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public NonUniqueResultException(String message) {
        super(message);
    }

    /**
     * Constructs a new NonUniqueResultException exception with the specified detail message.
     *
     * @param message the detail message.
     * @param cause another exception or error that caused this exception.
     *        Null indicates that no other cause is specified.
     */
    public NonUniqueResultException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new NonUniqueResultException exception with the specified cause.
     *
     * @param cause the cause.
     */
    public NonUniqueResultException(Throwable cause) {
        super(cause);
    }
}
