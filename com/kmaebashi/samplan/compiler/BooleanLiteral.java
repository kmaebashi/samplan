package com.kmaebashi.samplan.compiler;

class BooleanLiteral extends Expression {
    boolean value;

    BooleanLiteral(boolean value) {
        this.value = value;
    }
}
