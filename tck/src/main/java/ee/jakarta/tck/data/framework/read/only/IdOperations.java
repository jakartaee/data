/**
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.framework.read.only;

import java.util.List;
import java.util.stream.Stream;

import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;

/**
 * This interface contains common operations for the NaturalNumbers and AsciiCharacters repositories.
 *
 * @param <T> type of entity.
 */
public interface IdOperations<T> {
    Stream<T> findByIdBetween(long minimum, long maximum, Sort<T> sort);

    List<T> findByIdGreaterThanEqual(long minimum,
                                           Limit limit,
                                           Order<T> sorts);

    T[] findByIdLessThan(long exclusiveMax, Sort<T> primarySort, Sort<T> secondarySort);

    List<T> findByIdLessThanEqual(long maximum, Order<T> sorts);
}