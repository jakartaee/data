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
 *  SPDX-License-Identifier: Apache-2.0
 */

package jakarta.data.repository;

import jakarta.data.DataException;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.BiFunction;

/**
 * Order implements the pairing of an {@link Direction} and a property. It is used to provide input for Sort
 */
public interface Order {


    /**
     * @return The property name to order by
     */
    String getProperty();

    /**
     * @return Returns whether sorting for this property shall be ascending.
     */
    boolean isAscending();

    /**
     * @return Returns whether sorting for this property shall be descending.
     */
    boolean isDescending();


    /**
     * Create a {@link Order} instance
     * @param property the property name to order by
     * @param direction The direction order by
     * @return an {@link Order} instance
     * @param <O> the Order type
     * @throws NullPointerException when there are null parameter
     */
    static <O extends Order> O of(String property, Direction direction) {
        Objects.requireNonNull(property, "property is required");
        Objects.requireNonNull(direction, "direction is required");

        OrderSupplier<O> supplier =
        ServiceLoader.load(OrderSupplier.class)
                .findFirst()
                .orElseThrow(() -> new DataException("There is no implementation of OrderSupplier on the Class Loader"));
        return supplier.apply(property, direction);
    }

    /**
     * Create a {@link Order} instance on ascending direction {@link  Direction#ASC}
     * @param property the property name to order by
     * @return the Order type
     * @return an {@link Order} instance
     * @param <O> the Order type
     * @throws NullPointerException when there property is null
     */
    static <O extends Order> O asc(String property){
        return of(property, Direction.ASC);
    }
    /**
     * Create a {@link Order} instance on descending direction {@link  Direction#DESC}
     * @param property the property name to order by
     * @return the Order type
     * @return an {@link Order} instance
     * @param <O> the Order type
     * @throws NullPointerException when there property is null
     */
    static <O extends Order> O desc(String property){
        return of(property, Direction.DESC);
    }

    /**
     * The {@link Order} supplier that the API will use on the method {@link Order#of(String, Direction)}
     * @param <O> the {@link  Order implementation}
     */
    interface OrderSupplier<O extends Order> extends BiFunction<String, Direction, O>{}

}
