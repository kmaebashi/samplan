package com.kmaebashi.samplan.compiler;
import java.util.*;
import com.kmaebashi.samplan.svm.*;

class FunctionDefinition extends Declaration {
    int lineNumber;
    String name;
    ArrayList<Parameter> parameterList;
    SvmType type;
    Block block;

    FunctionDefinition(int lineNumber, String name, ArrayList<Parameter> parameterList,
                       SvmType type, Block block) {
        this.lineNumber = lineNumber;
        this.name = name;
        this.parameterList = parameterList;
        this.type = type;
        this.block = block;
    }
}
