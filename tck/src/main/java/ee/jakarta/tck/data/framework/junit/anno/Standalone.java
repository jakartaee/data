/*
 * Copyright (c) 2022, 2024 Contributors to the Eclipse Foundation
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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import ee.jakarta.tck.data.framework.junit.extensions.AssertionExtension;
import ee.jakarta.tck.data.framework.junit.extensions.StandaloneExtension;
import ee.jakarta.tck.data.framework.utilities.TestProperty;

/**
 * <p>These are test classes that DO NOT depend on any Jakarta EE profile
 * technologies.</p>
 *
 * <p>However, when running the TCK against a core/web/platform profile
 * container these
 * tests will be deployed and run on the container.</p>
 *
 * <p>The dynamic method in which we achieve the ability to run these tests
 * either
 * on the client JVM or container JVM depends on both the JUnit5 Standalone tag
 * AND the system property: {@link TestProperty#skipDeployment}  </p>
 *
 * <p>If this property is "true" the Arquillian extension will be ignored,
 * otherwise we will attempt to deploy the test using Arquillian.</p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag("standalone")
@Tag("core")
@Tag("web")
@Tag("platform")
@ExtendWith({StandaloneExtension.class, AssertionExtension.class})
public @interface Standalone {
}
