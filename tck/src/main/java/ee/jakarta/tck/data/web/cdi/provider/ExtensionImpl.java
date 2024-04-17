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
package ee.jakarta.tck.data.web.cdi.provider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import ee.jakarta.tck.data.common.cdi.AddressBook;
import ee.jakarta.tck.data.common.cdi.Directory;
import ee.jakarta.tck.data.common.cdi.TCKEntity;
import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Repository;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AfterTypeDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanAttributes;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.ProducerFactory;
import jakarta.enterprise.inject.spi.WithAnnotations;

/**
 * A Jakarta Data provider extension that produces the Directory and Address repositories
 * class. This provider is only for testing and is not a real implementation.
 * This extension verifies how a Jakarta Data provider is registered as a CDI
 * extension.
 */
public class ExtensionImpl implements Extension {

    private static final Logger log = Logger.getLogger(ExtensionImpl.class.getCanonicalName());
    
    // Static relation between Repository and Producer
    private static final Map<Class<?>, ProducerFactory<?>> staticProducerMap = new HashMap<>();
    static {
        staticProducerMap.put(AddressBook.class, new AddressRepositoryProducer.Factory<>() );
        staticProducerMap.put(Directory.class, new DirectoryRepositoryProducer.Factory<>());
    }

    private final ArrayList<Bean<?>> repositoryBeans = new ArrayList<>();

    private final HashSet<AnnotatedType<?>> repositoryTypes = new HashSet<>();

    public <T> void annotatedRepository(@Observes @WithAnnotations(Repository.class) ProcessAnnotatedType<T> event) {
        AnnotatedType<T> type = event.getAnnotatedType();
        Class<?> repositoryClass = type.getJavaClass();
        
        // First determine if we support the repository based on entity
        Class<?> entityClass = getEntityClassFromRepository(repositoryClass);
        if (entityClass != null && entityClass.isAnnotationPresent(TCKEntity.class)) {
            log.info("This extension provides for the repository: " + repositoryClass.getCanonicalName());
            log.info("Based on Entity with @TCKEntity annotation: " + entityClass.getCanonicalName());
            repositoryTypes.add(type);
            return;
        }

        // Second determine if we support the repository based on provider attribute
        String provider = type.getAnnotation(Repository.class).provider();
        if (Directory.PERSON_PROVIDER.equals(provider) || AddressBook.ADDRESS_PROVIDER.equals(provider)) {
            log.info("This extension provides for the repository: " + repositoryClass.getCanonicalName());
            log.info("Based on the Repository provider: " + provider);
            repositoryTypes.add(type);
            return;
        }
        
        // Otherwise, do not support
        log.info("This extension does not provides for the repository: " + repositoryClass.getCanonicalName());

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void afterTypeDiscovery(@Observes AfterTypeDiscovery event, BeanManager beanMgr) {
        for (AnnotatedType<?> repositoryType : repositoryTypes) {
                        
            Class<?> repositoryInterface = repositoryType.getJavaClass();            
            ProducerFactory repositoryProducer = staticProducerMap.get(repositoryInterface);
            
            if(repositoryProducer != null) {
                BeanAttributes<?> attrs = beanMgr.createBeanAttributes(repositoryType);
                Bean<?> bean = beanMgr.createBean(attrs, repositoryInterface, repositoryProducer);
                repositoryBeans.add(bean);
                continue;
            }

            String message = "The Jakarta Data provider cannot provide the " + repositoryType.getJavaClass().getName() 
                    + " repository because there is no producer that supports this repository";
            throw new MappingException(message);
        }
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanMgr) {
        for (Bean<?> bean : repositoryBeans) {
            event.addBean(bean);
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
