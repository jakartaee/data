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
package jakarta.data.metamodel;

import jakarta.data.metamodel.constraint.Like;
import jakarta.data.metamodel.restrict.Restriction;

public interface TextExpression<T> extends ComparableExpression<T,String> {

    TextExpression<T> prepend(String string);
    TextExpression<T> append(String string);
    TextExpression<T> prepend(Expression<T,String> string);
    TextExpression<T> append(Expression<T,String> string);

    TextExpression<T> upper();
    TextExpression<T> lower();

    TextExpression<T> left(int length);
    TextExpression<T> right(int length);

    NumericExpression<T,Integer> length();

    @Override
    Restriction<T> equalTo(String value);
    @Override
    Restriction<T> notEqualTo(String value);

    Restriction<T> between(String min, String max);
    Restriction<T> notBetween(String min, String max);

    Restriction<T> greaterThan(String value);
    Restriction<T> greaterThanEqual(String value);
    Restriction<T> lessThan(String value);
    Restriction<T> lessThanEqual(String value);

    Restriction<T> like(Like pattern);
    Restriction<T> like(String pattern);
    Restriction<T> like(String pattern, char charWildcard, char stringWildcard);
    Restriction<T> like(String pattern, char charWildcard, char stringWildcard, char escape);
    Restriction<T> notLike(String pattern);
    Restriction<T> notLike(String pattern, char charWildcard, char stringWildcard);
    Restriction<T> notLike(String pattern, char charWildcard, char stringWildcard, char escape);

    Restriction<T> contains(String substring);
    Restriction<T> notContains(String substring);

    Restriction<T> startsWith(String prefix);
    Restriction<T> notStartsWith(String prefix);
    Restriction<T> endsWith(String suffix);
    Restriction<T> notEndsWith(String suffix);
}
