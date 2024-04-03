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
package qbyn;

import ee.jakarta.tck.data.tools.annp.RepositoryInfo;
import ee.jakarta.tck.data.tools.qbyn.QueryByNameInfo;
import org.junit.jupiter.api.Test;
import org.stringtemplate.v4.ST;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ST4RepoGenTest {
    static String REPO_TEMPLATE = """
            import jakarta.annotation.Generated;
            import jakarta.data.repository.OrderBy;
            import jakarta.data.repository.Query;
            import jakarta.data.repository.Repository;
            import #repo.fqn#;

            @Repository(dataStore = "#repo.dataStore#")
            @Generated("ee.jakarta.tck.data.tools.annp.RespositoryProcessor")
            public interface #repo.name#$ extends #repo.name# {
                #repo.methods :{m |
                    @Override
                    @Query("#m.query#")
                    #m.orderBy :{o | @OrderBy(value="#o.property#", descending = #o.descending#)}#
                    public #m.returnType# #m.name# (#m.parameters: {p | #p#}; separator=", "#);
                    
                    }
            	#
            }
            """;
    @Test
    public void testSyntax() {
        List<String> methods = Arrays.asList("findByFloorOfSquareRootOrderByIdAsc", "findByHexadecimalIgnoreCase",
                "findById", "findByIdBetween", "findByHexadecimalIgnoreCaseBetweenAndHexadecimalNotIn");
        ST s = new ST( "<methods :{m | public X <m> ();\n}>");
        s.add("methods", methods);
        System.out.println(s.render());
    }
    @Test
    public void testRepoGen() {
        RepositoryInfo repo = new RepositoryInfo();
        repo.setFqn("org.acme.BookRepository");
        repo.setName("BookRepository");
        repo.setDataStore("book");

        RepositoryInfo.MethodInfo findByTitleLike = new RepositoryInfo.MethodInfo("findByTitleLike", "List<Book>", "from Book where title like :title", null);
        findByTitleLike.addParameter("String title");
        repo.addMethod(findByTitleLike);
        RepositoryInfo.MethodInfo findByNumericValue = new RepositoryInfo.MethodInfo("findByNumericValue", "Optional<AsciiCharacter>",
                "from AsciiCharacter where numericValue = :numericValue",
                        Collections.singletonList(new QueryByNameInfo.OrderBy("numericValue", QueryByNameInfo.OrderBySortDirection.ASC)));
        findByNumericValue.addParameter("int id");
        repo.addMethod(findByNumericValue);
        ST st = new ST(REPO_TEMPLATE, '#', '#');
        st.add("repo", repo);
        System.out.println(st.render());
    }
}
