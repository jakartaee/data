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
package jakarta.data.orm;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Lifecycle annotation indicating that an annotated method
 * obtains a specified lock level on an entity, according to
 * the semantics of the {@code lock()} operation of
 * {@code jakarta.persistence.EntityManager}.
 * <p>
 * The annotation method should have one parameter typed to
 * an entity class, a second parameter of type
 * {@code jakarta.persistence.LockModeType}, and should be
 * declared {@code void}. Optionally, it may have a third
 * parameter of type {@code jakarta.persistence.LockOption}.
 * If the third parameter exists, it may be declared as a
 * variadic parameter ("varargs").
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Lock {
}
