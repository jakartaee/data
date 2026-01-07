/**
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.standalone.entity;

import java.util.List;

public class FruitSupplier extends AbstractSupplier<Fruit> {

    @Override
    public List<Fruit> get() {

        Fruit f1 = fruit("00000000-0000-0000-0000-000000000001", "Apple", 10L);
        Fruit f2 = fruit("00000000-0000-0000-0000-000000000002", "Banana", 5L);
        Fruit f3 = fruit("00000000-0000-0000-0000-000000000003", "Orange", 10L);
        Fruit f4 = fruit("00000000-0000-0000-0000-000000000004", "Pear", 7L);
        Fruit f5 = fruit("00000000-0000-0000-0000-000000000005", "Grape", 3L);
        Fruit f6 = fruit("00000000-0000-0000-0000-000000000006", "Mango", 12L);
        Fruit f7 = fruit("00000000-0000-0000-0000-000000000007", "Apple", 5L);
        Fruit f8 = fruit("00000000-0000-0000-0000-000000000008", "Banana", 10L);
        Fruit f9 = fruit("00000000-0000-0000-0000-000000000009", "Orange", 7L);
        Fruit f10 = fruit("00000000-0000-0000-0000-000000000010", "Pear", 10L);

        return List.of(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10);
    }

    private Fruit fruit(String id, String name, Long quantity) {
        Fruit fruit = new Fruit();
        fruit.setId(id);
        fruit.setName(name);
        fruit.setQuantity(quantity);
        return fruit;
    }
}
