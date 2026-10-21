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

/**
 * Restrictions add {@linkplain Constraint constraints} at run time.
 *
 * <p>A {@link Find} or {@link Delete} method of a repository may include a
 * <a href="../../../module-summary.html#SpecialParameters">
 * special parameter</a> of type {@link Restriction}. For example,
 *
 * {@snippet lang="java":
 * @Repository
 * public interface Cars extends CrudRepository<Car, String> {
 *
 *     @Find
 *     Page<Car> listedAfter(@By(_Car.LISTED) @Is(GreaterThan.class) LocalDate date,
 *                           Restriction<Car> restriction,
 *                           Order<Car> sorts,
 *                           PageRequest pageReq);
 * }
 * }
 *
 * <p>The application determines which constraints to include, typically
 * obtaining them from the {@linkplain StaticMetamodel static metamodel}.
 * For example,
 *
 * {@snippet lang="java":
 *     Page<Car> firstPage = cars.listedAfter(
 *             LocalDate.now().minusDays(100),
 *             Restrict.all(_Car.make.upper().equalTo("JAKARTA MOTORS"),
 *                          _Car.model.upper().in("JILVERADO", "JIERRA", "JUKON"),
 *                          _Car.year.atLeast(2022),
 *                          _Car.price.between(20000, 40000)),
 *             Order.by(_Car.price.desc(),
 *                      _Car.vin.asc()),
 *             PageRequest.ofSize(20));
 * }
 *
 * <p>The {@linkplain Attribute example entity and static metamodel} for the
 * above are provided in the {@link Attribute} Javadoc.</p>
 *
 * <p>The module Javadoc provides an {@linkplain jakarta.data/ overview} of
 * Jakarta Data.
 *
 * @since 1.1
 */
package jakarta.data.restrict;

import jakarta.data.constraint.Constraint;
import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
