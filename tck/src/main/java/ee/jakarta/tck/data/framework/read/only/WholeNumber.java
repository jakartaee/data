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

/**
 * A record that contains a subset of the attributes of the NaturalNumber
 * entity. where the record component names do not match the corresponding
 * entity attribute names.
 */
public record WholeNumber(
        int numType,
        long sqrtFloor,
        long value) implements Comparable<WholeNumber> {

    @Override
    public int compareTo(WholeNumber other) {
        return Long.compare(value, other.value);
    }

    public String toString() {
        return value +
                " " + NumberType.values()[numType] +
                " √" + value + " >= " + sqrtFloor;
    }
}
