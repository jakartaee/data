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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates an annotation as an entity-defining annotation type within the Jakarta Data framework.
 * <p>
 * Annotations marked with {@code EntityQualifier} are recognized by annotation processors,
 * CDI extensions, and other components of the Jakarta Data framework as declaring an entity types.
 * Jakarta Data providers and persistence models that provide custom entity-defining annotations
 * (entity annotations other than {@code jakarta.persistence.Entity} and {@code jakarta.nosql.Entity})
 * must annotate each custom entity-defining annotation with this annotation. Applications must
 * not use this annotation directly.
 * </p>
 * <p>
 * By marking an annotation with {@code EntityQualifier}, Jakarta Data providers enable these annotations
 * to be applied to classes, indicating that they are entities. This enables Jakarta Data providers to easily
 * identify and process these entities, facilitating a standardized integration across different data management
 * implementations.
 * </p>
 *
 * Example of defining a custom entity annotation by a provider:
 * <pre>{@code
 * @EntityQualifier
 * @Target(ElementType.TYPE)
 * @Retention(RetentionPolicy.RUNTIME)
 * public @interface CustomEntity {
 * }
 * }</pre>
 * Example usage of a provider-defined custom entity annotation:
 * <pre>{@code
 * @CustomEntity
 * public class Book {
 *     // Implementation details here
 * }
 * }</pre>
 *
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntityQualifier {
}
