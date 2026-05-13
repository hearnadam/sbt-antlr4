grammar Hello;

greet : 'hello' ID ;
ID    : [a-zA-Z]+ ;
WS    : [ \t\r\n]+ -> skip ;
