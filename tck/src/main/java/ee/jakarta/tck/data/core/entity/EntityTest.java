package ee.jakarta.tck.data.core.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Core;
import jakarta.data.exceptions.MappingException;
import jakarta.inject.Inject;

/**
 * Figure out a way to test applications that use entities when
 */
@Core
public class EntityTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class).addClasses(Student.class, StudentDirectory.class);
    }

    //FIXME - This test could be a standalone test, but we would need some sort of provider.
    @Inject
    StudentDirectory directory;

    @Assertion(id = "EntityCreation", strategy = "Ensures that an entity is created and used based on provider")
    public void ensureEntityPersistedData() {
        List<Student> testData = new ArrayList<>();
        testData.add(new Student(1L, "Jimothy", 4));
        testData.add(new Student(2L, "Karn", 9));
        testData.add(new Student(3L, "Kyle", 32));
        testData.add(new Student(4L, "Brent", 45));
        testData.add(new Student(5L, "Lee", 92));
        
        testData.stream().forEach(student -> directory.save(student));
        
        int countAdults = directory.countByAgeGreaterThanEqual(18);
        assertEquals(3, countAdults, "Got unexpected count of adults");
        
        try {
            int countName = directory.countByNameIgnoreCase("kyle");
            fail("Name was transient should not have been able to count but was, " + countName);
        } catch (Exception e) {
            assertTrue(e instanceof MappingException, "Expected MappingException but got " + e.getClass().getCanonicalName());
        }
    }
}
