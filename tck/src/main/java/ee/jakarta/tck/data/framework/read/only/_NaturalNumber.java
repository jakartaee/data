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

import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.SortableAttribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.metamodel.impl.AttributeRecord;
import jakarta.data.metamodel.impl.SortableAttributeRecord;

/**
 * Static metamodel class for the NaturalNumber entity.
 */
@StaticMetamodel(NaturalNumber.class)
public interface _NaturalNumber {
    String FLOOROFSQUAREROOT = "floorOfSquareRoot";
    String ID = "id";
    String IS_ODD = "isOdd";
    String NUMBITSREQUIRED = "numBitsRequired";
    String NUMTYPE = "numType";
    String NUMTYPEORDINAL = "numTypeOrdinal";

    SortableAttribute<NaturalNumber> floorOfSquareRoot = new SortableAttributeRecord<>(FLOOROFSQUAREROOT);
    SortableAttribute<NaturalNumber> id = new SortableAttributeRecord<>(ID);
    SortableAttribute<NaturalNumber> isOdd = new SortableAttributeRecord<>(IS_ODD);
    SortableAttribute<NaturalNumber> numBitsRequired = new SortableAttributeRecord<>(NUMBITSREQUIRED);
    Attribute<NaturalNumber> numType = new AttributeRecord<>(NUMTYPE);
    SortableAttribute<NaturalNumber> numTypeOrdinal = new SortableAttributeRecord<>(NUMTYPEORDINAL);
}
