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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository;

/**
 * <p>Limits the number of results of a single invocation of a
 * repository find method to a maximum amount or to within a
 * positional range.</p>
 *
 * <p><code>Limit</code> is optionally specified as a parameter to a
 * repository method in one of the parameter positions after the
 * query parameters. For example,</p>
 *
 * <pre>
 * &#64;Query("SELECT o FROM Products o WHERE o.weight &lt;= ?1 AND o.width * o.length * o.height &lt;= ?2 ORDER BY o.price DESC")
 * Product[] freeShippingEligible(float maxWeight, float maxVolume, Limit limit);
 * 
 * ...
 * mostExpensive50 = products.freeShippingEligible(6.0f, 360.0f, Limit.of(50));
 * ...
 * secondMostExpensive50 = products.freeShippingEligible(6.0f, 360.0f, Limit.range(51, 100));
 * </pre>
 *
 * <p>A repository method will raise {@link IllegalArgumentException} if</p>
 * <ul>
 * <li>multiple <code>Limit</code> parameters are specified on the
 *     same method.</li>
 * <li><code>Limit</code> and {@link Pageable} parameters are specified on the
 *     same method.</li>
 * <li>a <code>Limit</code> parameter is specified in combination
 *     with the <code>First</code> keyword.</li>
 * </ul>
 */
public final class Limit {
    private static final int DEFAULT_START_AT = 1;
    private final int maxResults;
    private final int startAt;

    private Limit(int maxResults, int startAt) {
        this.maxResults = maxResults;
        this.startAt = startAt;
    }

    /**
     * <p>Maximum number of results that can be returned for a
     * single invocation of the repository method.</p>
     *
     * @return maximum number of results for a query.
     */
    public int maxResults() {
        return maxResults;
    }

    /**
     * <p>Offset at which to start when returning query results.
     * The first query result is position <code>1</code>.<p>
     *
     * @return offset of the first result.
     */
    public int startAt() {
        return startAt;
    }

    /**
     * <p>Create a limit that caps the number of results at the
     * specified maximum, starting from the first result.</p>
     *
     * @param maxResults maximum number of results.
     * @return limit that can be supplied to a <code>find...By</code>
     *         or <code>&#64;Query</code> method; will never be {@literal null}.
     * @throws IllegalArgumentException if maxResults is less than 1.
     */
    public static Limit of(int maxResults) {
        if (maxResults < 1)
            throw new IllegalArgumentException("maxResults: " + maxResults);

        return new Limit(maxResults, DEFAULT_START_AT);
    }

    /**
     * <p>Create a limit that restricts the results to a range,
     * beginning with the <code>startAt</code> position and
     * ending after the <code>endAt</code> position or the
     * position of the final result, whichever comes first.</p>
     *
     * @param startAt position at which to start including results.
     *                The first query result is position 1.
     * @param endAt   position after which to cease including results.
     * @return limit that can be supplied to a <code>find...By</code>
     *         or <code>&#64;Query</code> method; will never be {@literal null}.
     * @throws IllegalArgumentException if <code>startAt</code> is less than 1
     *         or <code>endAt</code> is less than <code>startAt</code>.
     */
    public static Limit range(int startAt, int endAt) {
        if (startAt < 1)
            throw new IllegalArgumentException("startAt: " + startAt);

        if (endAt < startAt)
            throw new IllegalArgumentException("startAt: " + startAt + ", endAt: " + endAt);

        return new Limit(endAt - startAt + 1, startAt);
    }
}
