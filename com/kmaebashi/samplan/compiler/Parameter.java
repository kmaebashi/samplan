package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

class Parameter {
    String name;
    SvmType type;
    int lineNumber;

    Parameter(int lineNumber, String name, SvmType type) {
        this.lineNumber = lineNumber;
        this.name = name;
        this.type = type;
    }
}
