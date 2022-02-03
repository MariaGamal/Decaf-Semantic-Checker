grammar Decaf;

// Lexer Rules:
WHITESPACE: [\t\r\n ]+ -> skip ;
COMMENTS: ('//' ~[\r\n]* | '/*'.*? '*/')-> skip;
ID: ALPHA ALPHA_NUM*;
INT_LITERAL: DECIMAL_LITERAL | HEX_LITERAL;
CHAR: [\u0020-\u0021\u0023-\u0026\u0028-\u005B\u005D-\u007E]|'\\'|'\\"'|'\\\''|'\\\\'|'\\n'|'\\t';
DIGIT: '0'..'9';
ALPHA: 'a'..'z'|'A'..'Z'|'_';
HEX: 'a'..'f'|'A'..'F';
HEX_DIGIT: DIGIT|HEX;
HEX_LITERAL: '0x' HEX_DIGIT+;
DECIMAL_LITERAL: DIGIT+ ;
CHAR_LITERAL: '\'' CHAR '\'';  // a...f
STRING_LITERAL: '"' CHAR* '"';
ALPHA_NUM: ALPHA | DIGIT;


// Parser Rules
program: 'class' 'Program' '{' field_decl* method_decl* '}';
line: (ID | ID '['INT_LITERAL']');
field_decl: type line(',' line)* ';' ;
method_decl: (type|'void') ID '('(type ID (',' type ID)*)?')' block;
block: '{' var_decl* statement* '}';
var_decl: type ID (',' ID)* ';';
type: 'int' | 'boolean';
statement: location assign_op expr ';'
         | method_call ';'
         | 'if' '(' expr ')' block ('else' block)?
         | 'for' ID '=' expr ',' expr block
         | 'return'expr? ';'
         | 'break' ';'
         | 'continue' ';'
         | block
         ;
assign_op: '='|'+='|'-=';
method_call: method_name '('(expr (',' expr)*)?')'
            | 'callout' '(' STRING_LITERAL (',' callout_arg)* ')'
            ;
method_name: ID;
location : ID | ID '['expr']';
expr: location
    | method_call
    | literal
    | expr bin_op expr
    | '-' expr
    | '!' expr
    | '(' expr ')'
    ;
callout_arg: expr | STRING_LITERAL;
bin_op: arith_op | rel_op | eq_op | cond_op;
arith_op: '+' | '-' | '*' | '/' | '%';
rel_op: '<' | '>' | '<=' | '>=';
eq_op: '==' | '!=';
cond_op: '&&' | '||';
literal: INT_LITERAL|CHAR_LITERAL|bool_literal;
bool_literal: 'true' | 'false';