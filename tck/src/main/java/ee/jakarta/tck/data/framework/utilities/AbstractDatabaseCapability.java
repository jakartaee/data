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
 * Base implementation for {@link DatabaseCapability}.
 *
 * <p>This class represents a conservative capability profile where no
 * database features are assumed to be supported by default.</p>
 *
 * <p>Concrete implementations should explicitly override only the
 * capabilities they are expected to support. This avoids implicit
 * assumptions and ensures that support is always intentional and
 * documented.</p>
 *
 * <p><strong>Design note:</strong> Capability support may vary not only
 * by database family but also by specific provider and configuration.
 * Therefore, this base class does not assume any capability, even if it
 * is commonly available in certain database categories.</p>
 */
public abstract class AbstractDatabaseCapability implements DatabaseCapability {

    @Override public boolean capableOfAddition() { return false; }

    @Override public boolean capableOfAnd() { return false; }

    @Override public boolean capableOfAssignmentToExpression() { return false; }

    @Override public boolean capableOfBetween() { return false; }

    @Override public boolean capableOfAttributeVsAttributeComparison() { return false; }

    @Override public boolean capableOfConcat() { return false; }

    @Override public boolean capableOfConditionalDelete() { return false; }

    @Override public boolean capableOfConditionalUpdate() { return false; }

    @Override public boolean capableOfConstraintsOnNonIdAttributes() { return false; }

    @Override public boolean capableOfCount() { return false; }

    @Override public boolean capableOfCountingDeletes() { return false; }

    @Override public boolean capableOfCountingUpdates() { return false; }

    @Override public boolean capableOfDivision() { return false; }

    @Override public boolean capableOfGreaterThan() { return false; }

    @Override public boolean capableOfGreaterThanEqual() { return false; }

    @Override public boolean capableOfIn() { return false; }

    @Override public boolean capableOfLeft() { return false; }

    @Override public boolean capableOfLength() { return false; }

    @Override public boolean capableOfLessThan() { return false; }

    @Override public boolean capableOfLessThanEqual() { return false; }

    @Override public boolean capableOfLike() { return false; }

    @Override public boolean capableOfLower() { return false; }

    @Override public boolean capableOfMultipleSort() { return false; }

    @Override public boolean capableOfMultiplication() { return false; }

    @Override public boolean capableOfNotBetween() { return false; }

    @Override public boolean capableOfNotEqual() { return false; }

    @Override public boolean capableOfNotIn() { return false; }

    @Override public boolean capableOfNotLike() { return false; }

    @Override public boolean capableOfNotNull() { return false; }

    @Override public boolean capableOfNull() { return false; }

    @Override public boolean capableOfOr() { return false; }

    @Override public boolean capableOfParentheses() { return false; }

    @Override public boolean capableOfQueryWithoutWhere() { return false; }

    @Override public boolean capableOfRight() { return false; }

    @Override public boolean capableOfSingleSort() { return false; }

    @Override public boolean capableOfSubtraction() { return false; }

    @Override public boolean capableOfUpper() { return false; }
}