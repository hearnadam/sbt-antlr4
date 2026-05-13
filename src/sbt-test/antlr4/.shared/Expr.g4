grammar Expr;

expr : INT ('+' INT)* ;
INT  : [0-9]+ ;
WS   : [ \t\r\n]+ -> skip ;
