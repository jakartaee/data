/**
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.framework.read.only;

/**
 * Enumation of regions that is used by the Country entity.
 */
public enum Region {
    // Keep these in alphabetical order so that tests can reliably sort on
    // them regardless of whether the Jakarta Data provider uses ordinal or
    // alphabetical order.
    AFRICA,
    ANTARCTICA,
    ASIA,
    CARIBBEAN,
    EUROPE,
    NORTH_AMERICA,
    OCEANIA,
    SOUTH_AMERICA
}
