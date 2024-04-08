/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package jakarta.data.page;

public interface OffsetPageRequest extends PageRequest {


    /**
     * <p>Creates a new page request with the same pagination information,
     * but with the specified page number.</p>
     *
     * @param pageNumber The page number
     * @return a new instance of {@code PageRequest}.
     *         This method never returns {@code null}.
     */
    PageRequest page(long pageNumber);

    /**
     * Returns the page to be returned.
     *
     * @return the page to be returned.
     */
    long page();
}
