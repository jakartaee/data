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
import jakarta.data.metamodel.constraint.NotLike;
import jakarta.data.metamodel.expression.NumericFunctionExpression;
import jakarta.data.metamodel.expression.TextFunctionExpression;
import jakarta.data.metamodel.restrict.BasicRestriction;

public interface TextExpression<T> extends ComparableExpression<T,String> {

    // QUESTION: do we really need to expose TextFunctionExpression & BasicRestriction here

    default TextFunctionExpression<T> prepend(String string) {
        return TextFunctionExpression.of("concat", string, this);
    }
    default TextFunctionExpression<T> append(String string) {
        return TextFunctionExpression.of("concat", this, string);
    }

    default TextFunctionExpression<T> prepend(TextExpression<T> string) {
        return TextFunctionExpression.of("concat", string, this);
    }
    default TextFunctionExpression<T> append(TextExpression<T> string) {
        return TextFunctionExpression.of("concat", string, this);
    }

    default TextFunctionExpression<T> upper() {
        return TextFunctionExpression.of("upper", this);
    }
    default TextFunctionExpression<T> lower() {
        return TextFunctionExpression.of("lower", this);
    }

    default TextFunctionExpression<T> left(int length) {
        return TextFunctionExpression.of("left", this, length);
    }
    default TextFunctionExpression<T> right(int length) {
        return TextFunctionExpression.of("right", this, length);
    }

    default NumericFunctionExpression<T,Integer> length() {
        return NumericFunctionExpression.of("length", this);
    }

    default BasicRestriction<T,String> like(Like pattern) {
        return BasicRestriction.of(this, pattern);
    }

    default BasicRestriction<T,String> like(String pattern) {
        return BasicRestriction.of(this, Like.pattern(pattern));
    }

    default BasicRestriction<T,String> like(String pattern, char charWildcard, char stringWildcard) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard);
        return BasicRestriction.of(this, constraint);
    }

    default BasicRestriction<T,String> like(String pattern, char charWildcard, char stringWildcard, char escape) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard, escape);
        return BasicRestriction.of(this, constraint);
    }

    default BasicRestriction<T,String> notLike(String pattern) {
        return BasicRestriction.of(this, NotLike.pattern(pattern));
    }

    default BasicRestriction<T,String> notLike(String pattern, char charWildcard, char stringWildcard) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard);
        return BasicRestriction.of(this, constraint);
    }

    default BasicRestriction<T,String> notLike(String pattern, char charWildcard, char stringWildcard, char escape) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard, escape);
        return BasicRestriction.of(this, constraint);
    }

    default BasicRestriction<T,String> contains(String substring) {
        return BasicRestriction.of(this, Like.substring(substring));
    }

    default BasicRestriction<T,String> notContains(String substring) {
        return BasicRestriction.of(this, NotLike.substring(substring));
    }

    default BasicRestriction<T,String> startsWith(String prefix) {
        return BasicRestriction.of(this, Like.prefix(prefix));
    }

    default BasicRestriction<T,String> notStartsWith(String prefix) {
        return BasicRestriction.of(this, NotLike.prefix(prefix));
    }

    default BasicRestriction<T,String> endsWith(String suffix) {
        return BasicRestriction.of(this, Like.suffix(suffix));
    }

    default BasicRestriction<T,String> notEndsWith(String suffix) {
        return BasicRestriction.of(this, NotLike.suffix(suffix));
    }

}
