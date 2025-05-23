/*
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

import java.io.Serializable;

@jakarta.nosql.Entity
@jakarta.persistence.Entity
public class NaturalNumber implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum NumberType {
        ONE, PRIME, COMPOSITE
    }

    @jakarta.nosql.Id
    @jakarta.persistence.Id
    private long id; //AKA the value

    @jakarta.nosql.Column
    private boolean isOdd;

    @jakarta.nosql.Column
    private Short numBitsRequired;

    // Sorting on enum types is vendor-specific in Jakarta Data.
    // Use numTypeOrdinal for sorting instead.
    @jakarta.nosql.Column
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private NumberType numType; // enum of ONE | PRIME | COMPOSITE

    @jakarta.nosql.Column
    private int numTypeOrdinal; // ordinal value of numType

    @jakarta.nosql.Column
    private long floorOfSquareRoot;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isOdd() {
        return isOdd;
    }

    public void setOdd(boolean isOdd) {
        this.isOdd = isOdd;
    }

    public Short getNumBitsRequired() {
        return numBitsRequired;
    }

    public void setNumBitsRequired(Short numBitsRequired) {
        this.numBitsRequired = numBitsRequired;
    }

    public NumberType getNumType() {
        return numType;
    }

    public void setNumType(NumberType numType) {
        this.numType = numType;
    }

    public int getNumTypeOrdinal() {
        return numTypeOrdinal;
    }

    public void setNumTypeOrdinal(int value) {
        numTypeOrdinal = value;
    }

    public long getFloorOfSquareRoot() {
        return floorOfSquareRoot;
    }

    public void setFloorOfSquareRoot(long floorOfSquareRoot) {
        this.floorOfSquareRoot = floorOfSquareRoot;
    }
}
