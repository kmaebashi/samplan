package com.kmaebashi.samplan.compiler;

class StringLiteral extends Expression {
    String value;

    StringLiteral(String value) {
        this.value = value;
    }
}
