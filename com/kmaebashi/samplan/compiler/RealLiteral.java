package com.kmaebashi.samplan.compiler;

class RealLiteral extends Expression {
    double value;

    RealLiteral(int lineNumber, double value) {
        super(lineNumber);
        this.value = value;
    }
}
