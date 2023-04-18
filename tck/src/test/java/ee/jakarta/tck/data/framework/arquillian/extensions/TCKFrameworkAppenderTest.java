package ee.jakarta.tck.data.framework.arquillian.extensions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ee.jakarta.tck.data.framework.utilities.TestProperty;

public class TCKFrameworkAppenderTest {
    
    TCKFrameworkAppender appender = new TCKFrameworkAppender();

    @BeforeAll
    public static void init() {
        for(TestProperty prop : TestProperty.values()) {
            if(prop.getKey().startsWith("java.")) {
                continue;
            }
            System.setProperty(prop.getKey(), "testValue");
        }
    }
    
    @Test
    public void appenderShouldAlwaysIncludePackagesAndProperties() {
        Archive<?> library = appender.createAuxiliaryArchive();
        
        assertTrue(library instanceof JavaArchive);
        
        assertFalse(library.getContent().isEmpty());
        
        assertTrue(library.getContent().containsKey(new BasicPath("/ee/jakarta/tck/data/framework/junit/anno")));
        assertTrue(library.getContent().containsKey(new BasicPath("/ee/jakarta/tck/data/framework/junit/extensions")));
        assertTrue(library.getContent().containsKey(new BasicPath("/ee/jakarta/tck/data/framework/utilities")));
        assertTrue(library.getContent().containsKey(new BasicPath("/tck.properties")));
    }
}
