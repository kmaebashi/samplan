package com.kmaebashi.samplan.compiler;
import java.util.*;
import com.kmaebashi.samplan.svm.*;

class FunctionDefinition {
    String name;
    ArrayList<Parameter> parameterList;
    SvmType type;
    Block block;

    FunctionDefinition(String name, ArrayList<Parameter> parameterList,
                       SvmType type, Block block) {
        this.name = name;
        this.parameterList = parameterList;
        this.type = type;
        this.block = block;
    }
}
