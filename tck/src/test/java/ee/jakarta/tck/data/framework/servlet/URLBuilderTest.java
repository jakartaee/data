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
package ee.jakarta.tck.data.framework.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

public class URLBuilderTest {
    
    @Test
    public void testAppendPath() throws MalformedURLException {
        URL baseURL = new URL("http://localhost:80/");
        
        URL expectedURL = new URL("http://localhost:80/my/custom/url");
        URL actualURL = URLBuilder.fromURL(baseURL).withPath("my").withPath("custom").withPath("url").build();
        assertEquals(expectedURL, actualURL);
        
        actualURL = URLBuilder.fromURL(baseURL).withPath("my", "custom", "url").build();
        assertEquals(expectedURL, actualURL);
        
        actualURL = URLBuilder.fromURL(baseURL).withPath("my").withPath("custom", "url").build();
        assertEquals(expectedURL, actualURL);
        
        baseURL = new URL("http://localhost:80/my/");
        actualURL = URLBuilder.fromURL(baseURL).withPath("custom").withPath("url").build();
        assertEquals(expectedURL, actualURL);
        
        actualURL = URLBuilder.fromURL(baseURL).withPath("custom", "url").build();
        assertEquals(expectedURL, actualURL);
    }
    
    @Test
    public void testAppendQueries() throws MalformedURLException {
        URL baseURL = new URL("http://localhost:80/myServlet/");
        
        URL expectedURL = new URL("http://localhost:80/myServlet?myKey=myValue");
        URL actualURL = URLBuilder.fromURL(baseURL).withQuery("myKey", "myValue").build();
        assertEquals(expectedURL, actualURL);
        
        expectedURL = new URL("http://localhost:80/myServlet?myKey=myValue&myKey2=myValue2");
        actualURL = URLBuilder.fromURL(baseURL).withQuery("myKey", "myValue").withQuery("myKey2", "myValue2").build();
        assertEquals(expectedURL, actualURL);
        
        baseURL = new URL("http://localhost:80/myServlet?myKey=myValue");
        actualURL = URLBuilder.fromURL(baseURL).withQuery("myKey2", "myValue2").build();
        assertEquals(expectedURL, actualURL);
        
        expectedURL = new URL("http://localhost:80/myServlet?myKey=myValue");
        actualURL = URLBuilder.fromURL(baseURL).withQuery("myKey", "myValue").build();
        assertEquals(expectedURL, actualURL);
    }
    
    @Test
    public void testAppendComplexQueryAndPath() throws MalformedURLException {
        URL baseURL = new URL("http://localhost:80/myServlet?myKey=myValue");
        
        URL expectedURL = new URL("http://localhost:80/myServlet/with/custom/path?myKey=myValue&anotherKey=anotherValue");
        URL actualURL = URLBuilder.fromURL(baseURL).withPath("with", "custom", "path").withQuery("anotherKey", "anotherValue").build();
        assertEquals(expectedURL, actualURL);
    }

}
