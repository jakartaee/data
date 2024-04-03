/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package ee.jakarta.tck.data.tools.annp;

import ee.jakarta.tck.data.tools.qbyn.ParseUtils;
import ee.jakarta.tck.data.tools.qbyn.QueryByNameInfo;
import ee.jakarta.tck.data.tools.qbyn.QueryByNameInfo.OrderBy;
import jakarta.data.repository.Repository;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;

public class RepositoryInfo {
    public static class MethodInfo {
        String name;
        String returnType;
        String query;
        List<OrderBy> orderBy;
        List<String> parameters = new ArrayList<>();
        List<String> exceptions = new ArrayList<>();

        public MethodInfo(String name, String returnType, String query, List<OrderBy> orderBy) {
            this.name = name;
            this.returnType = returnType;
            this.query = query;
            this.orderBy = orderBy;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
        public List<String> getParameters() {
            return parameters;
        }
        public void addParameter(String p) {
            parameters.add(p);
        }
        public List<OrderBy> getOrderBy() {
            return orderBy;
        }
    }
    private Element repositoryElement;
    private String fqn;
    private String name;
    private String dataStore = "";
    private ArrayList<MethodInfo> methods = new ArrayList<>();
    public ArrayList<ExecutableElement> qbnMethods = new ArrayList<>();

    public RepositoryInfo() {
    }
    public RepositoryInfo(Element repositoryElement) {
        this.repositoryElement = repositoryElement;
        Repository ann = repositoryElement.getAnnotation(Repository.class);
        setFqn(AnnProcUtils.getFullyQualifiedName(repositoryElement));
        setName(repositoryElement.getSimpleName().toString());
        setDataStore(ann.dataStore());
    }

    public Element getRepositoryElement() {
        return repositoryElement;
    }
    public String getFqn() {
        return fqn;
    }

    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataStore() {
        return dataStore;
    }

    public void setDataStore(String dataStore) {
        this.dataStore = dataStore;
    }


    public void addQBNMethod(ExecutableElement m, QueryByNameInfo info) {
        qbnMethods.add(m);
        String query = ParseUtils.toQuery(info);
        StringBuilder orderBy = new StringBuilder();
        MethodInfo mi = new MethodInfo(m.getSimpleName().toString(), m.getReturnType().toString(), query, info.getOrderBy());
        for (VariableElement p : m.getParameters()) {
            mi.addParameter(p.asType().toString() + " " + p.getSimpleName());
        }
        addMethod(mi);
    }
    public List<ExecutableElement> getQBNMethods() {
        return qbnMethods;
    }
    public boolean hasQBNMethods() {
        return !qbnMethods.isEmpty();
    }

    public ArrayList<MethodInfo> getMethods() {
        return methods;
    }

    public void addMethod(MethodInfo m) {
        methods.add(m);
    }
}
