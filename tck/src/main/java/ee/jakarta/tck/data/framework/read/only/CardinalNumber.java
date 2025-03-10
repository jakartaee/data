/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import jakarta.data.repository.Select;

/**
 * A record that contains a subset of the attributes of the NaturalNumber entity.
 * where the Select annotation is used to identify entity attribute names that do
 * not match the record component names.
 */
public record CardinalNumber(
        // Select annotation is not needed when the name matches
        Short numBitsRequired,
        @Select(_NaturalNumber.NUMTYPEORDINAL)
        int numType,
        @Select(_NaturalNumber.ID)
        long value) {

    @Override
    public String toString() {
        return value + " " +
                NumberType.values()[numType] + " (" +
                numBitsRequired + " bits)";
    }
}
