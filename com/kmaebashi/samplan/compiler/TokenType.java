package com.kmaebashi.samplan.compiler;

enum TokenType {
    VAR,
    BOOLEAN,
    INT,
    REAL,
    STRING,
    FUNCTION,
    TRUE,
    FALSE,
    IF,
    ELSIF,
    ELSE,
    WHILE,
    RETURN,
    IDENTIFIER,
    INT_VALUE,
    REAL_VALUE,
    STRING_VALUE,
    PLUS,
    MINUS,
    ASTERISK,
    SLASH,
    ADD,
    LEFT_BRACE,
    RIGHT_BRACE,
    LEFT_PAREN,
    RIGHT_PAREN,
    EQUAL,
    ASSIGNMENT,
    GREATER_THAN,
    GREATER_EQUAL,
    LESS_THAN,
    LESS_EQUAL,
    LOGICAL_AND,
    LOGICAL_OR,
    INCREMENT,
    DECLREMENT,
    COMMA,
    SEMICOLON,
    END_OF_FILE
}