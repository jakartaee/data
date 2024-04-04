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
package jakarta.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates an annotation as an entity qualifier within the Jakarta Data specification.
 * <p>
 * Annotations marked with {@code EntityQualifier} are recognized by annotation processors,
 * CDI extensions, and other components of the Jakarta Data framework as qualifiers for entities.
 * This approach allows for a flexible and standardized way to define entities without relying
 * on naming conventions or other inference methods.
 * <p>
 * Annotations that are annotated with {@code EntityQualifier} can be applied to classes to
 * indicate that they are entities. This enables Jakarta Data providers to easily identify and
 * process these entities.
 * <p>
 * Example of defining a custom entity annotation:
 * <pre>{@code
 * @EntityQualifier
 * @Target(ElementType.TYPE)
 * @Retention(RetentionPolicy.RUNTIME)
 * public @interface CustomEntity {
 * }
 * }</pre>
 *
 * Usage example of a custom entity annotation:
 * <pre>{@code
 * @CustomEntity
 * public class Book {
 *     // Implementation
 * }
 * }</pre>
 *
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntityQualifier {
}
