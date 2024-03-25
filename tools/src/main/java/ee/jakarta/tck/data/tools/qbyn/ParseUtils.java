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
package ee.jakarta.tck.data.tools.qbyn;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ee.jakarta.tck.data.tools.antlr.QBNLexer;
import ee.jakarta.tck.data.tools.antlr.QBNParser;
import ee.jakarta.tck.data.tools.antlr.QBNBaseListener;

/**
 * A utility class for parsing query by name method names using the Antlr4 generated parser
 */
public class ParseUtils {
    /**
     * Parse a query by name method name into a QueryByNameInfo object
     * @param queryByName the query by name method name
     * @return the parsed QueryByNameInfo object
     */
    public static QueryByNameInfo parseQueryByName(String queryByName) {
        CodePointCharStream input = CharStreams.fromString(queryByName);
        QBNLexer lexer = new QBNLexer(input); // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer); // create a parser that feeds off the tokens buffer
        QBNParser parser = new QBNParser(tokens);
        QueryByNameInfo info = new QueryByNameInfo();
        parser.addParseListener(new QBNBaseListener() {
            private StringBuffer property = new StringBuffer();

            @Override
            public void exitPredicate(ee.jakarta.tck.data.tools.antlr.QBNParser.PredicateContext ctx) {
                int count = ctx.condition().size();
                for (int i = 0; i < count; i++) {
                    ee.jakarta.tck.data.tools.antlr.QBNParser.ConditionContext cctx = ctx.condition(i);
                    String property = cctx.property().getText();
                    QueryByNameInfo.Operator operator = QueryByNameInfo.Operator.NONE;
                    if(cctx.operator() != null) {
                        operator = QueryByNameInfo.Operator.valueOf(cctx.operator().getText().toUpperCase());
                    }
                    boolean ignoreCase = cctx.ignore_case() != null;
                    boolean not = cctx.not() != null;
                    boolean and = false;
                    if(i > 0) {
                        // The AND/OR is only present if there is more than one condition
                        and = ctx.AND(i-1) != null;
                    }
                    // String property, Operator operator, boolean ignoreCase, boolean not, boolean and
                    info.addCondition(property, operator, ignoreCase, not, and);
                }
            }

            @Override
            public void exitSubject(ee.jakarta.tck.data.tools.antlr.QBNParser.SubjectContext ctx) {
                if(ctx.find() != null) {
                    System.out.println("find: " + ctx.find().getText());
                    System.out.println("find_expression.INTEGER: " + ctx.find_expression().INTEGER());
                    int findCount = 0;
                    if(ctx.find_expression().INTEGER() != null) {
                        findCount = Integer.parseInt(ctx.find_expression().INTEGER().getText());
                    }
                    info.setFindExpressionCount(findCount);
                } else {
                    QueryByNameInfo.Action action = QueryByNameInfo.Action.valueOf(ctx.action().getText().toUpperCase());
                    info.setAction(action);
                }
                if(ctx.ignored_text() != null) {
                    info.setIgnoredText(ctx.ignored_text().getText());
                }
            }
            @Override
            public void exitOrder_clause(ee.jakarta.tck.data.tools.antlr.QBNParser.Order_clauseContext ctx) {
                int count = ctx.order_item().size();
                if(ctx.property() != null) {
                    String property = ctx.property().getText();
                    info.addOrderBy(property, QueryByNameInfo.OrderBySortDirection.NONE);
                }
                for (int i = 0; i < count; i++) {
                    ee.jakarta.tck.data.tools.antlr.QBNParser.Order_itemContext octx = ctx.order_item(i);
                    String property = octx.property().getText();
                    QueryByNameInfo.OrderBySortDirection direction = octx.ASC() != null ? QueryByNameInfo.OrderBySortDirection.ASC : QueryByNameInfo.OrderBySortDirection.DESC;
                    info.addOrderBy(property, direction);
                }
            }
        });
        // Run the parser
        ParseTree tree = parser.query_method();
        return info;
    }
}
