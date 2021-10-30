package com.kmaebashi.samplan.svm;

public class SvmVirtualMachine {
    private SvmExecutable executable;
    private Value[] globalVariables;
    private Value[] stack;

    public SvmVirtualMachine(SvmExecutable executable) {
        this.executable = executable;
        this.globalVariables = new Value[executable.globalVariableCount];
        initValues(this.globalVariables);
        this.stack = new Value[10000];
        initValues(this.stack);
    }

    private static void initValues(Value[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = new Value();
        }
    }

    private static class RegisterInfo {
        int[] code;
        int base;
    }

    public void execute() {
        final int TOP_LEVEL = -1;
        int currentFunction = TOP_LEVEL; // top level
        int sp = 0;
        int pc = 0;
        int base = 0;

        int[] code = this.executable.topLevelCode;
        SvmOpCode[] codes = SvmOpCode.values();

        for (pc = 0; pc < code.length; ) {
            SvmOpCode instruction = codes[code[pc]];

            /*
            System.err.print("" + pc + ":\t" + instruction);
            int operandCount = instruction.getOperandCount();
            for (int i = 0; i < operandCount; i ++) {
                System.err.print(" " + code[pc + 1 + i]);
            }
            System.err.println("\t SP.." + sp);
             */
            switch (instruction) {
            case NOP:
                pc++;
                break;
            case PUSH_INT:
                this.stack[sp].intValue = code[pc + 1];
                sp++;
                pc += 2;
                break;
            case PUSH_REAL:
                this.stack[sp].realValue
                   = this.executable.realConstantPool[code[pc + 1]];
                sp++;
                pc += 2;
                break;
            case PUSH_STRING:
                this.stack[sp].stringValue
                    = this.executable.stringConstantPool[code[pc + 1]];
                sp++;
                pc += 2;
                break;
            case PUSH_STACK_INT:
                this.stack[sp].intValue = this.stack[base + code[pc + 1]].intValue;
                sp++;
                pc += 2;
                break;
            case PUSH_STACK_REAL:
                this.stack[sp].realValue = this.stack[base + code[pc + 1]].realValue;
                sp++;
                pc += 2;
                break;
            case PUSH_STACK_STRING:
                this.stack[sp].stringValue = this.stack[base + code[pc + 1]].stringValue;
                sp++;
                pc += 2;
                break;
            case POP_STACK_INT:
                this.stack[base + code[pc + 1]].intValue = this.stack[sp - 1].intValue;
                sp--;
                pc += 2;
                break;
            case POP_STACK_REAL:
                this.stack[base + code[pc + 1]].realValue = this.stack[sp - 1].realValue;
                sp--;
                pc += 2;
                break;
            case POP_STACK_STRING:
                this.stack[base + code[pc + 1]].stringValue = this.stack[sp - 1].stringValue;
                sp--;
                pc += 2;
                break;
            case PUSH_STATIC_INT:
                this.stack[sp].intValue = this.globalVariables[code[pc + 1]].intValue;
                sp++;
                pc += 2;
                break;
            case PUSH_STATIC_REAL:
                this.stack[sp].realValue = this.globalVariables[code[pc + 1]].realValue;
                sp++;
                pc += 2;
                break;
            case PUSH_STATIC_STRING:
                this.stack[sp].stringValue = this.globalVariables[code[pc + 1]].stringValue;
                sp++;
                pc += 2;
                break;
            case POP_STATIC_INT:
                this.globalVariables[code[pc + 1]].intValue = this.stack[sp - 1].intValue;
                sp--;
                pc += 2;
                break;
            case POP_STATIC_REAL:
                this.globalVariables[code[pc + 1]].realValue = this.stack[sp - 1].realValue;
                sp--;
                pc += 2;
                break;
            case POP_STATIC_STRING:
                this.globalVariables[code[pc + 1]].stringValue = this.stack[sp - 1].stringValue;
                sp--;
                pc += 2;
                break;
            case ADD_INT:
                this.stack[sp - 2].intValue
                   = this.stack[sp - 2].intValue + this.stack[sp - 1].intValue;
                sp--;
                pc++;
                break;
            case ADD_REAL:
                this.stack[sp - 2].realValue
                   = this.stack[sp - 2].realValue + this.stack[sp - 1].realValue;
                sp--;
                pc++;
                break;
            case ADD_STRING:
                this.stack[sp - 2].stringValue
                   = this.stack[sp - 2].stringValue + this.stack[sp - 1].stringValue;
                sp--;
                pc++;
                break;
            case SUB_INT:
                this.stack[sp - 2].intValue
                   = this.stack[sp - 2].intValue - this.stack[sp - 1].intValue;
                sp--;
                pc++;
                break;
            case SUB_REAL:
                this.stack[sp - 2].realValue
                   = this.stack[sp - 2].realValue - this.stack[sp - 1].realValue;
                sp--;
                pc++;
                break;
            case MUL_INT:
                this.stack[sp - 2].intValue
                   = this.stack[sp - 2].intValue * this.stack[sp - 1].intValue;
                sp--;
                pc++;
                break;
            case MUL_REAL:
                this.stack[sp - 2].realValue
                   = this.stack[sp - 2].realValue * this.stack[sp - 1].realValue;
                sp--;
                pc++;
                break;
            case DIV_INT:
                this.stack[sp - 2].intValue
                   = this.stack[sp - 2].intValue / this.stack[sp - 1].intValue;
                sp--;
                pc++;
                break;
            case DIV_REAL:
                this.stack[sp - 2].realValue
                   = this.stack[sp - 2].realValue / this.stack[sp - 1].realValue;
                sp--;
                pc++;
                break;
            case MINUS_INT:
                this.stack[sp - 1].intValue = this.stack[sp - 1].intValue;
                pc++;
                break;
            case MINUS_REAL:
                this.stack[sp - 1].realValue = this.stack[sp - 1].realValue;
                pc++;
                break;
            case INCREMENT:
                this.stack[sp - 1].intValue++;
                pc++;
                break;
            case DECREMENT:
                this.stack[sp - 1].intValue--;
                pc++;
                break;
            case CAST_INT_TO_REAL:
                this.stack[sp - 1].realValue = this.stack[sp - 1].intValue;
                pc++;
                break;
            case CAST_REAL_TO_INT:
                this.stack[sp - 1].intValue = (int)(this.stack[sp - 1].realValue);
                pc++;
                break;
            case CAST_BOOLEAN_TO_STRING:
                this.stack[sp - 1].stringValue = "" + (this.stack[sp - 1].intValue != 0);
                pc++;
                break;
            case CAST_INT_TO_STRING:
                this.stack[sp - 1].stringValue = "" + this.stack[sp - 1].intValue;
                pc++;
                break;
            case CAST_REAL_TO_STRING:
                this.stack[sp - 1].stringValue = "" + this.stack[sp - 1].realValue;
                pc++;
                break;
            case EQ_INT:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].intValue == this.stack[sp - 1].intValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case EQ_REAL:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].realValue == this.stack[sp - 1].realValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case EQ_STRING:
                this.stack[sp - 2].intValue
                     = (this.stack[sp - 2].stringValue.equals(this.stack[sp - 1].stringValue))
                        ? 1: 0;
                sp--;
                pc++;
                break;
            case GT_INT:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].intValue > this.stack[sp - 1].intValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case GT_REAL:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].realValue > this.stack[sp - 1].realValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case GT_STRING:
                this.stack[sp - 2].intValue
                    = ((this.stack[sp - 2].stringValue.
                        compareTo(this.stack[sp - 1].stringValue)) > 0) ? 1: 0;
                sp--;
                pc++;
                break;
            case GE_INT:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].intValue >= this.stack[sp - 1].intValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case GE_REAL:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].realValue >= this.stack[sp - 1].realValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case GE_STRING:
                this.stack[sp - 2].intValue
                    = ((this.stack[sp - 2].stringValue.
                        compareTo(this.stack[sp - 1].stringValue)) >= 0) ? 1: 0;
                sp--;
                pc++;
                break;
            case LT_INT:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].intValue < this.stack[sp - 1].intValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case LT_REAL:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].realValue < this.stack[sp - 1].realValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case LT_STRING:
                this.stack[sp - 2].intValue
                    = ((this.stack[sp - 2].stringValue.
                        compareTo(this.stack[sp - 1].stringValue)) < 0) ? 1: 0;
                sp--;
                pc++;
                break;
            case LE_INT:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].intValue <= this.stack[sp - 1].intValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case LE_REAL:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].realValue <= this.stack[sp - 1].realValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case LE_STRING:
                this.stack[sp - 2].intValue
                    = ((this.stack[sp - 2].stringValue.
                        compareTo(this.stack[sp - 1].stringValue)) <= 0) ? 1: 0;
                sp--;
                pc++;
                break;
            case NE_INT:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].intValue != this.stack[sp - 1].intValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case NE_REAL:
                this.stack[sp - 2].intValue
                    = (this.stack[sp - 2].realValue != this.stack[sp - 1].realValue) ? 1: 0;
                sp--;
                pc++;
                break;
            case NE_STRING:
                this.stack[sp - 2].intValue
                     = (this.stack[sp - 2].stringValue.equals(this.stack[sp - 1].stringValue))
                        ? 0: 1;
                sp--;
                pc++;
                break;
            case LOGICAL_AND:
                this.stack[sp - 2].intValue
                    = ((this.stack[sp - 2].intValue != 0)
                       && (this.stack[sp - 1].intValue != 0)) ? 1: 0;
                sp--;
                pc++;
                break;
            case LOGICAL_OR:
                this.stack[sp - 2].intValue
                    = ((this.stack[sp - 2].intValue != 0)
                       || (this.stack[sp - 1].intValue != 0)) ? 1: 0;
                sp--;
                pc++;
                break;
            case POP:
                sp--;
                pc++;
                break;
            case DUPLICATE:
                this.stack[sp - 1].copyTo(this.stack[sp]);
                sp++;
                pc++;
                break;
            case JUMP:
                pc = code[pc + 1];
                break;
            case JUMP_IF_TRUE:
                if (this.stack[sp - 1].intValue != 0) {
                    pc = code[pc + 1];
                } else {
                    pc += 2;
                }
                sp--;
                break;
            case JUMP_IF_FALSE:
                if (this.stack[sp - 1].intValue == 0) {
                    pc = code[pc + 1];
                } else {
                    pc += 2;
                }
                sp--;
                break;
            case INVOKE:
                {
                    int calleeFunction = code[pc + 1];
                    if (calleeFunction < SvmConstant.NATIVE_FUNCTION_COUNT) {
                        assert calleeFunction == 0; // print
                        String arg = stack[sp - 1].stringValue;
                        System.out.println(arg);
                        this.stack[sp - 1].intValue = 0;
                        pc += 2;
                    } else {
                        SvmFunction callee
                            = this.executable.functions[calleeFunction
                                                        - SvmConstant.NATIVE_FUNCTION_COUNT];
                        this.stack[sp].intValue = currentFunction;
                        this.stack[sp + 1].intValue = pc;
                        this.stack[sp + 2].intValue = base;
            
                        for (int i = 0; i < callee.localVariableCount; i++) {
                            this.stack[sp + SvmConstant.RETURN_INFO_SIZE + i].clear();
                        }
                        currentFunction = calleeFunction;
                        code = callee.opCode;
                        base = sp - callee.parameterCount;
                        sp += (SvmConstant.RETURN_INFO_SIZE + callee.localVariableCount);
                        pc = 0;
                    }
                }
                break;
            case RETURN:
                {
                    SvmFunction callee
                        = this.executable.functions[currentFunction
                                                    - SvmConstant.NATIVE_FUNCTION_COUNT];
                    int returnInfoIdx = sp - callee.localVariableCount
                                           - SvmConstant.RETURN_INFO_SIZE - 1;

                    currentFunction = stack[returnInfoIdx].intValue;
                    pc = this.stack[returnInfoIdx + 1].intValue + 2;
                    base = this.stack[returnInfoIdx + 2].intValue;

                    if (currentFunction == TOP_LEVEL) {
                        code = this.executable.topLevelCode;
                    } else {
                        code = this.executable
                                 .functions[currentFunction
                                            - SvmConstant.NATIVE_FUNCTION_COUNT].opCode;
                    }
                    this.stack[sp - 1].copyTo(stack[returnInfoIdx - callee.parameterCount]);
                    sp -= (callee.localVariableCount + SvmConstant.RETURN_INFO_SIZE
                           + callee.parameterCount);
                }
                break;
            }
        }
    }
}
