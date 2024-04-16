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
package ee.jakarta.tck.data.core.cdi.provider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import ee.jakarta.tck.data.common.cdi.AddressBook;
import ee.jakarta.tck.data.common.cdi.AddressRepository;
import ee.jakarta.tck.data.common.cdi.Directory;
import ee.jakarta.tck.data.common.cdi.DirectoryRepository;
import ee.jakarta.tck.data.common.cdi.TCKEntity;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.build.compatible.spi.BuildCompatibleExtension;
import jakarta.enterprise.inject.build.compatible.spi.Enhancement;
import jakarta.enterprise.inject.build.compatible.spi.Synthesis;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanBuilder;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticComponents;
import jakarta.enterprise.inject.build.compatible.spi.Types;
import jakarta.enterprise.lang.model.AnnotationMember;
import jakarta.enterprise.lang.model.declarations.ClassInfo;

/**
 * A fake Jakarta Data provider extension that only produces a single repository class,
 * which is because it doesn't have a real implementation and is only for tests
 * that register a Jakarta Data provider as a CDI extension.
 */
public class BuildCompatibleExtensionImpl implements BuildCompatibleExtension {
    
    private static final Logger log = Logger.getLogger(BuildCompatibleExtensionImpl.class.getCanonicalName());
    
    // Static relation between Repository and Implementation
    private static final Map<Class<?>, Class<?>> staticImplMap = new HashMap<>();
    static {
        staticImplMap.put(AddressBook.class, AddressRepository.class);
        staticImplMap.put(Directory.class, DirectoryRepository.class);
    }
    
    // List of repository class names
    private final List<String> repositoryClassNames = new ArrayList<>();

    /**
     * Identify classes that are annotated with Repository
     * and determine which apply to this provider.
     */
    @Enhancement(withAnnotations = Repository.class, types = Object.class, withSubtypes = true)
    public void enhancement(ClassInfo repositoryClassInfo) throws Exception{
        
        // First determine if we support the repository based on entity
        Class<?> entityClass = getEntityClassFromRepository(Class.forName(repositoryClassInfo.name()));
        if(entityClass != null && entityClass.isAnnotationPresent(TCKEntity.class)) {
            log.info("This extension provides for the repository: " + repositoryClassInfo.name());
            log.info("Based on Entity with @TCKEntity annotation: " + entityClass.getCanonicalName());
            repositoryClassNames.add(repositoryClassInfo.name());
            return;
        }

        // Second determine if we support the repository based on provider attribute
        AnnotationMember providerMember = repositoryClassInfo.annotation(Repository.class).member("provider");
        String provider = providerMember.asString();
        if(Directory.PERSON_PROVIDER.equals(provider) || AddressBook.ADDRESS_PROVIDER.equals(provider)) {
            log.info("This extension provides for the repository: " + repositoryClassInfo.name());
            log.info("Based on the Repository provider: " + provider);
            repositoryClassNames.add(repositoryClassInfo.name());
            return;
        }
        
        //Otherwise, do not support
        log.info("This extension does not provides for the repository: " + repositoryClassInfo.name());
    }

    /**
     * Register beans for repositories.
     */
    @Synthesis
    public void synthesis(Types types, SyntheticComponents synth) throws ClassNotFoundException {
        for (String repoClassName : repositoryClassNames) {
            @SuppressWarnings("unchecked")
            Class<Object> repoClass = (Class<Object>) Class.forName(repoClassName);
            SyntheticBeanBuilder<Object> builder = synth
                            .addBean(repoClass)
                            .name(repoClassName)
                            .type(types.ofClass(repoClassName))
                            .scope(ApplicationScoped.class)
                            .withParam("impl", staticImplMap.get(repoClass))
                            .createWith(BeanCreator.class);
            
            log.info("Registered " + repoClassName + " bean with " + builder);
        }
    }
    
    private Class<?> getEntityClassFromRepository(Class<?> repositoryInterface) {
        for (Type interfaceType : repositoryInterface.getGenericInterfaces()) {
            if (interfaceType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) interfaceType;
                if (parameterizedType.getRawType().getTypeName().startsWith(DataRepository.class.getPackageName())) {
                    Type typeParams[] = parameterizedType.getActualTypeArguments();
                    if (typeParams.length == 2 && typeParams[0] instanceof Class) {
                        return (Class<?>) typeParams[0];
                    }
                }
            }
        }
        
        return null;
    }
}
