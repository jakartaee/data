/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.standalone.persistence;

import java.util.Set;

import jakarta.data.metamodel.BasicAttribute;
import jakarta.data.metamodel.NumericAttribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.metamodel.TextAttribute;

import ee.jakarta.tck.data.standalone.persistence.Product.Department;

@StaticMetamodel(Product.class)
public interface _Product {
    String DEPARTMENTS = "departments";
    String NAME = "name";
    String PRICE = "price";
    String PRODUCTNUM = "productNum";
    String VERSIONNUM = "versionNum";

    @SuppressWarnings("unchecked")
    BasicAttribute<Product, Set<Department>> departments =
            (BasicAttribute<Product, Set<Department>>)
            (BasicAttribute<Product, ?>)
            BasicAttribute.of(Product.class, DEPARTMENTS, Set.class);

    TextAttribute<Product> name =
            TextAttribute.of(Product.class, NAME);

    NumericAttribute<Product, Double> price =
            NumericAttribute.of(Product.class, PRICE, Double.class);

    TextAttribute<Product> productNum =
            TextAttribute.of(Product.class, PRODUCTNUM);

    NumericAttribute<Product, Long> versionNum =
            NumericAttribute.of(Product.class, VERSIONNUM, long.class);

}
