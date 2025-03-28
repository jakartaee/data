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
import jakarta.data.metamodel.restrict.Restriction;

public interface TextExpression<T> extends ComparableExpression<T,String> {

    default TextExpression<T> prepend(String string) {
        return TextFunctionExpression.of("concat", string, this);
    }
    default TextExpression<T> append(String string) {
        return TextFunctionExpression.of("concat", this, string);
    }

    default TextExpression<T> prepend(TextExpression<T> string) {
        return TextFunctionExpression.of("concat", string, this);
    }
    default TextExpression<T> append(TextExpression<T> string) {
        return TextFunctionExpression.of("concat", string, this);
    }

    default TextExpression<T> upper() {
        return TextFunctionExpression.of("upper", this);
    }
    default TextExpression<T> lower() {
        return TextFunctionExpression.of("lower", this);
    }

    default TextExpression<T> left(int length) {
        return TextFunctionExpression.of("left", this, length);
    }
    default TextExpression<T> right(int length) {
        return TextFunctionExpression.of("right", this, length);
    }

    default NumericExpression<T,Integer> length() {
        return NumericFunctionExpression.of("length", this);
    }

    default Restriction<T> like(Like pattern) {
        return BasicRestriction.of(this, pattern);
    }

    default Restriction<T> like(String pattern) {
        return BasicRestriction.of(this, Like.pattern(pattern));
    }

    default Restriction<T> like(String pattern, char charWildcard, char stringWildcard) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard);
        return BasicRestriction.of(this, constraint);
    }

    default Restriction<T> like(String pattern, char charWildcard, char stringWildcard, char escape) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard, escape);
        return BasicRestriction.of(this, constraint);
    }

    default Restriction<T> notLike(String pattern) {
        return BasicRestriction.of(this, NotLike.pattern(pattern));
    }

    default Restriction<T> notLike(String pattern, char charWildcard, char stringWildcard) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard);
        return BasicRestriction.of(this, constraint);
    }

    default Restriction<T> notLike(String pattern, char charWildcard, char stringWildcard, char escape) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard, escape);
        return BasicRestriction.of(this, constraint);
    }

    default Restriction<T> contains(String substring) {
        return BasicRestriction.of(this, Like.substring(substring));
    }

    default Restriction<T> notContains(String substring) {
        return BasicRestriction.of(this, NotLike.substring(substring));
    }

    default Restriction<T> startsWith(String prefix) {
        return BasicRestriction.of(this, Like.prefix(prefix));
    }

    default Restriction<T> notStartsWith(String prefix) {
        return BasicRestriction.of(this, NotLike.prefix(prefix));
    }

    default Restriction<T> endsWith(String suffix) {
        return BasicRestriction.of(this, Like.suffix(suffix));
    }

    default Restriction<T> notEndsWith(String suffix) {
        return BasicRestriction.of(this, NotLike.suffix(suffix));
    }

}
