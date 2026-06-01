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
package ee.jakarta.tck.data.web.async;

import java.time.LocalDateTime;

import jakarta.data.metamodel.BooleanAttribute;
import jakarta.data.metamodel.NumericAttribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.metamodel.TemporalAttribute;
import jakarta.data.metamodel.TextAttribute;

@StaticMetamodel(Account.class)
public interface _Account {
    String ACCOUNTID = "accountId";
    String ACTIVE = "active";
    String BALANCE = "balance";
    String CREATED = "created";
    String EMAIL = "email";

    NumericAttribute<Account, Integer> accountId =
            NumericAttribute.of(Account.class, ACCOUNTID, int.class);

    BooleanAttribute<Account> active =
            BooleanAttribute.of(Account.class, ACCOUNTID, boolean.class);

    NumericAttribute<Account, Float> balance =
            NumericAttribute.of(Account.class, BALANCE, float.class);

    TemporalAttribute<Account, LocalDateTime> created =
            TemporalAttribute.of(Account.class, CREATED, LocalDateTime.class);

    TextAttribute<Account> email =
            TextAttribute.of(Account.class, EMAIL);
}
