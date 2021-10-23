package com.kmaebashi.samplan.compiler;

class BooleanLiteral extends Expression {
    boolean value;

    BooleanLiteral(int lineNumber, boolean value) {
        super(lineNumber);
        this.value = value;
    }
}
