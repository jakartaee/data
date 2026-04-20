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
    OTHER(Integer.MAX_VALUE), //No database type was configured
    RELATIONAL(100),
    GRAPH(50),
    DOCUMENT(40),
    TIME_SERIES(30),
    COLUMN(20),
    KEY_VALUE(10);

    private int flexibility;

    private DatabaseType(int flexibility) {
        this.flexibility = flexibility;
    }

    public boolean capableOfAddition() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfAnd() {
        return flexibility >= DOCUMENT.flexibility;
    }

    public boolean capableOfAssignmentToExpression() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfBetween() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfAttributeVsAttributeComparison() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfConcat() {
        return flexibility >= GRAPH.flexibility;
    }

    public boolean capableOfConditionalDelete() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfConditionalUpdate() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfConstraintsOnNonIdAttributes() {
        return flexibility >= DOCUMENT.flexibility;
    }

    public boolean capableOfCount() {
        return flexibility >= DOCUMENT.flexibility;
    }

    public boolean capableOfCountingDeletes() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfCountingUpdates() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfDivision() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfGreaterThan() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfGreaterThanEqual() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfIn() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfLeft() {
        return flexibility >= GRAPH.flexibility;
    }

    public boolean capableOfLength() {
        return flexibility >= GRAPH.flexibility;
    }

    public boolean capableOfLessThan() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfLessThanEqual() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfLike() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfLower() {
        return flexibility >= GRAPH.flexibility;
    }

    public boolean capableOfMultipleSort() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfMultiplication() {
        return flexibility >= RELATIONAL.flexibility;
    }


    public boolean capableOfNotBetween() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfNotEqual() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfNotIn() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfNotLike() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfNotNull() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfNull() {
        return flexibility >= COLUMN.flexibility;
    }

    public boolean capableOfOr() {
        return flexibility >= DOCUMENT.flexibility;
    }

    public boolean capableOfParentheses() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfQueryWithoutWhere() {
        return flexibility >= DOCUMENT.flexibility;
    }

    public boolean capableOfRight() {
        return flexibility >= GRAPH.flexibility;
    }

    public boolean capableOfSingleSort() {
        return flexibility >= DOCUMENT.flexibility;
    }

    public boolean capableOfSortingNulls() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfSubtraction() {
        return flexibility >= RELATIONAL.flexibility;
    }

    public boolean capableOfUpper() {
        return flexibility >= GRAPH.flexibility;
    }

    public static DatabaseType valueOfIgnoreCase(String value) {
        return Arrays.stream(DatabaseType.values()).filter(type -> type.name().equalsIgnoreCase(value)).findAny().orElse(DatabaseType.OTHER);
    }

    public boolean isKeywordSupportAtOrBelow(DatabaseType benchmark) {
        return this.flexibility <= benchmark.flexibility;
    }
}
