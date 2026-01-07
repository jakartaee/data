/**
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import net.datafaker.Faker;

import java.util.Objects;
import java.util.UUID;

@jakarta.persistence.Entity
@Entity
public class Vehicle {

    @Id
    @jakarta.persistence.Id
    private String id;

    @Column
    private String model;

    @Column
    private String make;

    @Column
    private String manufacturer;

    @Column
    private String color;

    @Column
    private Transmission transmission;

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getMake() {
        return make;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getColor() {
        return color;
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", make='" + make + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", color='" + color + '\'' +
                ", transmission=" + transmission +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Vehicle vehicle)) {
            return false;
        }
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static Vehicle of(Faker faker) {
        var fakeVehicle = faker.vehicle();
        Vehicle vehicle = new Vehicle();
        vehicle.id = UUID.randomUUID().toString();
        vehicle.model = fakeVehicle.model();
        vehicle.make = fakeVehicle.make();
        vehicle.manufacturer = fakeVehicle.manufacturer();
        vehicle.color = fakeVehicle.color();
        vehicle.transmission = faker.number().positive() % 2 == 0 ? Transmission.MANUAL : Transmission.AUTOMATIC;
        return vehicle;
    }
}
