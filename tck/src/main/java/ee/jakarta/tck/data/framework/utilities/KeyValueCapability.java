/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.framework.utilities;

/**
 * Capability profile for Key-Value databases based on the current behavior model.
 *
 * <p>In the existing model, key-value databases are considered the least flexible
 * category. As a result, no query-related capabilities are assumed to be supported.</p>
 *
 * <p>This implementation reflects that behavior by not enabling any capabilities.
 * All operations beyond basic key-based access (put/get/delete) are treated as
 * unsupported at the database level.</p>
 *
 * <p><strong>Note:</strong> While some key-value databases may offer extended features,
 * this profile intentionally remains conservative to match the current TCK behavior.</p>
 */
public final class KeyValueCapability extends AbstractDatabaseCapability {
}