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
package ee.jakarta.tck.data.common.cdi;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This is a custom lifecycle annotation only supported by the Jakarta Data provider present
 * in this TCK and is used to verify the <code>@Repository</code> provider attribute is honored by 
 * any other Jakarta Data providers present on the tests classpath.
 * 
 * This lifecycle annotation is named in such a way that it should not conflict with any other 
 * custom lifecycle annotations from other Jakarta Data providers.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface PutTCKLifecyleMethod {

}
