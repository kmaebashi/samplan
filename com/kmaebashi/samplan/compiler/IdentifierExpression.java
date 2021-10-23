package com.kmaebashi.samplan.compiler;

class IdentifierExpression extends Expression {
    String name;

    IdentifierExpression(String name) {
        this.name = name;
    }
}
