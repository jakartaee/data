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
package ee.jakarta.tck.data.framework.read.only;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

/**
 * This is a read only repository that represents the set of AsciiCharacters from 0-256. 
 * This repository will be pre-populated at test startup and verified prior to running tests.
 * 
 * TODO figure out a way to make this a ReadOnlyRepository instead.
 */
@Repository
public interface AsciiCharacters extends CrudRepository<AsciiCharacter, Long> {
    
    // READ - add more read only queries for test cases
    Optional<AsciiCharacter> findByDecimal(int id);

}
