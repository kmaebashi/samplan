package com.kmaebashi.samplan.compiler;

class StringLiteral extends Expression {
    String value;

    StringLiteral(int lineNumber, String value) {
        super(lineNumber);
        this.value = value;
    }
}
