package com.kmaebashi.samplan.svm;

public class SvmExecutable {
    double[] realConstantPool;
    String[] stringConstantPool;
    int globalVariableCount;

    SvmFunction[] functions;
    int[] topLevelCode;

    public SvmExecutable(double[] realConstantPool, String[] stringConstantPool,
                         int globalVariableCount, SvmFunction[] functions,
                         int[] topLevelCode) {

        this.realConstantPool = realConstantPool;
        this.stringConstantPool = stringConstantPool;
        this.globalVariableCount = globalVariableCount;
        this.functions = functions;
        this.topLevelCode = topLevelCode;
    }

    public void dump() {
        System.err.println("real constalt pool:");
        for (int i = 0; i < this.realConstantPool.length; i++) {
            System.err.println("" + i + ":\t" + this.realConstantPool[i]);
        }

        System.err.println("string constalt pool:");
        for (int i = 0; i < this.stringConstantPool.length; i++) {
            System.err.println("" + i + ":\t" + this.stringConstantPool[i]);
        }

        System.err.println("global variable count:" + this.globalVariableCount);

        System.err.println("topLevel:");
        dumpOpCode(topLevelCode);

        System.err.println("");

        for (int i = 0; i < this.functions.length; i++) {
            System.err.println("function[" + i + "]\t" + this.functions[i].name + ", "
                               + this.functions[i].parameterCount + "params");
            dumpOpCode(this.functions[i].opCode);
        }
    }

    private void dumpOpCode(int[] opCode) {
        SvmOpCode[] codes = SvmOpCode.values();

        for (int i = 0; i < opCode.length; i++) {
            SvmOpCode code = codes[opCode[i]];
            System.err.print("" + i + ":\t" + code);

            int operandCount = code.getOperandCount();
            for (int j = 0; j < operandCount; j++) {
                System.err.print(" " + opCode[i + j + 1]);
            }
            i += operandCount;
            System.err.println("");
        }
    }
}
