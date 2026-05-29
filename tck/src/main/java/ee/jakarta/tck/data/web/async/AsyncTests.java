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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Web;
import ee.jakarta.tck.data.framework.utilities.TestPropertyUtility;
import jakarta.inject.Inject;

@Web
@AnyEntity
public class AsyncTests {
    /**
     * Maximum amount of time to wait for asynchronous operations to complete.
     */
    static final long TIMEOUT_SECONDS = TimeUnit.MINUTES.toSeconds(2);

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(_Account.class, Account.class, Accounts.class);
    }

    @Inject
    Accounts accounts;

    @Assertion(id = "19", strategy = """
            Tests an asynchronous repository method that performs
            a Find operation.
            """)
    public void testAsynchronousFind() throws Exception {
        try {
            Class.forName("jakarta.enterprise.concurrent.Asynchronous");
        } catch (ClassNotFoundException x) {
            return; // Jakarta Concurrency API is not present
        }

        List<Account> testData = List.of(
                Account.of(104, false, 4.99f,
                           LocalDateTime.of(2026, 5, 19, 16, 25, 40),
                           "asyncUser101@eclipse.org"),
                Account.of(105, true, 55.99f,
                           LocalDateTime.of(2026, 5, 20, 10, 24, 50),
                           "asyncUser105@eclipse.org"),
                Account.of(106, true, 26.99f,
                           LocalDateTime.of(2026, 5, 20, 10, 23, 00),
                           "asyncUser106@eclipse.org"),
                Account.of(107, true, 77.99f,
                           LocalDateTime.of(2026, 7, 27, 17, 27, 10),
                           "asyncUser107@eclipse.org"));
        accounts.saveAll(testData);

        TestPropertyUtility.waitForEventualConsistency();

        CompletionStage<Optional<Float>> futureBalance = null;
        try {
            futureBalance = accounts.balance(105);
        } catch (UnsupportedOperationException x) {
            // Data provider is not capable of CompletionStage return type
        }

        if (futureBalance != null) {
            CompletableFuture<Float> futureSum =
                    futureBalance
                            .thenApply(b -> b.orElse(0.0f))
                            .thenCombine(accounts.balance(107)
                                                 .thenApply(b -> b.orElse(0.0f)),
                                         Float::sum)
                            .toCompletableFuture();

            assertEquals(133.98f, // 55.99 + 77.99
                         futureSum.get(TIMEOUT_SECONDS, TimeUnit.SECONDS),
                         0.01f);
        }

        accounts.deleteAll(testData);

        TestPropertyUtility.waitForEventualConsistency();
    }

    @Assertion(id = "19", strategy = """
            Tests an asynchronous repository method that performs
            an Insert operation.
            """)
    public void testAsynchronousInsert() throws Exception {
        try {
            Class.forName("jakarta.enterprise.concurrent.Asynchronous");
        } catch (ClassNotFoundException x) {
            return; // Jakarta Concurrency API is not present
        }

        assertEquals(false, accounts.findById(100).isPresent());
        assertEquals(false, accounts.findById(101).isPresent());
        assertEquals(false, accounts.findById(102).isPresent());

        CompletionStage<Void> stage;
        try {
            stage = accounts.add(
                 Account.of(101, true, 10.99f,
                            LocalDateTime.of(2026, 5, 19, 16, 25, 10),
                            "asyncUser101@eclipse.org"),
                 Account.of(102, true, 20.99f,
                            LocalDateTime.of(2026, 5, 19, 16, 22, 20),
                            "asyncUser102@eclipse.org"),
                 Account.of(103, true, 13.99f,
                            LocalDateTime.of(2026, 5, 19, 16, 23, 30),
                            "asyncUser103@eclipse.org"));
        } catch (UnsupportedOperationException x) {
            // Data provider is not capable of CompletionStage return type
            return;
        }

        stage.toCompletableFuture().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        TestPropertyUtility.waitForEventualConsistency();

        Account account1 = accounts.findById(101).orElseThrow();
        assertEquals(101,
                     account1.accountId);
        assertEquals(true,
                     account1.active);
        assertEquals(10.99f,
                     account1.balance,
                     0.001f);
        assertEquals(LocalDateTime.of(2026, 5, 19, 16, 25, 10),
                     account1.created);
        assertEquals("asyncUser101@eclipse.org",
                     account1.email);

        Account account2 = accounts.findById(102).orElseThrow();
        assertEquals(102,
                     account2.accountId);
        assertEquals(true,
                     account2.active);
        assertEquals(20.99f,
                     account2.balance,
                     0.001f);
        assertEquals(LocalDateTime.of(2026, 5, 19, 16, 22, 20),
                     account2.created);
        assertEquals("asyncUser102@eclipse.org",
                     account2.email);

        Account account3 = accounts.findById(103).orElseThrow();
        assertEquals(103,
                     account3.accountId);
        assertEquals(true,
                     account3.active);
        assertEquals(13.99f,
                     account3.balance,
                     0.001f);
        assertEquals(LocalDateTime.of(2026, 5, 19, 16, 23, 30),
                     account3.created);
        assertEquals("asyncUser103@eclipse.org",
                     account3.email);

        accounts.delete(account1);
        accounts.delete(account2);
        accounts.delete(account3);

        TestPropertyUtility.waitForEventualConsistency();
    }

}