/*
 * Copyright (c) 2023, 2024 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.core.cdi;

import static jakarta.data.repository.By.ID;

import java.util.List;

import jakarta.data.repository.By;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Repository;

/**
 * A Directory repository for testing.
 * <p>
 * Uses the 'provider' attribute to ensure that no Jakarta Data provider
 * implements this repository interface.
 */
@Repository(provider = Directory.PERSON_PROVIDER)
public interface Directory extends DataRepository<Person, Long> {

    // A string that is unique enough not to be used by any 
    // Jakarta Data provider attempting to run this TCK.
    public static final String PERSON_PROVIDER = "cb4d43ac-477a-4634-b3ee-a9ce81ea1801";

    List<Person> findByIdInOrderByAgeDesc(List<Long> ids);

    @Delete
    void deleteById(@By(ID) Long id);

}
