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
import jakarta.data.metamodel.restrict.BasicRestriction;

public interface TextExpression<T> extends ComparableExpression<T,String> {

    default TextExpression<T> prepend(String string) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    default TextExpression<T> append(String string) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    // We could leave these two for later

//    default TextExpression<T> prepend(Expression<T,String> string) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//    default TextExpression<T> append(Expression<T,String> string) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }

    default TextExpression<T> upper() {
        throw new UnsupportedOperationException("not yet implemented");
    }
    default TextExpression<T> lower() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    default TextExpression<T> left(int length) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    default TextExpression<T> right(int length) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    default NumericExpression<T,Integer> length() {
        throw new UnsupportedOperationException("not yet implemented");
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
