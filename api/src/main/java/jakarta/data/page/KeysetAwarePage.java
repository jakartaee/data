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
package jakarta.data.page;

/**
 * <p>A page of results from a repository query that performs
 * {@link KeysetAwareSlice keyset pagination}.</p>
 *
 * <p>The concept of {@link Page} differs from {@link Slice} in that slices
 * do not have awareness of a total number of pages or results. Page numbers
 * and total numbers of pages and results cannot be accurately known
 * with keyset pagination, which allows entities to be added and removed
 * in between traversal of slices or pages. When keyset pagination is used,
 * these values should not be relied upon.</p>
 *
 * @param <T> the type of elements in this page 
 */
public interface KeysetAwarePage<T> extends KeysetAwareSlice<T>, Page<T> {
}