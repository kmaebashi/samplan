package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

class RealLiteral extends Expression {
    double value;

    RealLiteral(int lineNumber, double value) {
        super(lineNumber);
        this.type = SvmType.REAL;
        this.value = value;
    }
}
