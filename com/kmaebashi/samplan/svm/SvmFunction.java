package com.kmaebashi.samplan.svm;

public class SvmFunction {
    String name;
    int parameterCount;
    int[] opCode;

    public SvmFunction(String name, int parameterCount, int[] opCode) {
        this.name = name;
        this.parameterCount = parameterCount;
        this.opCode = opCode;
    }
}
