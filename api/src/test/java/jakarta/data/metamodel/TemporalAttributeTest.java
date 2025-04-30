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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel;

import jakarta.data.expression.TemporalExpression;
import jakarta.data.metamodel.TemporalAttribute;
import jakarta.data.restrict.BasicRestriction;
import jakarta.data.restrict.Restriction;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
class TemporalAttributeTest {

    static class Audit {
        LocalDateTime createdAt;
        LocalTime updatedAt;
    }

    interface _Audit {
        String CREATED_AT = "createdAt";
        String UPDATED_AT = "updatedAt";

        TemporalAttribute<Audit, LocalDateTime> createdAt = TemporalAttribute.of(Audit.class, CREATED_AT, LocalDateTime.class);
        TemporalAttribute<Audit, LocalTime> updatedAt = TemporalAttribute.of(Audit.class, UPDATED_AT, LocalTime.class);
    }
    @Test
    @DisplayName("should create restriction for createdAt equal to localDateTime()")
    void shouldCreateEqualToCurrentDateTimeRestriction() {
        Restriction<Audit> restriction = _Audit.createdAt.equalTo(TemporalExpression.localDateTime());
        var basic = (BasicRestriction<?, ?>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic).isInstanceOf(BasicRestriction.class);
            soft.assertThat(basic.expression()).isEqualTo(_Audit.createdAt);
        });
    }

    @Test
    @DisplayName("should create restriction for updatedAt not equal to localTime()")
    void shouldCreateNotEqualToCurrentTimeRestriction() {
        Restriction<Audit> restriction = _Audit.updatedAt.notEqualTo(TemporalExpression.localTime());
        var basic = (BasicRestriction<?, ?>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic).isInstanceOf(BasicRestriction.class);
            soft.assertThat(basic.expression()).isEqualTo(_Audit.updatedAt);
        });
    }

    @Test
    @DisplayName("should create greaterThan restriction for createdAt using localDateTime()")
    void shouldCreateGreaterThanCurrentDateTimeRestriction() {
        Restriction<Audit> restriction = _Audit.createdAt.greaterThan(TemporalExpression.localDateTime());
        var basic = (BasicRestriction<?, ?>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic).isInstanceOf(BasicRestriction.class);
            soft.assertThat(basic.expression()).isEqualTo(_Audit.createdAt);
        });
    }

    @Test
    @DisplayName("should create lessThan restriction for updatedAt using localTime()")
    void shouldCreateLessThanCurrentTimeRestriction() {
        Restriction<Audit> restriction = _Audit.updatedAt.lessThan(TemporalExpression.localTime());
        var basic = (BasicRestriction<?, ?>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic).isInstanceOf(BasicRestriction.class);
            soft.assertThat(basic.expression()).isEqualTo(_Audit.updatedAt);
        });
    }
}