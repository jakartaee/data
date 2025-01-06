/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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

import java.util.Set;

import jakarta.data.Restrict;
import jakarta.data.Restriction;

/**
 * Represents an entity attribute in the {@link StaticMetamodel}.
 *
 * <p>The {@code Attribute} interface defines a contract for representing attributes of entity classes
 * in a static metamodel. This interface provides methods for building type-safe and expressive
 * restrictions directly based on entity attributes. It is commonly used with metadata classes
 * generated for static metamodels.</p>
 *
 * <p>For example, given the {@code Product} entity with a static metamodel, the following usage
 * is possible:</p>
 *
 * <pre>{@code
 * Restriction<Product> priceRestriction = _Product.price.equalTo(50.0D);
 * Restriction<Product> notInRestriction = _Product.category.notIn("Electronics", "Books");
 * }</pre>
 *
 * @param <T> the entity class to which this attribute belongs
 */
public interface Attribute<T> {

    /**
     * Obtain the entity attribute name, suitable for use wherever the specification requires
     * an entity attribute name. For example, as the parameter to {@link jakarta.data.Sort#asc(String)}.
     *
     * @return the entity attribute name.
     */
    String name();

    /**
     * Creates a restriction where the attribute's value must equal the specified value.
     *
     * <pre>{@code
     * Restriction<Product> priceRestriction = _Product.price.equalTo(50.0D);
     * }</pre>
     *
     * @param value the value to match
     * @return a {@link Restriction} representing the condition
     */
    default Restriction<T> equalTo(Object value) {
        return Restrict.equalTo(value, name());
    }

    /**
     * Creates a restriction where the attribute's value must be one of the specified values.
     *
     * <pre>{@code
     * Restriction<Product> categoryRestriction = _Product.category.in("Electronics", "Books");
     * }</pre>
     *
     * @param values the values to match
     * @return a {@link Restriction} representing the condition
     * @throws IllegalArgumentException if {@code values} is null or empty
     */
    default Restriction<T> in(Object... values) {
        if (values == null || values.length == 0)
            throw new IllegalArgumentException("values are required");

        return Restrict.in(Set.of(values), name());
    }

    /**
     * Creates a restriction where the attribute's value must be {@code null}.
     *
     * <pre>{@code
     * Restriction<Product> nullRestriction = _Product.description.isNull();
     * }</pre>
     *
     * @return a {@link Restriction} representing the condition
     */
    default Restriction<T> isNull() {
        return Restrict.equalTo(null, name());
    }


    /**
     * Creates a restriction where the attribute's value must not equal the specified value.
     *
     * <pre>{@code
     * Restriction<Product> priceRestriction = _Product.price.notEqualTo(100.0D);
     * }</pre>
     *
     * @param value the value that must not match
     * @return a {@link Restriction} representing the condition
     */
    default Restriction<T> notEqualTo(Object value) {
        return Restrict.notEqualTo(value, name());
    }

    /**
     * Creates a restriction where the attribute's value must not be one of the specified values.
     *
     * <pre>{@code
     * Restriction<Product> categoryRestriction = _Product.category.notIn("Food", "Clothing");
     * }</pre>
     *
     * @param values the values that must not match
     * @return a {@link Restriction} representing the condition
     * @throws IllegalArgumentException if {@code values} is null or empty
     */
    default Restriction<T> notIn(Object... values) {
        if (values == null || values.length == 0)
            throw new IllegalArgumentException("values are required");

        return Restrict.notIn(Set.of(values), name());
    }

    /**
     * Creates a restriction where the attribute's value must not be {@code null}.
     *
     * <pre>{@code
     * Restriction<Product> notNullRestriction = _Product.name.notNull();
     * }</pre>
     *
     * @return a {@link Restriction} representing the condition
     */
    default Restriction<T> notNull() {
        return Restrict.notEqualTo(null, name());
    }
}
