/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import ee.jakarta.tck.data.common.cdi.AddressRepository;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.Producer;
import jakarta.enterprise.inject.spi.ProducerFactory;

/**
 * A CDI producer for the AddressRepository
 * 
 * @param <R> The repository producer (i.e. this)
 * @param <P> The type of the bean containing the producer
 */
public class AddressRepositoryProducer<R, P> implements Producer<AddressRepository> {
    
    private static final Logger log = Logger.getLogger(AddressRepositoryProducer.class.getCanonicalName());
    
    /**
     * Factory class for this repository producer.
     */
    static class Factory<P> implements ProducerFactory<P> {
        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public <R> Producer<R> createProducer(Bean<R> bean) {
            return new AddressRepositoryProducer();
        }
    }

    @Override
    public void dispose(AddressRepository instance) {
        log.info("Directory CDI extension has been disposed: " + instance);
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public AddressRepository produce(CreationalContext<AddressRepository> cc) {
        AddressRepository instance = new AddressRepository();
        
        log.info("Address CDI extension has been produced: " + instance);

        return instance;
    }
}
