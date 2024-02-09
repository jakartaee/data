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
package jakarta.data.repository;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>Annotates a repository method that is search entities.</p>
 *
 * <p>The {@code Find} annotation indicates that the annotated repository method requests will execute a query by match parameter.
 * Thus, the types and names of the method parameters exactly match the types and names of the corresponding fields of the entity. Also,
 * there's no special naming convention for the @Find methods—they may be named arbitrarily, and their names encode no semantics.
 * </p>
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * {@code @Repository}
 * interface Garage {
 *     {@code @Find}
 *     List<Car> getCarWithModel({@code @By("model"} String model);
 * }
 * </pre>
 * <p>The {@code @Find} annotation can be used to indicate that the {@code getCarWithModel(model)} method is responsible for searching
 * a list of {@code Car} based at the model in parameter into a database.
 * </p>
 *
 * <p>If this annotation is combined with other operation annotations (e.g., {@code @Update}, {@code @Delete},
 *  {@code @Save}), it will throw an {@link UnsupportedOperationException} because only one operation type can be specified.
 *  A Jakarta Data provider implementation must detect (and report) this error at compile time or at runtime.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Find {
}
