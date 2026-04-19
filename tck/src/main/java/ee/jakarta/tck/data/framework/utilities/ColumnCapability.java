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
 * Capability profile for Column-oriented databases based on the current behavior model.
 *
 * <p>In the existing model, column databases introduce basic comparison and
 * filtering capabilities beyond simple key-based access. This includes support
 * for range queries, null checks, and inclusion-based filtering.</p>
 *
 * <p>More advanced features such as arithmetic operations, string manipulation,
 * sorting, and conditional updates are not assumed to be supported at this level.</p>
 *
 * <p><strong>Note:</strong> Actual support may vary depending on the specific
 * implementation (e.g., wide-column vs analytical column stores), but this
 * profile reflects the conservative baseline defined in the current TCK behavior.</p>
 */
public final class ColumnCapability extends MinimalDatabaseCapability {

    @Override
    public boolean capableOfBetween() {
        return true;
    }

    @Override
    public boolean capableOfGreaterThan() {
        return true;
    }

    @Override
    public boolean capableOfGreaterThanEqual() {
        return true;
    }

    @Override
    public boolean capableOfIn() {
        return true;
    }

    @Override
    public boolean capableOfLessThan() {
        return true;
    }

    @Override
    public boolean capableOfLessThanEqual() {
        return true;
    }

    @Override
    public boolean capableOfNotBetween() {
        return true;
    }

    @Override
    public boolean capableOfNotEqual() {
        return true;
    }

    @Override
    public boolean capableOfNotIn() {
        return true;
    }

    @Override
    public boolean capableOfNotNull() {
        return true;
    }

    @Override
    public boolean capableOfNull() {
        return true;
    }
}