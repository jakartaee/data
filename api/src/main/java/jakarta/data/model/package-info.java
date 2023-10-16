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
 * <p>A static metamodel for entities that are used in Jakarta Data repositories.</p>
 *
 * <p>The {@link StaticMetamodel} allows for type-safe operations that avoid the
 * need to hard-code entity attribute names as Strings. For example,</p>
 *
 * <pre>
 * &#64;Entity
 * public class Product {
 *     &#64;Id
 *     public long id;
 *
 *     public String name;
 *
 *     public float price;
 * }
 *
 * &#64;StaticMetamodel(Product.class)
 * public class Product_ {
 *     public static final Attribute id = Attribute.get();
 *     public static final Attribute name = Attribute.get();
 *     public static final Attribute price = Attribute.get();
 * }
 *
 * ...
 *
 * &#64;Repository
 * Product products;
 *
 * ...
 *
 * Pageable pageRequest = Pageable.ofSize(20)
 *                                .sortBy(Product_price.desc(),
 *                                        Product_name.asc(),
 *                                        Product_id.asc());
 *
 * page1 = products.findByNameLike(namePattern, pageRequest);
 * </pre>
 */
package jakarta.data.model;