package com.kmaebashi.samplan.compiler;
import java.util.*;
import com.kmaebashi.samplan.svm.*;

class FunctionDefinition extends Declaration {
    int lineNumber;
    String name;
    ArrayList<Parameter> parameterList;
    SvmType type;
    Block block;
    int functionId;
    ArrayList<VariableDeclaration> localVariableList
                                      = new ArrayList<VariableDeclaration>();
    boolean isNative = false;

    FunctionDefinition(int lineNumber, String name, ArrayList<Parameter> parameterList,
                       SvmType type, Block block) {
        this.lineNumber = lineNumber;
        this.name = name;
        this.parameterList = parameterList;
        this.type = type;
        this.block = block;
    }
}
