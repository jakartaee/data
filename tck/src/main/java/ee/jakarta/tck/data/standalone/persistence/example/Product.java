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
package ee.jakarta.tck.data.standalone.persistence.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Product {

    @Id
    private Long id;
    
    private String name;
    private Double price;
    
    @Transient
    private Double surgePrice;
    
    public static Product of(Long id, String name, Double price, Double surgePrice) {
        return new Product(id, name, price, surgePrice);
    }

    public Product(Long id, String name, Double price, Double surgePrice) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.surgePrice = surgePrice;
    }

    public Product() {
        //do nothing
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSurgePrice() {
        return surgePrice;
    }

    public void setSurgePrice(Double surgePrice) {
        this.surgePrice = surgePrice;
    }
    
}
