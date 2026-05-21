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

@jakarta.nosql.Entity
@jakarta.persistence.Entity
public class Account {

    @jakarta.nosql.Id
    @jakarta.persistence.Id
    int accountId;

    @jakarta.nosql.Column
    @jakarta.persistence.Column(nullable = false)
    boolean active;

    @jakarta.nosql.Column
    @jakarta.persistence.Column(nullable = false)
    float balance;

    @jakarta.nosql.Column
    @jakarta.persistence.Column(nullable = false)
    LocalDateTime created;

    @jakarta.nosql.Column
    String email;

    public static Account of(int accountId,
                             boolean active,
                             float balance,
                             LocalDateTime created,
                             String email) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setActive(active);
        account.setBalance(balance);
        account.setCreatedOn(created);
        account.setEmail(email);
        return account;
    }

    public int getAccountId() {
        return accountId;
    }

    public float getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedOn() {
        return created;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public void setAccountId(int value) {
        accountId = value;
    }

    public void setActive(boolean value) {
        active = value;
    }

    public void setBalance(float value) {
        balance = value;
    }

    public void setCreatedOn(LocalDateTime value) {
        created = value;
    }

    public void setEmail(String value) {
        email = value;
    }

    @Override
    public String toString() {
        return "Account#" + accountId +
               (active ? " active " : "") +
               " for " + email +
               " owes $" + balance +
               " created " + created;
    }

}
