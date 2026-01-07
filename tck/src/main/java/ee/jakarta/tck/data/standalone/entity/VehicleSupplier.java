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

public class VehicleSupplier extends AbstractSupplier<Vehicle> {

    private static final List<Vehicle> VEHICLES = List.of(
            vehicle("00000000-0000-0000-0000-000000000001", "Model S", "Sedan", "Tesla", "Red", Transmission.AUTOMATIC),
            vehicle("00000000-0000-0000-0000-000000000002", "Model 3", "Sedan", "Tesla", "Blue", Transmission.AUTOMATIC),
            vehicle("00000000-0000-0000-0000-000000000003", "Civic", "Compact", "Honda", "White", Transmission.MANUAL),
            vehicle("00000000-0000-0000-0000-000000000004", "Accord", "Sedan", "Honda", "Black", Transmission.AUTOMATIC),
            vehicle("00000000-0000-0000-0000-000000000005", "Corolla", "Compact", "Toyota", "Silver", Transmission.MANUAL),
            vehicle("00000000-0000-0000-0000-000000000006", "Camry", "Sedan", "Toyota", "Gray", Transmission.AUTOMATIC),
            vehicle("00000000-0000-0000-0000-000000000007", "Golf", "Hatchback", "Volkswagen", "Blue", Transmission.MANUAL),
            vehicle("00000000-0000-0000-0000-000000000008", "Passat", "Sedan", "Volkswagen", "White", Transmission.AUTOMATIC),
            vehicle("00000000-0000-0000-0000-000000000009", "A4", "Sedan", "Audi", "Black", Transmission.AUTOMATIC),
            vehicle("00000000-0000-0000-0000-000000000010", "A3", "Hatchback", "Audi", "Red", Transmission.MANUAL)
    );

    @Override
    public List<Fruit> get() {
        return VEHICLES;
    }

    private static Vehicle vehicle(String id,
                            String model,
                            String make,
                            String manufacturer,
                            String color,
                            Transmission transmission) {

        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setModel(model);
        vehicle.setMake(make);
        vehicle.setManufacturer(manufacturer);
        vehicle.setColor(color);
        vehicle.setTransmission(transmission);
        return vehicle;
    }
}
