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
/**
 * Defines the capability profile of a database family or provider.
 *
 * <p>This interface represents what operations and expressions are expected
 * to be supported by a given database type. Implementations should document
 * nuances such as partial support, vendor-specific behavior, or limitations.</p>
 *
 * <p><strong>Important:</strong> A {@code true} value indicates expected support,
 * but does not guarantee uniform behavior across all vendors of the same family.</p>
 */
public interface DatabaseCapability {

    boolean capableOfAddition();

    boolean capableOfAnd();

    boolean capableOfAssignmentToExpression();

    boolean capableOfBetween();

    boolean capableOfAttributeVsAttributeComparison();

    boolean capableOfConcat();

    boolean capableOfConditionalDelete();

    boolean capableOfConditionalUpdate();

    boolean capableOfConstraintsOnNonIdAttributes();

    boolean capableOfCount();

    boolean capableOfCountingDeletes();

    boolean capableOfCountingUpdates();

    boolean capableOfDivision();

    boolean capableOfGreaterThan();

    boolean capableOfGreaterThanEqual();

    boolean capableOfIn();

    boolean capableOfLeft();

    boolean capableOfLength();

    boolean capableOfLessThan();

    boolean capableOfLessThanEqual();

    boolean capableOfLike();

    boolean capableOfLower();

    boolean capableOfMultipleSort();

    boolean capableOfMultiplication();

    boolean capableOfNotBetween();

    boolean capableOfNotEqual();

    boolean capableOfNotIn();

    boolean capableOfNotLike();

    boolean capableOfNotNull();

    boolean capableOfNull();

    boolean capableOfOr();

    boolean capableOfParentheses();

    boolean capableOfQueryWithoutWhere();

    boolean capableOfRight();

    boolean capableOfSingleSort();

    boolean capableOfSubtraction();

    boolean capableOfUpper();
}