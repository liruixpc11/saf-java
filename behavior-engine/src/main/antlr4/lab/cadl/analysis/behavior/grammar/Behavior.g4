grammar Behavior;


// model output
modelOutput: modelId '(' ID (',' ID)* ')'
        | modelId;
modelId: ID;

// behavior expression

behavior: ID                                            # StateBehavior
        | '(' behavior ')' behaviorConstraint?          # ConstraintBehavior
        | NEGATION behavior                             # NegationBehavior
        | behavior LOGICAL_OP behavior                  # LogicalBehavior
        | behavior constrainnTimeOp behavior            # TimeBehavior
        | constraintAlwaysTimeOp behavior               # AlwaysBehavior
        ;

constrainnTimeOp: TIME_OP operationConstraint?;
constraintAlwaysTimeOp: ALWAYS_TIME_OP operationConstraint?;

LOGICAL_OP: 'and' | 'or' | 'xor';
ALWAYS_TIME_OP: '[]';
TIME_OP:  '~>' | '[]' | 'olap' | 'dur' | 'sw' | 'ew' | 'eq';

behaviorConstraint: '[' (timeConstraint | countConstraint) ']';
operationConstraint: '[' RELATIVE_OP time (':' time)? ']';

timeConstraint: TIME_POSITION RELATIVE_OP time (':' time)?;
time: INT TIME_UNIT;
TIME_POSITION: 'at' | 'duration' | 'end';

countConstraint: COUNT_TYPE RELATIVE_OP INT (':' INT)?;
COUNT_TYPE: 'icount' | 'bcount' | 'rate';

// state expression

constraintState: state stateConstraint?;

state: primeState
        | importState
        | importState2;

primeState: '{' attributeList? '}';
importState: importVariable '(' attributeList? ')'
        | importVariable '(' context ')'
        | importVariable '(' context ',' attributeList ')';
importState2: '{' importState '}';

context: '$' ID;

attributeList: attributePair (',' attributePair)*;

stateConstraint: '[' stateConstraintType RELATIVE_OP (RANGE | INT) ']';

attributePair: ID RELATIVE_OP value;

value: RANGE | STRING | FLOAT | INT | ID | variable | argument | WILDCARD;

// variable
importVariable: ID ('.' ID)?;
variable: '$' ID '.' ID;
argument: '$' INT;

// relative operation
RELATIVE_OP: '>=' | '<=' | '>' | '<' | '!=' | '=';
NEGATION: 'not';

TIME_UNIT: 'SECS' | 'MSECS' | 'secs' | 'msecs' | 'SEC' | 'MSEC' | 'sec' | 'msec' | 's' | 'ms' | 'S' | 'MS';

// constraint key
stateConstraintType: '_eventno' | '_limit' | COUNT_TYPE;
// CONTRINT_KEY: 'duration' | '_eventno' | '_limit' | 'icount' | 'bcount' | 'rate' | 'end' | 'at';

// wildcard
WILDCARD: '_ANY_' | '*';

// string
STRING : '"' (ESC | .)*? '"'
        | '\'' (ESC | .)*? '\'';
fragment ESC : '\\' [btnr"\\];

// numbers
FLOAT: INT '.'
        | INT '.' INT
        | '.' INT
        ;
RANGE : INT ':' INT;
INT: DIGIT+;
fragment ID_LETER : 'a'..'z' | 'A'..'Z' | '_';
fragment DIGIT : '0'..'9';

// id
// NOTE can begin weith digit!!
ID : (ID_LETER | DIGIT) (ID_LETER | DIGIT)*;

WS : [ \t\r\n]+ -> skip;