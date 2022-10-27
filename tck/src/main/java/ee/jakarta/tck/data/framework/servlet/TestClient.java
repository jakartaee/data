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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This is a superclass that test classes can extend to act as a test client
 * to execute tests that are deployed on a Web Profile Server.
 * 
 * This is used for complex test situations where a custom servlet is required.
 */
public class TestClient {
    
    private static final String nl = System.lineSeparator();
    
    private static final Logger log = Logger.getLogger(TestClient.class.getCanonicalName());
    
    /**
     * Runs test against servlet at requestURL, and asserts a successful response.
     * 
     * @param requestURL - URL to request a connection to the test servlet
     */
    public void runTest(URL requestURL) {
        assertSuccessfulURLResponse(requestURL, null);
    }
    
    /**
     * Runs test against servlet at requestURL, and asserts a successful response.
     * Additionally, asserts that a certain string appears in response.
     * 
     * @param requestURL - URL to request a connection to the test servlet
     * @param expected - Assert that the expected string is in the response body
     */
    public void runTest(URL requestURL, String expected) {
        String response = assertSuccessfulURLResponse(requestURL, null);
        assertTrue(response.contains(expected), "The expected string [" + expected + "] was not found in the response: " + response);
    }
    
    /**
     * Runs test against servlet at requestURL.
     * Provide properties if you want them included in a POST request, otherwise pass in null.
     * Returns the response body for custom assertions.
     * 
     * @param requestURL - URL to request a connection to the test servlet
     * @param props - properties to accompany a POST request to servlet
     * @return - response body as a string
     */
    public String runTestWithResponse(URL requestURL, Properties props) {
        return assertSuccessfulURLResponse(requestURL, props);
    }
    

    //##### test runner ######
    private String assertSuccessfulURLResponse(URL url, Properties props) {        
        boolean withProps = props != null;
        boolean pass = false;
        
        log.info("Running test on servlet via URL: " + url.toString());

        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setConnectTimeout((int) Duration.ofSeconds(30).toMillis());
            
            if(withProps) {
                con.setRequestMethod("POST");
                try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())){
                    wr.writeBytes( toEncodedString(props) );
                }
                       
            } else {
                con.setRequestMethod("GET");
            }

            final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            final StringBuilder outputBuilder = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                outputBuilder.append(line).append(nl);

                if (line.contains(TestServlet.SUCCESS)) {
                    pass = true;
                }
            }

            assertTrue(con.getResponseCode() < 400, "Connection returned a response code that was greater than 400");
            assertTrue(pass, "Output did not contain successful message: " + TestServlet.SUCCESS);
        
            return outputBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Exception: " + e.getClass().getName() + " requesting URL=" + url.toString(), e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
    
    protected static String toEncodedString(Properties args) throws UnsupportedEncodingException {
        StringBuffer buf = new StringBuffer();
        Enumeration<?> names = args.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = args.getProperty(name);
            
            buf.append(URLEncoder.encode(name, StandardCharsets.UTF_8.name()))
                .append("=")
                .append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
            
            if (names.hasMoreElements())
                buf.append("&");
        }
        return buf.toString();
    }
}
