package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

abstract class Expression {
    int lineNumber;
    SvmType type;

    Expression(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
