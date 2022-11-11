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
 * Indicates a connection-related failure that might be intermittent in nature. The operation might succeed if reattempted by the application. If the data access resource is enlisted in a transaction, the application must ensure that the transaction is rolled back before retrying the operation under a new transaction.
 */
public class DataAccessResourceFailureException extends DataException {

    /**
     * Constructs a new DataAccessResourceFailureException exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public DataAccessResourceFailureException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataAccessResourceFailureException exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public DataAccessResourceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DataAccessResourceFailureException exception with the specified cause.
     *
     * @param cause the cause.
     */
    public DataAccessResourceFailureException(Throwable cause) {
        super(cause);
    }
}
