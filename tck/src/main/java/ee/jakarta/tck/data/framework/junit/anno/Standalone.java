/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.ExtendWith;

import ee.jakarta.tck.data.framework.junit.extensions.AssertionExtension;
import ee.jakarta.tck.data.framework.junit.extensions.AssertionNameGenerator;
import ee.jakarta.tck.data.framework.junit.extensions.StandaloneExtension;

/**
 * These are test classes that can DO NOT depend on any Jakarta EE server
 * technologies.
 * 
 * However, when running the TCK against a core/web/full profile server these
 * tests will be deployed and run on the server.
 * 
 * The dynamic method in which we achieve the ability to run these tests either
 * on the client JVM or server JVM depends on both the JUnit5 tag AND a system
 * property: {@link ee.jakarta.tck.data.framework.junit.extensions.StandaloneExtension#isStandaloneProperty}
 * 
 * If this property is true the Arquillian extension will be ignore,
 * otherwise we will attempt to deploy the test using Arquillian.
 * 
 * @see ee.jakarta.tck.data.framework.junit.extensions.StandaloneExtension
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Tags({ @Tag("standalone"), @Tag("core"), @Tag("web"), @Tag("full") })
@ExtendWith({ StandaloneExtension.class, AssertionExtension.class })
@DisplayNameGeneration(AssertionNameGenerator.class)
public @interface Standalone {
}
