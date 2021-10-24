package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

class BooleanLiteral extends Expression {
    boolean value;

    BooleanLiteral(int lineNumber, boolean value) {
        super(lineNumber);
        this.type = SvmType.BOOLEAN;
        this.value = value;
    }
}
