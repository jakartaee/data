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
import jakarta.data.metamodel.function.NumericFunction;
import jakarta.data.metamodel.function.NumericFunctionRecord;
import jakarta.data.metamodel.function.TextFunction;
import jakarta.data.metamodel.function.TextFunctionRecord;
import jakarta.data.metamodel.restrict.BasicRestriction;

public interface TextExpression<T> extends ComparableExpression<T,String> {

    default TextFunction<T> prepend(String string) {
        return new TextFunctionRecord<>("concat", string, this);
    }
    default TextFunction<T> append(String string) {
        return new TextFunctionRecord<>("concat", this, string);
    }

    // We could leave these two for later

//    default TextFunction<T> prepend(Expression<T,String> string) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//    default TextFunction<T> append(Expression<T,String> string) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }

    default TextFunction<T> upper() {
        return new TextFunctionRecord<>("upper", this);
    }
    default TextFunction<T> lower() {
        return new TextFunctionRecord<>("lower", this);
    }

    default TextFunction<T> left(int length) {
        return new TextFunctionRecord<>("left", this, length);
    }
    default TextFunction<T> right(int length) {
        return new TextFunctionRecord<>("right", this, length);
    }

    default NumericFunction<T,Integer> length() {
        return new NumericFunctionRecord<>("length", this);
    }

    default BasicRestriction<T,String> like(Like pattern) {
        return new BasicRestrictionRecord<>(this, pattern);
    }

    default BasicRestriction<T,String> like(String pattern) {
        return new BasicRestrictionRecord<>(this, Like.pattern(pattern));
    }

    default BasicRestriction<T,String> like(String pattern, char charWildcard, char stringWildcard) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard);
        return new BasicRestrictionRecord<>(this, constraint);
    }

    default BasicRestriction<T,String> like(String pattern, char charWildcard, char stringWildcard, char escape) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard, escape);
        return new BasicRestrictionRecord<>(this, constraint);
    }

    default BasicRestriction<T,String> notLike(String pattern) {
        return new BasicRestrictionRecord<>(this, NotLike.pattern(pattern));
    }

    default BasicRestriction<T,String> notLike(String pattern, char charWildcard, char stringWildcard) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard);
        return new BasicRestrictionRecord<>(this, constraint);
    }

    default BasicRestriction<T,String> notLike(String pattern, char charWildcard, char stringWildcard, char escape) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard, escape);
        return new BasicRestrictionRecord<>(this, constraint);
    }

    default BasicRestriction<T,String> contains(String substring) {
        return new BasicRestrictionRecord<>(this, Like.substring(substring));
    }

    default BasicRestriction<T,String> notContains(String substring) {
        return new BasicRestrictionRecord<>(this, NotLike.substring(substring));
    }

    default BasicRestriction<T,String> startsWith(String prefix) {
        return new BasicRestrictionRecord<>(this, Like.prefix(prefix));
    }

    default BasicRestriction<T,String> notStartsWith(String prefix) {
        return new BasicRestrictionRecord<>(this, NotLike.prefix(prefix));
    }

    default BasicRestriction<T,String> endsWith(String suffix) {
        return new BasicRestrictionRecord<>(this, Like.suffix(suffix));
    }

    default BasicRestriction<T,String> notEndsWith(String suffix) {
        return new BasicRestrictionRecord<>(this, NotLike.suffix(suffix));
    }

}
