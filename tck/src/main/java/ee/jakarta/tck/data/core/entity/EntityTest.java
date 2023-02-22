package ee.jakarta.tck.data.core.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import ee.jakarta.tck.data.framework.arquillian.extensions.TargetRepositoryProvider;
import ee.jakarta.tck.data.framework.arquillian.extensions.UseWithProvider;
import ee.jakarta.tck.data.framework.junit.anno.Core;
import ee.jakarta.tck.data.framework.junit.anno.ParameterizedAssertion;
import ee.jakarta.tck.data.framework.junit.anno.TestEntity;
import ee.jakarta.tck.data.framework.junit.extensions.EntityArgumentProvider;
import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.DataRepository;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Figure out a way to test applications that use entities when
 */
@Core
public class EntityTest {

    @Deployment(name = "NOSQL")
    @UseWithProvider(value = TargetRepositoryProvider.NOSQL)
    public static JavaArchive createNoSQLDeployment() {
        return ShrinkWrap.create(JavaArchive.class).addPackage("ee.jakarta.tck.data.common.entity.nosql");
    }
    
    @Deployment(name = "PERSISTANCE")
    @UseWithProvider(value = TargetRepositoryProvider.PERSISTANCE)
    public static JavaArchive createPersistanceDeployment() {
        return ShrinkWrap.create(JavaArchive.class).addPackage("ee.jakarta.tck.data.common.entity.persistance");
    }

    @ParameterizedTest
    @TestEntity(entityName = "Student")
    @ArgumentsSource(EntityArgumentProvider.class)
    @ParameterizedAssertion(id = "EntityCreation", strategy = "Ensures that an entity is created and used based on provider")
    public void ensureEntityPersistedData(Class<?> student) throws Exception {
        Constructor<?> studentConst = student.getConstructor(Long.class, String.class, int.class);
        
        Class<DataRepository> repoClazz = (Class<DataRepository>) Class.forName(student.getPackageName() + "StudentDirectory");
        DataRepository studentDirectory = CDI.current().select(repoClazz).get();
        
        
        List<Object> testData = new ArrayList<>();
        testData.add(studentConst.newInstance(1L, "Jimothy", 4));
        testData.add(studentConst.newInstance(2L, "Karn", 9));
        testData.add(studentConst.newInstance(3L, "Kyle", 32));
        testData.add(studentConst.newInstance(4L, "Brent", 45));
        testData.add(studentConst.newInstance(5L, "Lee", 92));
        
        testData.stream().forEach(s -> {
            try {
                studentDirectory.getClass().getMethod("add", student).invoke(studentDirectory, s);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        
        
        int countAdults = (int) studentDirectory.getClass().getMethod("countByAgeGreaterThanEqual", int.class).invoke(studentDirectory,18);
        assertEquals(3, countAdults, "Got unexpected count of adults");
        
        try {
            int countName = (int) studentDirectory.getClass().getMethod("countByNameIgnoreCase", String.class).invoke(studentDirectory, "kyle");
            fail("Name was transient should not have been able to count but was, " + countName);
        } catch (Exception e) {
            assertTrue(e instanceof MappingException, "Expected MappingException but got " + e.getClass().getCanonicalName());
        }
    }
}
