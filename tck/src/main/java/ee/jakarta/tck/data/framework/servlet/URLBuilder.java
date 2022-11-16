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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A utility builder class for generating a request URL to a servlet with custom
 * paths and queries.
 */
public class URLBuilder {
    
    private static final Logger log = Logger.getLogger(URLBuilder.class.getCanonicalName());

    private URL baseURL;
    private List<String> paths;
    private Map<String, String> queries;

    /*
     * Private constructor - builder class
     */
    private URLBuilder(URL baseURL, List<String> paths, Map<String, String> queries) {
        this.baseURL = baseURL;
        this.paths = paths;
        this.queries = queries;
    }

    /**
     * Start with a base URL that contains connection data. i.e. protocol, hostname,
     * and port number
     * 
     * Example:
     * 
     * <pre>
     *  &#64;ArquillianResource
     *  URL baseURL;
     *  
     *  &#64;Test
     *  public void testSuccessAndFailure(TestInfo testInfo) {
     *     URL requestURL = URLBuilder.fromURL(baseURL).build()
     *   }
     * </pre>
     * 
     * @param url - the base URL
     * @return an instance of URLBuilder
     */
    public static URLBuilder fromURL(URL url) {
        List<String> paths = null;
        Map<String, String> queries = null;

        String strPaths = url.getPath();
        if (strPaths != null && strPaths != "") {
            paths = new ArrayList<>();
            for (String path : strPaths.split("/")) {
                paths.add(path);
            }
        }

        String strQueries = url.getQuery();
        if (strQueries != null && strQueries != "") {
            queries = new HashMap<>();
            for (String query : strQueries.split("&")) {
                int equalIndex = query.indexOf("=");
                queries.put(query.substring(0, equalIndex), query.substring(equalIndex, query.length()));
            }
        }

        URL baseURL;
        try {
            baseURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to create a baseURL", e);
        }

        return new URLBuilder(baseURL, paths, queries);
    }

    /**
     * Append to the URL's path.
     * 
     * Example:
     * <pre>
     * URLBuilder.fromURL(new URL(http://localhost:80/servlet/)).withPath("endpoint1").build
     * 
     * returns: http://localhost:80/servlet/endpoint1/
     * </pre>
     * @param path - The path to append
     * @return This URL builder object
     */
    public URLBuilder withPath(String path) {
        if (this.paths == null) {
            this.paths = new ArrayList<>(Arrays.asList(path));
        } else {
            this.paths.add(path);
        }
        return this;
    }

    /**
     * Append to the URL's query.
     * 
     * Example:
     * <pre>
     * URLBuilder.fromURL(new URL(http://localhost:80/servlet?firstName=kyle)).withQuery("lastName", "aure").build
     * 
     * returns: http://localhost:80/servlet?firstName=kyle&#38;lastName=aure
     * </pre>
     * 
     * @param key - the key part of the query
     * @param value - the value part of the query
     * @return This URL builder object
     */
    public URLBuilder withQuery(String key, String value) {
        if (this.queries == null) {
            this.queries = new HashMap<>(Collections.singletonMap(key, value));
        } else {
            this.queries.put(key, value);
        }
        return this;
    }

    /**
     * Builds the URL and returns it.
     * 
     * @return - the URL with all appended paths and queries
     */
    public URL build() {

        URL extendedURL = baseURL;

        extendedURL = extendQuery(extendedURL, queries);
        extendedURL = extendPath(extendedURL, paths);
        
        log.info("Built extended URL: " + extendedURL.toString());

        return extendedURL;
    }

    private static URL extendQuery(URL baseURL, Map<String, String> queries) {
        if (queries == null)
            return baseURL;

        // Get existing query part
        boolean existingQuery = baseURL.getQuery() != null;
        String extendedQuery = existingQuery ? "?" + baseURL.getQuery() : "?";

        // Append additional query parts
        for (Map.Entry<String, String> entry : queries.entrySet()) {
            extendedQuery += entry.getKey() + "=" + entry.getValue() + "&";
        }

        // Cleanup trailing symbol(s)
        extendedQuery = extendedQuery.substring(0, extendedQuery.length() - 1);

        // Generate and return new URL
        try {
            return new URL(baseURL.getProtocol(), baseURL.getHost(), baseURL.getPort(),
                    baseURL.getPath() + extendedQuery, null);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private URL extendPath(URL baseURL, List<String> paths) {
        if (paths == null)
            return baseURL;

        // Get existing path part
        boolean existingPath = baseURL.getPath() != null;
        String extendedPath = existingPath ? baseURL.getPath() : "";

        // Append additional path parts
        for (String pathPart : paths) {
            pathPart = pathPart.replace("/", ""); // Remove existing /
            extendedPath += pathPart + "/";
        }

        // cleanup trailing symbol(s)
        extendedPath = extendedPath.substring(0, extendedPath.length() - 1);

        // Generate and return new URL
        try {
            return new URL(baseURL.getProtocol(), baseURL.getHost(), baseURL.getPort(),
                    extendedPath + (baseURL.getQuery() == null ? "" : "?" + baseURL.getQuery()), null);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
