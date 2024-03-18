/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.jakarta.tck.data.web.validation;

import java.util.List;
import java.util.stream.Stream;

import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Repository
public interface Rectangles extends DataRepository<Rectangle, String> {
    
    @Save
    Rectangle save(@Valid Rectangle entity);

    @Save
    List<Rectangle> saveAll(@Valid List<Rectangle> entities);
    
    @PositiveOrZero
    long countBy();

    @Find
    Stream<Rectangle> findAll();
    
    void deleteAllBy();

    @Size(min = 0, max = 3)
    List<Rectangle> findAllByOrderByIdAsc();
}
