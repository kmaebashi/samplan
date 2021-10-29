package com.kmaebashi.samplan.svm;

public class SvmFunction {
    String name;
    int parameterCount;
    int localVariableCount;
    int[] opCode;

    public SvmFunction(String name, int parameterCount, int localVariableCount,
                       int[] opCode) {
        this.name = name;
        this.parameterCount = parameterCount;
        this.localVariableCount = localVariableCount;
        this.opCode = opCode;
    }
}
