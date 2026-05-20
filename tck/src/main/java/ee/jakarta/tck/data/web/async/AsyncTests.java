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
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
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

        CompletableFuture<Float> futureSum =
                accounts.balance(105)
                        .thenApply(b -> b.orElse(0.0f))
                        .thenCombine(accounts.balance(107)
                                             .thenApply(b -> b.orElse(0.0f)),
                                     Float::sum)
                        .toCompletableFuture();

        assertEquals(133.98f, // 55.99 + 77.99
                     futureSum.get(TIMEOUT_SECONDS, TimeUnit.SECONDS),
                     0.01f);

        accounts.deleteAll(testData);

        TestPropertyUtility.waitForEventualConsistency();
    }

    @Assertion(id = "19", strategy = """
            Tests an asynchronous repository method that performs
            an Insert operation.
            """)
    public void testAsynchronousInsert() throws InterruptedException {
        try {
            Class.forName("jakarta.enterprise.concurrent.Asynchronous");
        } catch (ClassNotFoundException x) {
            return; // Jakarta Concurrency API is not present
        }

        assertEquals(false, accounts.findById(100).isPresent());
        assertEquals(false, accounts.findById(101).isPresent());
        assertEquals(false, accounts.findById(102).isPresent());

        accounts.add(
                Account.of(101, true, 10.99f,
                           LocalDateTime.of(2026, 5, 19, 16, 25, 10),
                           "asyncUser101@eclipse.org"),
                Account.of(102, true, 20.99f,
                           LocalDateTime.of(2026, 5, 19, 16, 22, 20),
                           "asyncUser102@eclipse.org"),
                Account.of(103, true, 13.99f,
                           LocalDateTime.of(2026, 5, 19, 16, 23, 30),
                           "asyncUser103@eclipse.org"));

        TestPropertyUtility.waitForEventualConsistency();

        Set<Integer> idsFound = new TreeSet<>();
        for (long startNS = System.nanoTime();
             idsFound.size() < 3 && TIMEOUT_SECONDS >
                 TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startNS);
             TimeUnit.SECONDS.sleep(1)) {
            if (!idsFound.contains(101)) {
                accounts.findById(101).ifPresent(account -> {
                    assertEquals(101,
                                 account.accountId);
                    assertEquals(true,
                                 account.active);
                    assertEquals(10.99f,
                                 account.balance,
                                 0.001f);
                    assertEquals(LocalDateTime.of(2026, 5, 19, 16, 25, 10),
                                 account.created);
                    assertEquals("asyncUser101@eclipse.org",
                                 account.email);
                    idsFound.add(101);
                });
            }
            if (!idsFound.contains(102)) {
                accounts.findById(102).ifPresent(account -> {
                    assertEquals(102,
                                 account.accountId);
                    assertEquals(true,
                                 account.active);
                    assertEquals(20.99f,
                                 account.balance,
                                 0.001f);
                    assertEquals(LocalDateTime.of(2026, 5, 19, 16, 22, 20),
                                 account.created);
                    assertEquals("asyncUser102@eclipse.org",
                                 account.email);
                    idsFound.add(102);
                });
            }
            if (!idsFound.contains(103)) {
                accounts.findById(103).ifPresent(account -> {
                    assertEquals(103,
                                 account.accountId);
                    assertEquals(true,
                                 account.active);
                    assertEquals(13.99f,
                                 account.balance,
                                 0.001f);
                    assertEquals(LocalDateTime.of(2026, 5, 19, 16, 23, 30),
                                 account.created);
                    assertEquals("asyncUser103@eclipse.org",
                                 account.email);
                    idsFound.add(103);
                });
            }
        }

        assertEquals(Set.of(101, 102, 103),
                     idsFound);

        accounts.deleteById(101);
        accounts.deleteById(102);
        accounts.deleteById(103);

        TestPropertyUtility.waitForEventualConsistency();
    }

}