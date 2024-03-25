/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
// 4.6.1. BNF Grammar for Query Methods
grammar QBN;

@header {
// TBD
package ee.jakarta.tck.data.tools.antlr;
}

query_method : subject predicate order_clause? ;

subject : (action | find find_expression) ignored_text? BY ;

action : find | delete | update | count | exists  ;

find : 'find' ;
delete : 'delete' ;
update : 'update' ;
count : 'count' ;
exists : 'exists' ;

find_expression : FIRST INTEGER? ;

predicate : condition ( (AND | OR) condition )* ;

condition : property ignore_case? not? operator? ;
ignore_case : IGNORE_CASE ;
not : NOT ;

operator : CONTAINS | ENDSWITH | STARTSWITH | LESSTHAN| LESSTHANEQUAL | GREATERTHAN |
                GREATERTHANEQUAL | BETWEEN | EMPTY | LIKE | IN | NULL |
                 TRUE | FALSE ;
property : (IDENTIFIER | IDENTIFIER '_' property)+ ;

order_clause : ORDER_BY ( order_item )* ( order_item | property ) ;

order_item : property ( ASC | DESC ) ;

ignored_text : IDENTIFIER ;

// Lexer rules
FIRST : 'First' ;
BY : 'By' ;
CONTAINS : 'Contains' ;
ENDSWITH : 'EndsWith' ;
STARTSWITH : 'StartsWith' ;
LESSTHAN : 'LessThan' ;
LESSTHANEQUAL : 'LessThanEqual' ;
GREATERTHAN : 'GreaterThan' ;
GREATERTHANEQUAL : 'GreaterThanEqual' ;
BETWEEN : 'Between' ;
EMPTY : 'Empty' ;
LIKE : 'Like' ;
IN : 'In' ;
NULL : 'Null' ;
TRUE : 'True' ;
FALSE : 'False' ;
IGNORE_CASE : 'IgnoreCase' ;
NOT : 'Not' ;
ORDER_BY : 'OrderBy' ;
AND : 'And' ;
OR : 'Or' ;
ASC : 'Asc' ;
DESC : 'Desc' ;

IDENTIFIER : ([A-Z][a-z]+)+? ;
INTEGER : [0-9]+ ;
WS : [ \t\r\n]+ -> skip ;
