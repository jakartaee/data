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

package jakarta.data.exceptions;

/**
 * Thrown by the data provider when a problem occurs.
 */
public class DataException extends RuntimeException {


    /**
     * Constructs a new DataException exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public DataException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataException exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public DataException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DataException exception with the specified cause.
     *
     * @param cause the cause.
     */
    public DataException(Throwable cause) {
        super(cause);
    }
}
