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
package ee.jakarta.tck.data.core.cdi;

import java.util.UUID;

/**
 * A test entity that will be persisted to a repository. Uses the custom
 * {@code @TCKEntity} annotation.
 *
 * @see ee.jakarta.tck.data.core.cdi.TCKEntity
 */
@TCKEntity
public record AddressRecord(UUID id, int house, String street, String city,
                            String state, long zipCode) {
}
