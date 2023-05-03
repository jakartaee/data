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
package ee.jakarta.tck.data.framework.junit.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import ee.jakarta.tck.data.framework.junit.extensions.PrepopulationExtension;
import ee.jakarta.tck.data.framework.read.only.Populator;
import jakarta.data.repository.CrudRepository;

/**
 * <p> 
 * These are test classes that perform read-only tests on entities.
 * The repository will be pre-populated prior to the test running.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ReadOnlyTests.class)
@ExtendWith({ PrepopulationExtension.class })
public @interface ReadOnlyTest {
    Class<? extends CrudRepository<?,?>> repository();
    Class<? extends Populator> populator();
}
