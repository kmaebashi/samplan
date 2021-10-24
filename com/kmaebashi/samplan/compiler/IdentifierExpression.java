package com.kmaebashi.samplan.compiler;

class IdentifierExpression extends Expression {
    String name;
    VariableDeclaration declaration;

    IdentifierExpression(int lineNumber, String name) {
        super(lineNumber);
        this.name = name;
    }
}
