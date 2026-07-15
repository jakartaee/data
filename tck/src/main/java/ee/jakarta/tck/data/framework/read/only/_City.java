/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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

import jakarta.data.metamodel.NumericAttribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.metamodel.TextAttribute;

/**
 * Static metamodel class for the City embeddable.
 */
@StaticMetamodel(City.class)
public interface _City {

    String NAME = "name";
    String POPULATION = "population";

    TextAttribute<City> name = //
                    TextAttribute.of(City.class, NAME);

    NumericAttribute<City, Integer> population = //
                    NumericAttribute.of(City.class, POPULATION, int.class);
}
