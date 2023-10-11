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
package ee.jakarta.tck.data.core.cdi.provider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

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
import jakarta.enterprise.inject.spi.WithAnnotations;

/**
 * A Jakarta Data provider extension that produces the Directory repository class.
 * This provider is only for testing and is not a real implementation. 
 * This extension verifies how a Jakarta Data provider is registered as a CDI extension.
 */
public class PersonExtension implements Extension {
    
    public static final String PERSON_PROVIDER = "PERSON_PROVIDER";
    
    private static final Logger log = Logger.getLogger(PersonExtension.class.getCanonicalName());
    
    private final ArrayList<Bean<?>> repositoryBeans = new ArrayList<>();

    private final HashSet<AnnotatedType<?>> repositoryTypes = new HashSet<>();

    public <T> void annotatedRepository(@Observes @WithAnnotations(Repository.class) ProcessAnnotatedType<T> event) {
        AnnotatedType<T> type = event.getAnnotatedType();

        Repository repository = type.getAnnotation(Repository.class);
        String provider = repository.provider();
        if (PERSON_PROVIDER.equals(provider)) {
            log.info("Person CDI Extension: adding repository " + repository.toString() + ' ' + type.getJavaClass().getName());
            repositoryTypes.add(type);
        } else {
            log.info("Person CDI Extension: ignore repository " + repository.toString() + ' ' + type.getJavaClass().getName());
        }
    }

    public void afterTypeDiscovery(@Observes AfterTypeDiscovery event, BeanManager beanMgr) {
        for (AnnotatedType<?> repositoryType : repositoryTypes) {
            Class<?> repositoryInterface = repositoryType.getJavaClass();

            Class<?> entityClass = null;
            for (Type interfaceType : repositoryInterface.getGenericInterfaces()) {
                if (interfaceType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) interfaceType;
                    if (parameterizedType.getRawType().getTypeName().startsWith(DataRepository.class.getPackageName())) {
                        Type typeParams[] = parameterizedType.getActualTypeArguments();
                        if (typeParams.length == 2 && typeParams[0] instanceof Class) {
                            entityClass = (Class<?>) typeParams[0];
                            break;
                        }
                    }
                }
            }

            if (entityClass == null)
                throw new MappingException("Did not find the entity class for " + repositoryInterface);

            PersonEntity entityAnno = entityClass.getAnnotation(PersonEntity.class);

            if (entityAnno == null) {
                Repository repository = repositoryType.getAnnotation(Repository.class);
                if (!Repository.ANY_PROVIDER.equals(repository.provider())) {
                    String message = "The Person Jakarta Data provider cannot provide the " +
                            repositoryType.getJavaClass().getName() + " repository because the repository's " +
                            entityClass.getName() + " entity class is not annotated with " + PersonEntity.class.getName();
                    throw new MappingException(message);
                }
            } else {
                BeanAttributes<?> attrs = beanMgr.createBeanAttributes(repositoryType);
                Bean<?> bean = beanMgr.createBean(attrs, repositoryInterface, new DirectoryRepositoryProducer.Factory<>());
                repositoryBeans.add(bean);
            }
        }
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanMgr) {
        for (Bean<?> bean : repositoryBeans) {
            event.addBean(bean);
        }
    }
}
