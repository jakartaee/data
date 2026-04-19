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
 * Capability profile for Document databases based on the current behavior model.
 *
 * <p>In the existing model, document databases introduce logical composition,
 * basic aggregation, and query flexibility beyond simple comparisons. This
 * includes support for logical operators, counting, sorting (single field),
 * and filtering without requiring a strict schema.</p>
 *
 * <p>While document databases often provide rich querying capabilities,
 * support may vary significantly across vendors, especially for advanced
 * expressions, arithmetic operations, and string manipulation functions.</p>
 *
 * <p>This profile reflects a conservative baseline consistent with the current
 * TCK behavior, enabling capabilities expected at the document level while
 * leaving more advanced features to higher categories.</p>
 */
public final class DocumentCapability extends AbstractDatabaseCapability {

    @Override
    public boolean capableOfAnd() {
        return true;
    }

    @Override
    public boolean capableOfConstraintsOnNonIdAttributes() {
        return true;
    }

    @Override
    public boolean capableOfCount() {
        return true;
    }

    @Override
    public boolean capableOfOr() {
        return true;
    }

    @Override
    public boolean capableOfQueryWithoutWhere() {
        return true;
    }

    @Override
    public boolean capableOfSingleSort() {
        return true;
    }

    // Inherited from COLUMN level

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