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
package ee.jakarta.tck.data.full.cdi.provider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ee.jakarta.tck.data.full.cdi.Directory;
import ee.jakarta.tck.data.full.cdi.Person;

/**
 * An implementation of the Directory repository interface.
 * Implementation is backed by an in-memory HashMap and has one function.
 */
public class DirectoryRepository implements Directory {
    
    private Map<Long, Person> data = new HashMap<Long, Person>();
    
    public DirectoryRepository() {
        data.put(01L, new Person(01L, "Alexander", "Grant", 72));
        data.put(02L, new Person(02L, "Bella", "Glover", 48));
        data.put(03L, new Person(03L, "Dorothy", "Wright", 67));
        data.put(04L, new Person(04L, "Lauren", "Powell", 14));
        data.put(05L, new Person(05L, "Olivia", "Skinner", 22));
        data.put(06L, new Person(06L, "Robert", "Green", 108));
        data.put(07L, new Person(07L, "Leonard", "Nolan", 29));
        data.put(010L, new Person(010L, "Michelle", "Parr", 59));
        data.put(011L, new Person(011L, "Victor", "Gibson", 12));
        data.put(012L, new Person(012L, "Grace", "Clarkson", 85));
    }

    @Override
    public List<String> findFirstNameByIdInOrderByAgeDesc(List<Long> ids) {
         return data.values()
             .stream()
             .filter(p -> ids.contains(p.id))
             .sorted(Comparator.comparing((Person p) -> p.age).reversed())
             .map(p -> p.firstName)
             .collect(Collectors.toList());

}

}
