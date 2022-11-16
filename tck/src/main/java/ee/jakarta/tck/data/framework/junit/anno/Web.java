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

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.ExtendWith;

import ee.jakarta.tck.data.framework.junit.extensions.AssertionExtension;
import ee.jakarta.tck.data.framework.junit.extensions.AssertionNameGenerator;

/**
 * These are test classes that REQUIRE web profile to be executed. For these
 * tests to run they must deploy an application to a Jakarta EE server using the
 * Arquillian {@code @Deployment} annotation.
 * 
 * At runtime the Arquillian Servlet protocol must be used for communication
 * with the Jakarta EE server.
 * 
 * This annotation is also inspected by the TCKArchiveProcessor 
 * to automatically add resources to the application deployed to the server.
 * 
 * @see ee.jakarta.tck.data.framework.arquillian.extensions.TCKArchiveProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tags({ @Tag("web"), @Tag("full") })
@ExtendWith({ ArquillianExtension.class, AssertionExtension.class })
@DisplayNameGeneration(AssertionNameGenerator.class)
public @interface Web {
}
