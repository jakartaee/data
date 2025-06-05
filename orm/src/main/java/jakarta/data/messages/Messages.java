/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package jakarta.data.messages;

import java.text.MessageFormat;
import java.util.ResourceBundle;

// This class is for internal use only and is not documented as API
// and its package is not exported by the module.
public class Messages {
    private static final ResourceBundle MESSAGES =
            ResourceBundle.getBundle("jakarta.data.messages.Messages");

    // Prevent instantiation
    private Messages() {
    }

    /**
     * Obtain a message and substitute in the supplied arguments.
     *
     * @param key  a message key from the Messages.properties file.
     * @param args arguments corresponding to {0}, {1}, ... in the message.
     * @return the message.
     */
    public static String get(String key, Object... args) {
        return MessageFormat.format(MESSAGES.getString(key),
                                    args);
    }
}
