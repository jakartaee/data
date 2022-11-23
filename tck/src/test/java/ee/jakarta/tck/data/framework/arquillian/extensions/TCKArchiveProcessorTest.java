/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.framework.arquillian.extensions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.jupiter.api.Test;

import ee.jakarta.tck.data.framework.junit.anno.Core;
import ee.jakarta.tck.data.framework.junit.anno.Full;
import ee.jakarta.tck.data.framework.junit.anno.Signature;
import ee.jakarta.tck.data.framework.junit.anno.Web;

/**
 * Tests to ensure we correctly append framework classes to archives that are deployed using Arquillian
 */
public class TCKArchiveProcessorTest {
    private static final TCKArchiveProcessor processor = new TCKArchiveProcessor();
    
    @Core
    private static class TestCoreClass {}
    
    @Test
    public void processorShouldAlwaysAppendAnnotationsAndExtensions() {
        JavaArchive testJar = ShrinkWrap.create(JavaArchive.class);
        assertTrue(testJar.getContent().isEmpty());
        
        processor.process(testJar, new TestClass(TestCoreClass.class));
        assertFalse(testJar.getContent().isEmpty());
        
        // Should append Anno and Extension packages
        assertTrue(testJar.getContent().containsKey(new BasicPath("/ee/jakarta/tck/data/framework/junit/anno")));    
        assertTrue(testJar.getContent().containsKey(new BasicPath("/ee/jakarta/tck/data/framework/junit/extensions")));
        
        //Should not append servlet or signature packages
        assertFalse(testJar.getContent().containsKey(new BasicPath("/ee/jakarta/tck/data/framework/servlet")));
        assertFalse(testJar.getContent().containsKey(new BasicPath("/ee/jakarta/tck/data/framework/signature")));
    }
    
    @Web
    private static class TestWebClass {}
    
    @Test
    public void processorShouldAppendServletFrameworkForWebArchives() {
        WebArchive testWAR = ShrinkWrap.create(WebArchive.class);
        assertTrue(testWAR.getContent().isEmpty());
        
        processor.process(testWAR, new TestClass(TestWebClass.class));
        assertFalse(testWAR.getContent().isEmpty());
        
        // Should append Anno, Extension, and Servlet packages
        assertTrue(testWAR.getContent().containsKey(new BasicPath("/WEB-INF/classes/ee/jakarta/tck/data/framework/junit/anno")));      
        assertTrue(testWAR.getContent().containsKey(new BasicPath("/WEB-INF/classes/ee/jakarta/tck/data/framework/junit/extensions")));      
        assertTrue(testWAR.getContent().containsKey(new BasicPath("/WEB-INF/classes/ee/jakarta/tck/data/framework/servlet")));    
        
        // Should not append signature packages
        assertFalse(testWAR.getContent().containsKey(new BasicPath("/WEB-INF/classes/ee/jakarta/tck/data/framework/signature")));
    }
    
    @Signature
    @Full
    private static class TestSignatureClass {}
    
    @Test
    public void processorShouldAppendSignatureFrameworkForSignatureArchvies() {
        WebArchive testWAR = ShrinkWrap.create(WebArchive.class);
        assertTrue(testWAR.getContent().isEmpty());
        
        processor.process(testWAR, new TestClass(TestSignatureClass.class));
        assertFalse(testWAR.getContent().isEmpty());
        
        // Should append Anno, Extension, and Servlet packages
        assertTrue(testWAR.getContent().containsKey(new BasicPath("/WEB-INF/classes/ee/jakarta/tck/data/framework/junit/anno")));      
        assertTrue(testWAR.getContent().containsKey(new BasicPath("/WEB-INF/classes/ee/jakarta/tck/data/framework/junit/extensions")));      
        assertTrue(testWAR.getContent().containsKey(new BasicPath("/WEB-INF/classes/ee/jakarta/tck/data/framework/servlet"))); 
        assertTrue(testWAR.getContent().containsKey(new BasicPath("/WEB-INF/classes/ee/jakarta/tck/data/framework/signature")));
    }
    
}
