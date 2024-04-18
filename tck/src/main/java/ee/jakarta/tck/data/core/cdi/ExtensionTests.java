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
package ee.jakarta.tck.data.core.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import ee.jakarta.tck.data.common.cdi.AddressBook;
import ee.jakarta.tck.data.common.cdi.AddressRecord;
import ee.jakarta.tck.data.common.cdi.Directory;
import ee.jakarta.tck.data.common.cdi.Person;
import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.CDI;
import ee.jakarta.tck.data.framework.junit.anno.Core;
import jakarta.inject.Inject;

@Core
@AnyEntity
@CDI
public class ExtensionTests {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(ExtensionTests.class.getPackage())
                .addPackage(Person.class.getPackage());
    }
    
    @Inject
    Directory directory;
    
    @Inject
    AddressBook addressBook;
    
    @Assertion(id = "133", strategy = "Verifies ability for a CDI BuildCompatibleExtension to handle "
            + "EntityDefining annotations and repository provider attributes.")
    public void testDataProviderWithBuildCompatibleExtension() {
        List<Person> result = directory.findByIdInOrderByAgeDesc(List.of(04L, 05L, 011L));
        List<String> firstNames = result.stream().map(p -> p.firstName).collect(Collectors.toList());
        List<String> lastNames = result.stream().map(p -> p.lastName).collect(Collectors.toList());
        assertEquals(List.of("Olivia", "Lauren", "Victor"), firstNames);
        assertEquals(List.of("Skinner", "Powell", "Gibson"), lastNames);
        
        
    }
    
    @Assertion(id = "640", strategy = "Verifies that another Jakarta Data Provider does not attempt to "
            + "implement the Dictonary repository based on provider attribute.")
    public void testDataRepositoryHonorsProviderAttribute() {
        long id = 013L;
        Person original = new Person(id, "Mark", "Pearson", 46);
        Person updated = new Person(id, "Mark", "Pearson", 45);
        
        try {
            assertEquals(null, directory.putPerson(original));
            assertEquals(original, directory.putPerson(updated));
        } finally {
            directory.deleteById(id);
        }
    }
    
    @Assertion(id = "640", strategy = "Verifies that another Jakarta Data Provider does not attempt to "
            + "implement the Address repository based on the EntityDefining annotation.")
    public void testDataRepositoryHonorsEntityDefiningAnnotation() {
        UUID id = UUID.randomUUID();
        AddressRecord original = new AddressRecord(id, 1057, "1st Street NW", "Rochester", "MN", 55901);
        AddressRecord updated = new AddressRecord(id, 1057, "1st Street NW", "Rochester", "MN", 55902);
        
        try {
            assertEquals(null, addressBook.putAddress(original));
            assertEquals(original, addressBook.putAddress(updated));
        } finally {
            addressBook.deleteById(id);
        }
    }
}
