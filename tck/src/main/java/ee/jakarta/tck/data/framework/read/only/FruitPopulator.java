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

import ee.jakarta.tck.data.standalone.entity.Fruit;

import java.util.List;

public class FruitPopulator implements Populator<FruitRepository> {

    public static final List<Fruit> FRUITS = List.of(
            fruit("00000000-0000-0000-0000-000000000001", "Apple", 10L),
            fruit("00000000-0000-0000-0000-000000000002", "Banana", 5L),
            fruit("00000000-0000-0000-0000-000000000003", "Orange", 10L),
            fruit("00000000-0000-0000-0000-000000000004", "Pear", 7L),
            fruit("00000000-0000-0000-0000-000000000005", "Grape", 3L),
            fruit("00000000-0000-0000-0000-000000000006", "Mango", 12L),
            fruit("00000000-0000-0000-0000-000000000007", "Apple", 5L),
            fruit("00000000-0000-0000-0000-000000000008", "Banana", 10L),
            fruit("00000000-0000-0000-0000-000000000009", "Orange", 7L),
            fruit("00000000-0000-0000-0000-000000000010", "Pear", 10L)
    );

    @Override
    public void populationLogic(FruitRepository repo) {
        repo.saveAll(FRUITS);
    }

    @Override
    public boolean isPopulated(FruitRepository repo) {
        return repo.countAll() == 10L;
    }

    private static Fruit fruit(String id, String name, Long quantity) {
        Fruit fruit = new Fruit();
        fruit.setId(id);
        fruit.setName(name);
        fruit.setQuantity(quantity);
        return fruit;
    }
}