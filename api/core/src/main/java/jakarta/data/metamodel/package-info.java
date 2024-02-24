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
 * public class _Product {
 *     public static volatile {@code SortableAttribute<Product>} id;
 *     public static volatile {@code TextAttribute<Product>} name;
 *     public static volatile {@code SortableAttribute<Product>} price);
 * }
 *
 * ...
 *
 * &#64;Repository
 * Products products;
 *
 * ...
 *
 * {@code Pagerequest<Product>} pageRequest = Order.by(_Product.price.desc(),
 *                                             _Product.name.asc(),
 *                                             _Product.id.asc())
 *                                         .page(1)
 *                                         .size(20);
 *
 * page1 = products.findByNameLike(namePattern, pageRequest);
 * </pre>
 *
 * <p>The module JavaDoc provides an {@link jakarta.data/ overview} of Jakarta Data.</p>
 */
package jakarta.data.metamodel;
