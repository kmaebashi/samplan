package com.kmaebashi.samplan.compiler;

class IdentifierExpression extends Expression {
    String name;

    IdentifierExpression(int lineNumber, String name) {
        super(lineNumber);
        this.name = name;
    }
}
