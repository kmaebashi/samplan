package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

class VariableDeclaration extends Statement {
    String name;
    SvmType type;
    Expression initializer;
    boolean isGlobal;
    boolean isParameter = false;
    int id;

    VariableDeclaration(int lineNumber, String name, SvmType type, Expression initializer) {
        super(lineNumber);
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }
}
