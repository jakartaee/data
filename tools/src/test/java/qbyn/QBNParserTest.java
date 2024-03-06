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

import ee.jakarta.tck.data.tools.qbyn.ParseUtils;
import ee.jakarta.tck.data.tools.qbyn.QueryByNameInfo;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import ee.jakarta.tck.data.tools.antlr.QBNLexer;
import ee.jakarta.tck.data.tools.antlr.QBNParser;
import ee.jakarta.tck.data.tools.antlr.QBNBaseListener;

import java.io.IOException;

public class QBNParserTest {
    // Some of these are not actual query by name examples even though they follow the pattern
    String actionExamples = """
        findByHexadecimalContainsAndIsControlNot
        findByDepartmentCountAndPriceBelow
        countByHexadecimalNotNull
        existsByThisCharacter
        findByDepartmentsContains
        findByDepartmentsEmpty
        findByFloorOfSquareRootNotAndIdLessThanOrderByBitsRequiredDesc
        findByFloorOfSquareRootOrderByIdAsc
        findByHexadecimalIgnoreCase
        findByHexadecimalIgnoreCaseBetweenAndHexadecimalNotIn
        findById
        findByIdBetween
        findByIdBetweenOrderByNumTypeAsc
        findByIdGreaterThanEqual
        findByIdIn
        findByIdLessThan
        findByIdLessThanEqual
        findByIdLessThanOrderByFloorOfSquareRootDesc
        findByIsControlTrueAndNumericValueBetween
        findByIsOddFalseAndIdBetween
        findByIsOddTrueAndIdLessThanEqualOrderByIdDesc
        findByNameLike
        findByNumTypeAndFloorOfSquareRootLessThanEqual
        findByNumTypeAndNumBitsRequiredLessThan
        findByNumTypeInOrderByIdAsc
        findByNumTypeNot
        findByNumTypeOrFloorOfSquareRoot
        findByNumericValue
        findByNumericValueBetween
        findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual
        findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith
        findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc
        findByPriceNotNullAndPriceLessThanEqual
        findByPriceNull
        findByProductNumLike
        """;

    /**
     * Test the parser using a local QBNBaseListener implementation
     * @throws IOException
     */
    @Test
    public void testQueryByNameExamples() throws IOException {
        String[] examples = actionExamples.split("\n");
        for (String example : examples) {
            System.out.println(example);
            CodePointCharStream input = CharStreams.fromString(example); // create a lexer that feeds off of input CharStream
            QBNLexer lexer = new QBNLexer(input); // create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer); // create a parser that feeds off the tokens buffer
            QBNParser parser = new QBNParser(tokens);
            QueryByNameInfo info = new QueryByNameInfo();
            parser.addParseListener(new QBNBaseListener() {
                private StringBuffer property = new StringBuffer();

                @Override
                public void exitPredicate(QBNParser.PredicateContext ctx) {
                    int count = ctx.condition().size();
                    for (int i = 0; i < count; i++) {
                        QBNParser.ConditionContext cctx = ctx.condition(i);
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
                public void exitSubject(QBNParser.SubjectContext ctx) {
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
                public void exitOrder_clause(QBNParser.Order_clauseContext ctx) {
                    int count = ctx.order_item().size();
                    if(ctx.property() != null) {
                        String property = ctx.property().getText();
                        info.addOrderBy(property, QueryByNameInfo.OrderBySortDirection.NONE);
                    }
                    for (int i = 0; i < count; i++) {
                        QBNParser.Order_itemContext octx = ctx.order_item(i);
                        String property = octx.property().getText();
                        QueryByNameInfo.OrderBySortDirection direction = octx.ASC() != null ? QueryByNameInfo.OrderBySortDirection.ASC : QueryByNameInfo.OrderBySortDirection.DESC;
                        info.addOrderBy(property, direction);
                    }
                }
            });
            ParseTree tree = parser.query_method();
            // print LISP-style tree for the
            System.out.println(tree.toStringTree(parser));
            // Print out the parsed QueryByNameInfo
            System.out.println(info);

        }
    }

    /**
     * Test the parser using the ParseUtils class
     */
    @Test
    public void testParseUtils() {
        String[] examples = actionExamples.split("\n");
        for (String example : examples) {
            System.out.println(example);
            QueryByNameInfo info = ParseUtils.parseQueryByName(example);
            System.out.println(info);
        }
    }
}
