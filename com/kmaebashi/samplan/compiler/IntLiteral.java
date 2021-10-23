package com.kmaebashi.samplan.compiler;

class IntLiteral extends Expression {
    int value;

    IntLiteral(int lineNumber, int value) {
        super(lineNumber);
        this.value = value;
    }
}
