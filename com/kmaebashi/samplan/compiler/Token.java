package com.kmaebashi.samplan.compiler;

class Token {
    TokenType type;
    String tokenString;
    int intValue;
    double realValue;
    String stringValue;
    int lineNumber;
    int column;

    Token(TokenType type, String tokenString, int lineNumber, int column) {
        this.type = type;
        this.tokenString = tokenString;
        this.lineNumber = lineNumber;
        this.column = column;
    }

    Token(TokenType type, String tokenString, int intValue, int lineNumber, int column) {
        this(type, tokenString, lineNumber, column);
        this.intValue = intValue;
    }

    Token(TokenType type, String tokenString, double realValue, int lineNumber, int column) {
        this(type, tokenString, lineNumber, column);
        this.realValue = realValue;
    }

    Token(TokenType type, String tokenString, String stringValue, int lineNumber, int column) {
        this(type, tokenString, lineNumber, column);
        this.stringValue = stringValue;
    }
}
