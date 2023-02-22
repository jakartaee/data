package ee.jakarta.tck.data.framework.arquillian.extensions;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentScenarioGenerator;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;

public class FlexibleEntityDeployment implements DeploymentScenarioGenerator {

        @Override
        public List<DeploymentDescription> generate(TestClass testClass) {
            Method m = getMethodForDeployment(testClass, TargetRepositoryProvider.get());
            
            if(m == null) {
                return Collections.emptyList();
            }
            
            Class<Archive<?>> archiveType = (Class<Archive<?>>) m.getReturnType();

            Archive<?> deployment;
            try {
                deployment = archiveType.cast(m.invoke(testClass));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

            return Arrays.asList(new DeploymentDescription("Flexibile entity deployment", deployment));
        }

        private Method getMethodForDeployment(TestClass clazz, TargetRepositoryProvider targetRepository) {
            Method[] methods = clazz.getMethods(Deployment.class);

            if (methods.length == 0) {
                return null;
            } else if (methods.length == 1) {
                return methods[0];
            } else {
                for (Method method : methods) {
                    if (method.getAnnotation(UseWithProvider.class) != null && 
                        method.getAnnotation(UseWithProvider.class).value().equals(targetRepository) ){
                        return method;
                    }
                }
                
                throw new IllegalStateException(                        
                        "Deployment method cannot be found. "
                        + "Are you sure you have an appropriate @UseWithProvider annotation "
                        + "on one of your @Deployment methods in " + clazz.getName() + "?");
            }
        }
}
