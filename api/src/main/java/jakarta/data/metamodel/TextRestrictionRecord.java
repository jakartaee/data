/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel;

import jakarta.data.metamodel.constraint.Between;
import jakarta.data.metamodel.constraint.Constraint;
import jakarta.data.metamodel.constraint.EqualTo;
import jakarta.data.metamodel.constraint.GreaterThan;
import jakarta.data.metamodel.constraint.GreaterThanOrEqual;
import jakarta.data.metamodel.constraint.LessThan;
import jakarta.data.metamodel.constraint.LessThanOrEqual;
import jakarta.data.metamodel.constraint.Like;
import jakarta.data.metamodel.constraint.NotBetween;
import jakarta.data.metamodel.constraint.NotEqualTo;
import jakarta.data.metamodel.constraint.NotLike;
import jakarta.data.metamodel.restrict.TextRestriction;

import java.util.Objects;

record TextRestrictionRecord<T>(String attribute, Constraint<String> constraint)
        implements TextRestriction<T> {

    TextRestrictionRecord {
        Objects.requireNonNull(attribute, "Attribute must not be null");
        Objects.requireNonNull(constraint, "Constraint must not be null");
    }

    @Override
    public TextRestriction<T> ignoreCase() {
        // TODO: Use a switch statement after we move up to Java 21 minimum
        Constraint<String> caseIgnoringConstraint;
        if (constraint instanceof Like) {
            caseIgnoringConstraint = ((Like) constraint).ignoreCase();
        } else if (constraint instanceof NotLike) {
            caseIgnoringConstraint = ((NotLike) constraint).ignoreCase();
        } else if (constraint instanceof EqualTo) {
            caseIgnoringConstraint = ((EqualTo<String>) constraint).ignoreCase();
        } else if (constraint instanceof NotEqualTo) {
            caseIgnoringConstraint = ((NotEqualTo<String>) constraint).ignoreCase();
        } else if (constraint instanceof Between) {
            caseIgnoringConstraint = ((Between<String>) constraint).ignoreCase();
        } else if (constraint instanceof NotBetween) {
            caseIgnoringConstraint = ((NotBetween<String>) constraint).ignoreCase();
        } else if (constraint instanceof GreaterThan) {
            caseIgnoringConstraint = ((GreaterThan<String>) constraint).ignoreCase();
        } else if (constraint instanceof GreaterThanOrEqual) {
            caseIgnoringConstraint = ((GreaterThanOrEqual<String>) constraint).ignoreCase();
        } else if (constraint instanceof LessThan) {
            caseIgnoringConstraint = ((LessThan<String>) constraint).ignoreCase();
        } else if (constraint instanceof LessThanOrEqual) {
            caseIgnoringConstraint = ((LessThanOrEqual<String>) constraint).ignoreCase();
        } else {
            throw new UnsupportedOperationException(
                    "Cannot ignore case of a " +
                    constraint.getClass().getInterfaces()[0].getName() + " constraint");
        }

        return new TextRestrictionRecord<>(attribute, caseIgnoringConstraint);
    }

    @Override
    public TextRestriction<T> negate() {
        return new TextRestrictionRecord<>(attribute, constraint.negate());
    }

    /**
     * Textual representation of a text restriction.
     * For example,
     * <pre>title LIKE '%JAKARTA EE%'</pre>
     *
     * @return textual representation of a text restriction.
     */
    @Override
    public String toString() {
        return attribute + ' ' + constraint;
    }
}
