/*
 * Copyright (c) 2023, 2024 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.common.cdi;

import jakarta.enterprise.context.Dependent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * An implementation of the AddressBook repository interface.
 * Implementation is backed by an in-memory HashMap.
 */
@Dependent
public class AddressRepository implements AddressBook {
    
    private Map<UUID, AddressRecord> data = new HashMap<UUID, AddressRecord>();
    
    public AddressRepository() {
    }

    @Override
    public List<AddressRecord> findById(List<UUID> ids) {
        return data.values()
                .stream()
                .filter(a -> ids.contains(a.id()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public AddressRecord putAddress(AddressRecord address) {
        return data.put(address.id(), address);
    }

}
