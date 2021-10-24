package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

class StringLiteral extends Expression {
    String value;

    StringLiteral(int lineNumber, String value) {
        super(lineNumber);
        this.type = SvmType.STRING;
        this.value = value;
    }
}
