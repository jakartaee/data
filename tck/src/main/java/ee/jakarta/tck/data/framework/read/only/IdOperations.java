/**
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 * <p>
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.jakarta.tck.data.framework.read.only;

import java.util.List;

import jakarta.data.Limit;
import jakarta.data.repository.Query;

/**
 * This interface contains common operations for the NaturalNumbers and AsciiCharacters repositories.
 */
public interface IdOperations {
    long countByIdBetween(long minimum, long maximum);

    boolean existsById(long id);

    @Query("SELECT id WHERE id >= :inclusiveMin ORDER BY id ASC")
    List<Long> withIdEqualOrAbove(long inclusiveMin, Limit limit);
}