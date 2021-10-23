package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

class VariableDeclaration extends Statement {
    String name;
    SvmType type;
    Expression initializer;

    VariableDeclaration(String name, SvmType type, Expression initializer) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }
}
