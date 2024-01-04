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
package ee.jakarta.tck.data.web.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Web;
import ee.jakarta.tck.data.framework.utilities.TestPropertyUtility;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

@Web
@AnyEntity
public class ValidationTests {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(Rectangle.class, Rectangles.class);
    }
    
    @Inject
    private Rectangles rectangles;
    
    @BeforeEach
    public void cleanup() {
        rectangles.deleteAllBy();
        TestPropertyUtility.waitForEventualConsistency();
    }
    
    @Assertion(id = "133",
            strategy="Ensure that entities that do not violate the constraints can be successfully inserted into the database")
    public void testSaveWithValidConstraints() {        
        // Save
        Rectangle validRect = new Rectangle("RECT-000", 5L, 5L, 5, 5);
        validRect = rectangles.save(validRect);
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(1, rectangles.countBy());
        assertRectangleFields(validRect, getResults().get(0));
        
        // Save All
        List<Rectangle> validRects = List.of(
                validRect,
                new Rectangle("RECT-001", 0L, 0L, 1, 1), //min
                new Rectangle("RECT-002", 1800L, 1000L, 120, 80), //max
                new Rectangle("RECT-003", 1L, 1L, 1, 1) //positive
                );
        validRects = immutableListOf(rectangles.saveAll(validRects));
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(4, rectangles.countBy(), "Number of results was incorrect");
        
        //validate
        List<Rectangle> resultRects = getResults();
        for (int i = 0; i < resultRects.size(); i++) {
            assertRectangleFields(validRects.get(i), resultRects.get(i));
        } 
    }
    
    @Assertion(id = "133", strategy="Ensure that entities that violate the constraints cannot be inserted into the database")
    public void testSaveWithInvalidConstraints() {
        ConstraintViolationException resultingException;
        
        // Save
        Rectangle invalidRect = new Rectangle("RECT-010", -1L, -1L, -1, 0); //Min values
        
        resultingException = assertThrows(ConstraintViolationException.class, () -> {
            rectangles.save(invalidRect);
        });
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(0, rectangles.countBy(), "No rectangles should have presisted to database while violating constraints.");
        assertEquals(4, resultingException.getConstraintViolations().size(), "Incorrect number of constraint violations.");
        
        // Save All
        List<Rectangle> invalidRects = List.of(
                new Rectangle("", 5L, 5L, 5, 5), //blank id
                new Rectangle("RECT-012", 1801L, 5L, 5, 5), //Max x
                new Rectangle("RECT-013", 5L, 1001L, 5, 5), //Max y
                new Rectangle("RECT-014", 5L, 5L, 121, 5), //Max width
                new Rectangle("RECT-014", 5L, 5L, 5, 81) //Max height
                );
        
        resultingException = assertThrows(ConstraintViolationException.class, () -> {
            rectangles.saveAll(invalidRects);
        });
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(0, rectangles.countBy(), "No rectangles should have presisted to database while violating constraints.");
        assertEquals(5, resultingException.getConstraintViolations().size(), "Incorrect number of constraint violations.");        
    }
    
    @Assertion(id = "133",
            strategy="Ensure that entities that do not violate the constraints can be successfully updated in the database")
    public void testUpdateWithValidConstraints() {        
        // Save
        Rectangle validRect = new Rectangle("RECT-020", 5L, 5L, 5, 5);
        validRect = rectangles.save(validRect);
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(1, rectangles.countBy(), "Number of results was incorrect");
        assertRectangleFields(validRect, getResults().get(0));
        
        // Update
        Rectangle updatedRect = new Rectangle("RECT-020", 10L, 10L, 10, 10);
        updatedRect = rectangles.save(updatedRect);
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(1, rectangles.countBy(), "Number of results was incorrect");
        assertRectangleFields(updatedRect, getResults().get(0));     
    }
    
    @Assertion(id = "133",
            strategy="Ensure that entities that do not violate the constraints can be successfully updated in the database")
    public void testUpdateAllWithValidConstraints() {        
        // Save All
        List<Rectangle> validRects = List.of(
                new Rectangle("RECT-030", 5L, 5L, 5, 5),
                new Rectangle("RECT-031", 0L, 0L, 1, 1),
                new Rectangle("RECT-032", 1800L, 1000L, 120, 80),
                new Rectangle("RECT-033", 1L, 1L, 1, 1)
                );
        rectangles.saveAll(validRects);
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(4, rectangles.countBy(), "Number of results was incorrect");
        
        // Update All
        List<Rectangle> updatedRects = List.of(
                new Rectangle("RECT-030", 6L, 5L, 5, 5),
                new Rectangle("RECT-031", 0L, 1L, 1, 1),
                new Rectangle("RECT-032", 1800L, 999L, 120, 80),
                new Rectangle("RECT-033", 1L, 1L, 1, 2)
                );
        updatedRects = immutableListOf(rectangles.saveAll(updatedRects));
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(4, rectangles.countBy(), "Number of results was incorrect");
        
        // Verify All
        List<Rectangle> resultRects = getResults();
        for(int i = 0; i < resultRects.size(); i++) {
            assertRectangleFields(updatedRects.get(i), resultRects.get(i));
        }     
    }
    
    @Assertion(id = "133", strategy="Ensure that entities that violate the constraints cannot be updated in the database")
    public void testUpdateWithInvalidConstraints() {
        ConstraintViolationException resultingException;
        
        // Save
        Rectangle validRect = new Rectangle("RECT-040", 5L, 5L, 5, 5);
        validRect = rectangles.save(validRect);
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(1, rectangles.countBy(), "Number of results was incorrect");
        
        // Update
        Rectangle updatedInvalidRect = new Rectangle("RECT-040", -5L, -5L, -5, -5);
        resultingException = assertThrows(ConstraintViolationException.class, () -> {
            rectangles.save(updatedInvalidRect);
        });
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(1, rectangles.countBy(), "Number of results was incorrect");
        assertEquals(4, resultingException.getConstraintViolations().size());
        
        // Verify
        Rectangle resultRect = getResults().get(0);
        assertRectangleFields(validRect, resultRect);        
    }
    
    @Assertion(id = "133", strategy="Ensure that entities that violate the constraints cannot be updated in the database")
    public void testUpdateAllWithInvalidConstraints() {
        ConstraintViolationException resultingException;
        
        // Save All
        List<Rectangle> validRects = List.of(
                new Rectangle("RECT-050", 5L, 5L, 5, 5),
                new Rectangle("RECT-051", 5L, 5L, 5, 5),
                new Rectangle("RECT-052", 5L, 5L, 5, 5),
                new Rectangle("RECT-053", 5L, 5L, 5, 5)
                );
        validRects = immutableListOf(rectangles.saveAll(validRects));
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(4, rectangles.countBy(), "Number of results was incorrect");
        
        // Update All
        List<Rectangle> invalidRects = List.of(
                new Rectangle("RECT-050", 1801L, 5L, 5, 5), //Max x
                new Rectangle("RECT-051", 5L, 1001L, 5, 5), //Max y
                new Rectangle("RECT-052", 5L, 5L, 121, 5), //Max width
                new Rectangle("RECT-053", 5L, 5L, 5, 81) //Max height
                );
        resultingException = assertThrows(ConstraintViolationException.class, () -> {
            rectangles.saveAll(invalidRects);
        });
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(4, rectangles.countBy(), "Number of results was incorrect");
        assertEquals(4, resultingException.getConstraintViolations().size());
        
        // Verify
        List<Rectangle> resultRects = getResults();
        for(int i = 0; i < resultRects.size(); i++) {
            assertRectangleFields(validRects.get(i), resultRects.get(i));
        }     
    }
    
    @Assertion(id = "133", 
            strategy="Ensure that entities that do not violate the constraints on a method return data from the database")
    public void testValidResultsFromMethod() {
        // Save
        List<Rectangle> validRects = List.of(
                new Rectangle("RECT-060", 5L, 5L, 5, 5),
                new Rectangle("RECT-061", 0L, 0L, 1, 1),
                new Rectangle("RECT-062", 1800L, 1000L, 120, 80)
                );
        validRects = immutableListOf(rectangles.saveAll(validRects));
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(3, rectangles.countBy(), "Number of results was incorrect");
        
        // Get
        List<Rectangle> resultRects = rectangles.findAllByOrderByIdAsc();
        
        // Verify
        for(int i = 0; i < resultRects.size(); i++) {
            assertRectangleFields(validRects.get(i), resultRects.get(i));
        }
    }
    
    @Assertion(id = "133", 
            strategy="Ensure that entities that violate the constraints on a method do not return data from the database")
    public void testInvalidResultsFromMethod() {
        ConstraintViolationException resultingException;
        
        // Save
        List<Rectangle> invalidRects = List.of(
                new Rectangle("RECT-070", 5L, 5L, 5, 5),
                new Rectangle("RECT-071", 0L, 0L, 1, 1),
                new Rectangle("RECT-072", 1800L, 1000L, 120, 80),
                new Rectangle("RECT-073", 1L, 1L, 1, 1)
                );
        invalidRects = immutableListOf(rectangles.saveAll(invalidRects));
        
        TestPropertyUtility.waitForEventualConsistency();
        
        assertEquals(4, rectangles.countBy(), "Number of results was incorrect");
        
        // Get
        resultingException = assertThrows(ConstraintViolationException.class, () -> {
            rectangles.findAllByOrderByIdAsc(); //returns 4 results when max is 3
        });
        
        // Verify
        assertEquals(1, resultingException.getConstraintViolations().size());
    }
    
    /**
     * Converts an iterable to an immutable list
     * 
     * @param <T> object type
     * @param objects in an iterable that will be copied into a list
     * @return an immutable list of the objects provided by an iterable
     */
    private <T> List<T> immutableListOf(Iterable<T> objects) {
        return StreamSupport.stream(objects.spliterator(), false)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all results from repository and ensures they are ordered
     * 
     * @return List of all ordered results
     */
    private List<Rectangle> getResults() {
        return rectangles.findAll()
                .sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
                .toList();
    }
    
    /**
     * Softly asserts all fields of the rectangle entity are equal.
     * 
     * @param expected - The expected rectangle
     * @param actual - The actual rectangle
     * @param additionalMessages - Additional messages to add to a failed assertion
     */
    private void assertRectangleFields(Rectangle expected, Rectangle actual, String... additionalMessages) {
        assertAll("Asserting rectangle fields are equal." + 
                Stream.of(additionalMessages).collect(Collectors.joining(" ")), 
                () -> assertEquals(expected.getId(), actual.getId(), "Incorrect ID"),
                () -> assertEquals(expected.getX(), actual.getX(), "Incorrect X value"),
                () -> assertEquals(expected.getY(), actual.getY(), "Incorrect Y value"),
                () -> assertEquals(expected.getWidth(), actual.getWidth(), "Incorrect width value"),
                () -> assertEquals(expected.getHeight(), actual.getHeight(), "Incorrect height value")
                );
    }

}