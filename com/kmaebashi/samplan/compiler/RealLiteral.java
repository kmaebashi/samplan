package com.kmaebashi.samplan.compiler;

class RealLiteral extends Expression {
    double value;

    RealLiteral(double value) {
        this.value = value;
    }
}
