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

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
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
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(org.antlr.v4.runtime.Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, org.antlr.v4.runtime.RecognitionException e) {
                throw new IllegalArgumentException("Invalid query by name method name: " + queryByName);
            }
        });
        parser.addParseListener(new QBNBaseListener() {
            @Override
            public void visitErrorNode(ErrorNode node) {
                throw new IllegalArgumentException("Invalid query by name method name: " + queryByName);
            }


            @Override
            public void exitPredicate(ee.jakarta.tck.data.tools.antlr.QBNParser.PredicateContext ctx) {
                int count = ctx.condition().size();
                for (int i = 0; i < count; i++) {
                    ee.jakarta.tck.data.tools.antlr.QBNParser.ConditionContext cctx = ctx.condition(i);
                    String property = cctx.property().getText();
                    QueryByNameInfo.Operator operator = QueryByNameInfo.Operator.EQUAL;
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
                    String property = camelCase(ctx.property().getText());
                    info.addOrderBy(property, QueryByNameInfo.OrderBySortDirection.NONE);
                }
                for (int i = 0; i < count; i++) {
                    ee.jakarta.tck.data.tools.antlr.QBNParser.Order_itemContext octx = ctx.order_item(i);
                    String property = camelCase(octx.property().getText());
                    QueryByNameInfo.OrderBySortDirection direction = octx.ASC() != null ? QueryByNameInfo.OrderBySortDirection.ASC : QueryByNameInfo.OrderBySortDirection.DESC;
                    info.addOrderBy(property, direction);
                }
            }
        });
        // Run the parser
        ParseTree tree = parser.query_method();

        return info;
    }

    public static String camelCase(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    /**
     * Convert a QueryByNameInfo object into a JDQL query string
     * @param info - parse QBN info
     * @return toQuery(info, false)
     * @see #toQuery(QueryByNameInfo, boolean)
     */
    public static String toQuery(QueryByNameInfo info) {
        return toQuery(info, false);
    }
    /**
     * Convert a QueryByNameInfo object into a JDQL query string
     * @param info - parse QBN info
     * @param includeOrderBy - if the order by clause should be included in the query
     * @return the JDQL query string
     */
    public static String toQuery(QueryByNameInfo info, boolean includeOrderBy) {
        StringBuilder sb = new StringBuilder();
        int paramIdx = 1;
        QueryByNameInfo.Action action = info.getAction();
        switch (action) {
            case FIND:
                break;
            case DELETE:
                sb.append("delete ").append(info.getEntity()).append(' ');
                break;
            case UPDATE:
                sb.append("update ").append(info.getEntity()).append(' ');
                break;
            case COUNT:
                sb.append("select count(this) ");
                break;
            case EXISTS:
                sb.append("select count(this)>0 ");
                break;
        }
        //
        if(info.getPredicates().isEmpty()) {
            return sb.toString();
        }

        sb.append("where ");
        for(int n = 0; n < info.getPredicates().size(); n ++) {
            QueryByNameInfo.Condition c = info.getPredicates().get(n);

            // EndWith -> right(property, length(?1)) = ?1
            if(c.operator == QueryByNameInfo.Operator.ENDSWITH) {
                sb.append("right(").append(camelCase(c.property))
                        .append(", length(?")
                        .append(paramIdx)
                        .append(")) = ?")
                        .append(paramIdx)
                ;
                paramIdx ++;
            }
            // StartsWith -> left(property, length(?1)) = ?1
            else if(c.operator == QueryByNameInfo.Operator.STARTSWITH) {
                sb.append("left(").append(camelCase(c.property))
                        .append(", length(?")
                        .append(paramIdx)
                        .append(")) = ?")
                        .append(paramIdx)
                ;
                paramIdx ++;
            }
            // Contains -> property like '%'||?1||'%'
            else if(c.operator == QueryByNameInfo.Operator.CONTAINS) {
                sb.append(camelCase(c.property)).append(" like '%'||?").append(paramIdx).append("||'%'");
                paramIdx++;
            }
            // Null
            else if(c.operator == QueryByNameInfo.Operator.NULL) {
                if(c.not) {
                    sb.append(camelCase(c.property)).append(" is not null");
                } else {
                    sb.append(camelCase(c.property)).append(" is null");
                }
            }
            // Empty
            else if(c.operator == QueryByNameInfo.Operator.EMPTY) {
                if(c.not) {
                    sb.append(camelCase(c.property)).append(" is not empty");
                } else {
                    sb.append(camelCase(c.property)).append(" is empty");
                }
            }
            // Other operators
            else {
                boolean ignoreCase = c.ignoreCase;
                if(ignoreCase) {
                    sb.append("lower(");
                }
                sb.append(camelCase(c.property));
                if(ignoreCase) {
                    sb.append(")");
                }
                if (c.operator == QueryByNameInfo.Operator.EQUAL && c.not) {
                    sb.append(" <>");
                } else {
                    if(c.not) {
                        sb.append(" not");
                    }
                    String jdql = c.operator.getJDQL();
                    sb.append(jdql);
                }
                // Other operators that need a parameter, add a placeholder
                if (c.operator.parameters() > 0) {
                    if (ignoreCase) {
                        sb.append(" lower(?").append(paramIdx).append(")");
                    } else {
                        sb.append(" ?").append(paramIdx);
                    }
                    paramIdx++;
                    if (c.operator.parameters() == 2) {
                        if (ignoreCase) {
                            sb.append(" and lower(?").append(paramIdx).append(")");
                        } else {
                            sb.append(" and ?").append(paramIdx);
                        }
                        paramIdx++;
                    }
                }
            }
            // See if we need to add an AND or OR
            if(n < info.getPredicates().size()-1) {
                // The and/or comes from next condition
                boolean isAnd = info.getPredicates().get(n+1).and;
                if (isAnd) {
                    sb.append(" and ");
                } else {
                    sb.append(" or ");
                }
            }
        }

        // If there is an orderBy clause, add it to query
        if(includeOrderBy && !info.getOrderBy().isEmpty()) {
            for (QueryByNameInfo.OrderBy ob : info.getOrderBy()) {
                sb.append(" order by ").append(ob.property).append(' ');
                if(ob.direction != QueryByNameInfo.OrderBySortDirection.NONE) {
                    sb.append(ob.direction.name().toLowerCase());
                }
            }
        }

        return sb.toString();
    }
}
