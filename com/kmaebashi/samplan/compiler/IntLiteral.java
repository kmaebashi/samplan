package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

class IntLiteral extends Expression {
    int value;

    IntLiteral(int lineNumber, int value) {
        super(lineNumber);
        this.type = SvmType.INT;
        this.value = value;
    }
}
