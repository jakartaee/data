package ee.jakarta.tck.data.framework.junit.extensions;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import ee.jakarta.tck.data.framework.arquillian.extensions.TargetRepositoryProvider;
import ee.jakarta.tck.data.framework.junit.anno.TestEntity;

public class EntityArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Method testMethod = context.getTestMethod().get();
        TestEntity testEntity = testMethod.getAnnotation(TestEntity.class);
        if(testEntity == null) {
            throw new NullPointerException("EntityArgumentProvider requires that test method is also annotated with TestEntities"); 
        }
        
        String entityPackage = TargetRepositoryProvider.get().getRepositoryPackage();
        Class<?> entityClazz = Class.forName(entityPackage + "." + testEntity.entityName());
        
        return Stream.of(Arguments.of(entityClazz));
    }
}
