/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

/**
 * <p>This package defines the Service Provider Interface (SPI) by which
 * Jakarta Data providers can make themselves discoverable by Jakarta EE products
 * for CDI dependency injection.</p>
 *
 * <p>Jakarta Data providers that provide their own custom dependency injection
 * or other vendor-specific mechanisms can ignore this SPI and do not need
 * to provide or implement it.</p>
 *
 * <p>Jakarta EE applications do not use the Jakarta Data Provider SPI.
 * Jakarta EE products are not required to make this package available to
 * applications.</p>
 */
package jakarta.data.spi.provider;