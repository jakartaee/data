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
package ee.jakarta.tck.data.web.annotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Web;
import ee.jakarta.tck.data.framework.security.TestSecurityContext;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;

/**
 * Tests that security annotations on repository interface methods
 * are enforced by the repository implementation.
 *
 * <p>TCK runners must provide a CDI bean implementing
 * {@link TestSecurityContext}.</p>
 */
@Web
@AnyEntity
public class AnnotationsSecurityTests {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(SecuredProduct.class,
                            SecuredProducts.class);
    }

    @Inject
    SecuredProducts products;

    @Inject
    TestSecurityContext securityContext;

    @AfterEach
    public void clearSecurityContext() {
        securityContext.clearCallerRoles();
    }

    @Assertion(id = "1293", strategy = """
            Verify that a repository method annotated with @DenyAll
            always rejects access by throwing a SecurityException.
            """)
    public void testDenyAllRejectsAccess() {
        assertThrows(SecurityException.class,
                     () -> products.findByProductId("P001"));
    }

    @Assertion(id = "1293", strategy = """
            Verify that a repository method annotated with @PermitAll
            allows access regardless of whether any security roles are set.
            """)
    public void testPermitAllAllowsAccessWithoutRoles() {
        securityContext.clearCallerRoles();
        List<SecuredProduct> result = products.findAll();
        assertNotNull(result);
    }

    @Assertion(id = "1293", strategy = """
            Verify that a repository method annotated with @RolesAllowed
            rejects access when the caller does not have a matching role.
            """)
    public void testRolesAllowedRejectsWithoutMatchingRole() {
        securityContext.setCallerRoles("user");
        assertThrows(SecurityException.class,
                     () -> products.remove(
                             new SecuredProduct("P999", "any")));
    }

    @Assertion(id = "1293", strategy = """
            Verify that a repository method annotated with @RolesAllowed
            allows access when the caller has one of the required roles.
            """)
    public void testRolesAllowedAllowsWithMatchingRole() {
        securityContext.setCallerRoles("admin");
        assertDoesNotThrow(() -> products.remove(
                new SecuredProduct("P999", "any")));
    }
}
