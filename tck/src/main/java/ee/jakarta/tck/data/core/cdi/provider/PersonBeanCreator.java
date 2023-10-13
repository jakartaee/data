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

import java.util.logging.Logger;

import ee.jakarta.tck.data.common.cdi.Directory;
import ee.jakarta.tck.data.common.cdi.DirectoryRepository;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.build.compatible.spi.Parameters;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanCreator;

/**
 * Creates beans for repositories for which the entity class has the PersonEntity annotation.
 */
public class PersonBeanCreator implements SyntheticBeanCreator<Object> {
    
    private static final Logger log = Logger.getLogger(PersonBeanCreator.class.getCanonicalName());
    
    @Override
    public Object create(Instance<Object> instance, Parameters parameters) {
        String provider = parameters.get("provider", String.class);
        if (provider == Directory.PERSON_PROVIDER) {
            log.info("Creating repository for " + instance + ", provider: " + provider);
            return new DirectoryRepository();
        } else {
            log.info("Bean creator does not support creating " + instance + " for provider " + provider);
            return null;
        }
    }
}
