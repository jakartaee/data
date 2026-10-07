/*
 * Copyright (c) 2023,2026 Contributors to the Eclipse Foundation
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
 * <p>A static metamodel for entities that are used in Jakarta Data
 * repositories.</p>
 *
 * <p>The {@link jakarta.data.metamodel.StaticMetamodel} allows for type-safe
 * operations that avoid the
 * need to hard-code entity attribute names as Strings. For example,
 *
 * {@snippet lang="java":
 * @Entity
 * public class Product {
 *     @Id
 *     public long id;
 *
 *     public String name;
 *
 *     public float price;
 *
 *     public LocalDate producedOn;
 * }
 * }
 *
 * {@snippet lang="java":
 * @StaticMetamodel(Product.class)
 * public interface _Product {
 *     String ID = "id";
 *     String NAME = "name";
 *     String PRICE = "price";
 *     String PRODUCEDON = "producedOn";
 *
 *     NumericAttribute<Product,Long> id = NumericAttribute.of(
 *             Product.class, ID, long.class);
 *     TextAttribute<Product> name = TextAttribute.of(
 *             Product.class, NAME);
 *     NumericAttribute<Product,Float> price = NumericAttribute.of(
 *             Product.class, PRICE, float.class);
 *     TemporalAttribute<Product,LocalDate> producedOn = TemporalAttribute.of(
 *             Product.class, PRODUCEDON, LocalDate.class);
 * }
 * }
 *
 * {@snippet lang="java":
 *     @Inject
 *     Products products;
 *
 *     ...
 *
 *     Order<Product> order =
 *             Order.by(_Product.price.desc(),
 *                      _Product.name.asc(),
 *                      _Product.id.asc());
 *
 *     page1 = products.findByNameLike(namePattern, pageRequest);
 * }
 *
 * <p>The module Javadoc provides an {@link jakarta.data/ overview} of Jakarta
 * Data.</p>
 */
package jakarta.data.metamodel;
