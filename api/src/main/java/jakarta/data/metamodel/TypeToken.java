/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import jakarta.annotation.Nonnull;
import jakarta.data.messages.Messages;

/**
 * <p>Captures a full generic type at compile time. This is useful in the
 * {@link StaticMetamodel} to represent entity attributes that are
 * collections. For example,
 *
 * <pre>{@code
 * BasicAttribute<Car, List<String>> repairs = BasicAttribute.of(
 *         Car.class, REPAIRS, new TypeToken<List<String>>(){});
 * }</pre>
 *
 * <p>This is the standard <em>super-type token</em> pattern: because the
 * anonymous class extends {@code TypeToken<List<String>>}, the JVM records
 * {@code List<String>} as its generic supertype, allowing
 * {@link #type()} to return the fully parameterized {@link Type} and
 * {@link #rawType()} to return {@code List.class}.
 *
 * @param <V> the type being represented.
 * @since 1.1
 */
public abstract class TypeToken<V> {

    private final Type type;

    /**
     * Captures the type argument {@code V} from the anonymous subclass.
     * Must be instantiated as an anonymous subclass. For example:
     * {@code new TypeToken<List<String>>(){}}.
     *
     * @throws IllegalArgumentException if constructed directly rather than
     *                                  as an anonymous subclass with a
     *                                  concrete type argument
     */
    protected TypeToken() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType parameterized) {
            type = parameterized.getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException(
                    Messages.get("016.must.be.anonymous",
                                 TypeToken.class.getSimpleName(),
                                 "new TypeToken<List<String>>(){}"));
        }
    }

    /**
     * Returns the raw (erased) {@link Class} for the captured type.
     * For example, for {@code TypeToken<List<String>>} this returns
     * {@code List.class}.
     *
     * @return the raw class
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public final Class<V> rawType() {
        if (type instanceof ParameterizedType parameterized) {
            return (Class<V>) parameterized.getRawType();
        }
        return (Class<V>) type;
    }

    /**
     * The full generic {@link Type} captured by this token. For example,
     * the {@link ParameterizedType} representing {@code List<String>}.
     *
     * @return the captured generic type
     */
    @Nonnull
    public final Type type() {
        return type;
    }
}
