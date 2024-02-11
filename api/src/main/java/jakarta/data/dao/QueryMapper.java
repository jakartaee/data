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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.dao;


import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This interface holds the interfaces that compose the Fluent API for selecting and deleting NoSQL entities.
 */
public interface QueryMapper {


    /**
     * Represents the first step in the delete query fluent API
     */
    interface MapperDeleteFrom extends MapperDeleteQueryBuild {

        /**
         * Starts a new delete condition by a column name
         *
         * @param name the column name
         * @return a new {@link MapperDeleteNameCondition}
         * @throws NullPointerException when name is null
         */
        MapperDeleteNameCondition where(String name);
    }

    /**
     * Represents a delete condition based on a column name
     */
    interface MapperDeleteNameCondition {


        /**
         * Creates a delete condition where the column name provided is equal to the provided value
         *
         * @param value the value to the condition
         * @param <T>   the type
         * @return the {@link MapperDeleteWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperDeleteWhere eq(T value);

        /**
         * Creates a delete condition where the column name provided is like to the provided value
         *
         * @param value the value to the condition
         * @return the {@link MapperDeleteWhere}
         * @throws NullPointerException when value is null
         */
        MapperDeleteWhere like(String value);

        /**
         * Creates a delete condition where the column name provided is greater than to the provided value
         *
         * @param value the value to the condition
         * @param <T>   the type
         * @return the {@link MapperDeleteWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperDeleteWhere gt(T value);

        /**
         * Creates a delete condition where the column name provided is greater than or equal to the provided value
         *
         * @param <T>   the type
         * @param value the value to the condition
         * @return the {@link MapperDeleteWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperDeleteWhere gte(T value);

        /**
         * Creates a delete condition where the column name provided is less than the provided value
         *
         * @param <T>   the type
         * @param value the value to the condition
         * @return the {@link MapperDeleteWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperDeleteWhere lt(T value);

        /**
         * Creates a delete condition where the column name provided is less than or equal to the provided value
         *
         * @param <T>   the type
         * @param value the value to the condition
         * @return the {@link MapperDeleteWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperDeleteWhere lte(T value);

        /**
         * Creates a delete condition where the column name provided is between the provided values
         *
         * @param <T>    the type
         * @param valueA the values within a given range
         * @param valueB the values within a given range
         * @return the {@link MapperDeleteWhere}
         * @throws NullPointerException when either valueA or valueB are null
         */
        <T> MapperDeleteWhere between(T valueA, T valueB);

        /**
         * Creates a delete condition where the column name provided is in the provided iterable values
         *
         * @param values the values
         * @param <T>    the type
         * @return the {@link MapperDeleteWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperDeleteWhere in(Iterable<T> values);

        /**
         * Creates a NOT delete condition for the column name provided
         *
         * @return {@link MapperDeleteNotCondition}
         */
        MapperDeleteNotCondition not();
    }

    /**
     * Represents a NOT delete condition in the delete query fluent API
     */
    interface MapperDeleteNotCondition extends MapperDeleteNameCondition {
    }

    /**
     * Represents the last step of the delete query fluent API execution
     */
    interface MapperDeleteQueryBuild {


        /**
         * Executes the query
         */
        void execute();

    }

    /**
     * Represents a step where it's possible to perform a logical conjunction or disjunction adding one more delete condition or end up performing the built query
     */
    interface MapperDeleteWhere extends MapperDeleteQueryBuild {

        /**
         * Create a new delete condition performing logical conjunction (AND) by giving a column name
         *
         * @param name the column name
         * @return the same {@link MapperDeleteNameCondition} with the delete condition appended
         * @throws NullPointerException when name is null
         */
        MapperDeleteNameCondition and(String name);

        /**
         * Create a new delete condition performing logical disjunction (OR) by giving a column name
         *
         * @param name the column name
         * @return the same {@link MapperDeleteNameCondition} with the delete condition appended
         * @throws NullPointerException when name is null
         */
        MapperDeleteNameCondition or(String name);
    }

    /**
     * Represents the first step in the query fluent API
     */
    interface MapperFrom extends MapperQueryBuild {

        /**
         * Starts a new condition by given a column name
         *
         * @param name the column name
         * @return a new {@link MapperNameCondition}
         * @throws NullPointerException when name is null
         */
        MapperNameCondition where(String name);

        /**
         * Defines the position of the first result to retrieve.
         *
         * @param skip the first result to retrieve
         * @return a query with first result defined
         */
        MapperSkip skip(long skip);


        /**
         * Defines the maximum number of results to retrieve.
         *
         * @param limit the limit
         * @return a query with the limit defined
         */
        MapperLimit limit(long limit);

        /**
         * Add the order how the result will return
         *
         * @param name the name to be ordered
         * @return a query with the sort defined
         * @throws NullPointerException when name is null
         */
        MapperOrder orderBy(String name);
    }

    /**
     * Represents the step in the query fluent API where it's possible to define the maximum number of results to retrieve or to perform the query execution
     */
    interface MapperLimit extends MapperQueryBuild {

        /**
         * Defines the position of the first result to retrieve.
         *
         * @param skip the number of elements to skip
         * @return a query with first result defined
         */
        MapperSkip skip(long skip);
    }

    /**
     * Represents a condition based on a column name
     */
    interface MapperNameCondition {


        /**
         * Creates a condition where the column name provided is equal to the provided value
         *
         * @param value the value to the condition
         * @param <T>   the type
         * @return the {@link MapperWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperWhere eq(T value);

        /**
         * Creates a condition where the column name provided is like to the provided value
         *
         * @param value the value to the condition
         * @return the {@link MapperWhere}
         * @throws NullPointerException when value is null
         */
        MapperWhere like(String value);

        /**
         * Creates a condition where the column name provided is greater than to the provided value
         *
         * @param <T>   the type
         * @param value the value to the condition
         * @return the {@link MapperWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperWhere gt(T value);

        /**
         * Creates a condition where the column name provided is greater than or equal to the provided value
         *
         * @param <T>   the type
         * @param value the value to the condition
         * @return the {@link MapperWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperWhere gte(T value);

        /**
         * Creates a condition where the column name provided is less than the provided value
         *
         * @param <T>   the type
         * @param value the value to the condition
         * @return the {@link MapperWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperWhere lt(T value);

        /**
         * Creates a condition where the column name provided is less than or equal to the provided value
         *
         * @param <T>   the type
         * @param value the value to the condition
         * @return the {@link MapperWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperWhere lte(T value);

        /**
         * Creates a condition where the column name provided is between the provided values
         *
         * @param <T>    the type
         * @param valueA the values within a given range
         * @param valueB the values within a given range
         * @return the {@link MapperWhere}
         * @throws NullPointerException when either valueA or valueB are null
         */
        <T> MapperWhere between(T valueA, T valueB);

        /**
         * Creates a condition where the column name provided is in the provided iterable values
         *
         * @param values the values
         * @param <T>    the type
         * @return the {@link MapperWhere}
         * @throws NullPointerException when value is null
         */
        <T> MapperWhere in(Iterable<T> values);

        /**
         * Creates a NOT condition for the column name provided.
         *
         * @return {@link MapperNotCondition}
         */
        MapperNotCondition not();
    }

    /**
     * Represents the step in the query fluent API where it's possible to:
     * <ul>
     *     <li>define the order of the results</li>
     *     <li>or define the position of the first result</li>
     *     <li>or define the maximum number of results to retrieve</li>
     *     <li>or to perform the query execution</li>
     * </ul>
     */
    interface MapperNameOrder extends MapperQueryBuild {

        /**
         * Add the order of how the result will return based on a given column name
         *
         * @param name the column name to be ordered
         * @return a query with the sort defined
         * @throws NullPointerException when name is null
         */
        MapperOrder orderBy(String name);


        /**
         * Defines the position of the first result to retrieve.
         *
         * @param skip the first result to retrieve
         * @return a query with first result defined
         */
        MapperSkip skip(long skip);


        /**
         * Defines the maximum number of results to retrieve.
         *
         * @param limit the limit
         * @return a query with the limit defined
         */
        MapperLimit limit(long limit);
    }

    /**
     * Represents a NOT condition in the delete query fluent API
     */
    interface MapperNotCondition extends MapperNameCondition {
    }

    /**
     * Represents the step in the query fluent API where it's possible to define the order of the results or to perform the query execution
     */
    interface MapperOrder {


        /**
         * Defines the order ascending
         *
         * @return the {@link MapperNameOrder} instance
         */
        MapperNameOrder asc();

        /**
         * Defines the order as descending
         *
         * @return the {@link MapperNameOrder} instance
         */
        MapperNameOrder desc();
    }

    /**
     * Represents the last step of the query fluent API execution
     */
    interface MapperQueryBuild {


        /**
         * Executes the query and it returns as a {@link List}
         *
         * @param <T> the entity type
         * @return the result of the query
         */
        <T> List<T> result();

        /**
         * Executes the query and it returns as a {@link Stream}
         *
         * @param <T> the entity type
         * @return the result of the query
         */
        <T> Stream<T> stream();

        /**
         * Executes the query and returns the result as a single element otherwise it will return an {@link Optional#empty()}
         *
         * @param <T> the entity type
         * @return the result of the query that may have one or empty result
         */
        <T> Optional<T> singleResult();


    }

    /**
     * Represents the step in the query fluent API where it's possible to define the position of the first result to retrieve or to perform the query execution
     */
    interface MapperSkip extends MapperQueryBuild {


        /**
         * Defines the maximum number of results to retrieve.
         *
         * @param limit the limit
         * @return a query with the limit defined
         */
        MapperLimit limit(long limit);
    }

    /**
     * Represents a step where it's possible to:
     * <ul>
     *     <li>Create a new condition performing logical conjunction (AND) by giving a column name</li>
     *     <li>Or create a new condition performing logical disjunction (OR) by giving a column name</li>
     *     <li>Or define the position of the first result</li>
     *     <li>Or define the maximum number of results to retrieve</li>
     *     <li>Or define the order of the results</li>
     *     <li>Or to perform the query execution</li>
     * </ul>
     */
    interface MapperWhere extends MapperQueryBuild {


        /**
         * Create a new condition performing logical conjunction (AND) by giving a column name
         *
         * @param name the column name
         * @return the same {@link MapperNameCondition} with the condition appended
         * @throws NullPointerException when name is null
         */
        MapperNameCondition and(String name);

        /**
         * Create a new condition performing logical disjunction (OR) by giving a column name
         *
         * @param name the column name
         * @return the same {@link MapperNameCondition} with the condition appended
         * @throws NullPointerException when name is null
         */
        MapperNameCondition or(String name);

        /**
         * Defines the position of the first result to retrieve.
         *
         * @param skip the first result to retrieve
         * @return a query with first result defined
         */
        MapperSkip skip(long skip);


        /**
         * Defines the maximum number of results to retrieve.
         *
         * @param limit the limit
         * @return a query with the limit defined
         */
        MapperLimit limit(long limit);

        /**
         * Add the order how the result will return
         *
         * @param name the name to order
         * @return a query with the sort defined
         * @throws NullPointerException when name is null
         */
        MapperOrder orderBy(String name);
    }

}
