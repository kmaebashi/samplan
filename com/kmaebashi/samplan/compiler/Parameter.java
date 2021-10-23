package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

class Parameter {
    String name;
    SvmType type;

    Parameter(String name, SvmType type) {
        this.name = name;
        this.type = type;
    }
}
