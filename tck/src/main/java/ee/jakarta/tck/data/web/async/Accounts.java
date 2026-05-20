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

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Select;
import jakarta.enterprise.concurrent.Asynchronous;

/**
 * A repository with some methods marked Asynchronous.
 * Although this compiles against Jakarta Concurrency, the Asynchronous
 * annotation need not be available at run time, because the TCK will
 * detect the presence or absence of the Jakarta Concurrency API and
 * skip the tests when not present.
 */
@Repository
public interface Accounts extends BasicRepository<Account, Integer> {

    @Asynchronous
    @Insert
    void add(Account... accounts);

    @Asynchronous
    @Find
    @Select(_Account.BALANCE)
    CompletionStage<Optional<Float>> balance(int accountId);

}
