package com.kmaebashi.samplan.compiler;

enum UnaryExpressionKind {
    MINUS,
    INCREMENT,
    DECREMENT,
    CAST_INT_TO_REAL,
    CAST_REAL_TO_INT,
    CAST_BOOLEAN_TO_STRING,
    CAST_INT_TO_STRING,
    CAST_REAL_TO_STRING,
}
