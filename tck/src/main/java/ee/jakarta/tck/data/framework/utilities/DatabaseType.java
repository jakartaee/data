/*
 * Copyright (c) 2024,2026 Contributors to the Eclipse Foundation
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

import java.util.Arrays;

/**
 * This enum represents the configured DatabaseType based on the
 * {@link TestProperty} databaseType
 */
public enum DatabaseType {
    OTHER(Integer.MAX_VALUE, new OtherCapability()),// No database type was configured
    RELATIONAL(100, new RelationalCapability()),
    GRAPH(50, new GraphCapability()),
    DOCUMENT(40, new DocumentCapability()),
    TIME_SERIES(30, new TimeSeriesCapability()),
    COLUMN(20, new ColumnCapability()),
    KEY_VALUE(10, new KeyValueCapability());

    private final int flexibility;
    private final DatabaseCapability capability;

    DatabaseType(int flexibility, DatabaseCapability capability) {
        this.flexibility = flexibility;
        this.capability = capability;
    }

    public int flexibility() {
        return flexibility;
    }

    public DatabaseCapability capability() {
        return capability;
    }

    public boolean capableOfAddition() {
        return capability.capableOfAddition();
    }

    public boolean capableOfAnd() {
        return capability.capableOfAnd();
    }

    public boolean capableOfAssignmentToExpression() {
        return capability.capableOfAssignmentToExpression();
    }

    public boolean capableOfBetween() {
        return capability.capableOfBetween();
    }

    public boolean capableOfAttributeVsAttributeComparison() {
        return capability.capableOfAttributeVsAttributeComparison();
    }

    public boolean capableOfConcat() {
        return capability.capableOfConcat();
    }

    public boolean capableOfConditionalDelete() {
        return capability.capableOfConditionalDelete();
    }

    public boolean capableOfConditionalUpdate() {
        return capability.capableOfConditionalUpdate();
    }

    public boolean capableOfConstraintsOnNonIdAttributes() {
        return capability.capableOfConstraintsOnNonIdAttributes();
    }

    public boolean capableOfCount() {
        return capability.capableOfCount();
    }

    public boolean capableOfCountingDeletes() {
        return capability.capableOfCountingDeletes();
    }

    public boolean capableOfCountingUpdates() {
        return capability.capableOfCountingUpdates();
    }

    public boolean capableOfDivision() {
        return capability.capableOfDivision();
    }

    public boolean capableOfGreaterThan() {
        return capability.capableOfGreaterThan();
    }

    public boolean capableOfGreaterThanEqual() {
        return capability.capableOfGreaterThanEqual();
    }

    public boolean capableOfIn() {
        return capability.capableOfIn();
    }

    public boolean capableOfLeft() {
        return capability.capableOfLeft();
    }

    public boolean capableOfLength() {
        return capability.capableOfLength();
    }

    public boolean capableOfLessThan() {
        return capability.capableOfLessThan();
    }

    public boolean capableOfLessThanEqual() {
        return capability.capableOfLessThanEqual();
    }

    public boolean capableOfLike() {
        return capability.capableOfLike();
    }

    public boolean capableOfLower() {
        return capability.capableOfLower();
    }

    public boolean capableOfMultipleSort() {
        return capability.capableOfMultipleSort();
    }

    public boolean capableOfMultiplication() {
        return capability.capableOfMultiplication();
    }

    public boolean capableOfNotBetween() {
        return capability.capableOfNotBetween();
    }

    public boolean capableOfNotEqual() {
        return capability.capableOfNotEqual();
    }

    public boolean capableOfNotIn() {
        return capability.capableOfNotIn();
    }

    public boolean capableOfNotLike() {
        return capability.capableOfNotLike();
    }

    public boolean capableOfNotNull() {
        return capability.capableOfNotNull();
    }

    public boolean capableOfNull() {
        return capability.capableOfNull();
    }

    public boolean capableOfOr() {
        return capability.capableOfOr();
    }

    public boolean capableOfParentheses() {
        return capability.capableOfParentheses();
    }

    public boolean capableOfQueryWithoutWhere() {
        return capability.capableOfQueryWithoutWhere();
    }

    public boolean capableOfRight() {
        return capability.capableOfRight();
    }

    public boolean capableOfSingleSort() {
        return capability.capableOfSingleSort();
    }

    public boolean capableOfSortingNullsFirst() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfSortingNullsLast() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfSubtraction() {
        return capability.capableOfSubtraction();
    }

    public boolean capableOfUpper() {
        return capability.capableOfUpper();
    }


    public static DatabaseType valueOfIgnoreCase(String value) {
        return Arrays.stream(DatabaseType.values()).filter(type -> type.name().equalsIgnoreCase(value)).findAny().orElse(DatabaseType.OTHER);
    }

    /**
     * @deprecated Use explicit capability checks instead.
     */
    @Deprecated
    public boolean isKeywordSupportAtOrBelow(DatabaseType benchmark) {
        return this.flexibility <= benchmark.flexibility;
    }
}
