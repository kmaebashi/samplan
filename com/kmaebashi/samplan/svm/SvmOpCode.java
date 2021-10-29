package com.kmaebashi.samplan.svm;

public enum SvmOpCode {
    NOP(0),
    PUSH_INT(1),
    PUSH_REAL(1),
    PUSH_STRING(1),
    /**********/
    PUSH_STACK_INT(1),
    PUSH_STACK_REAL(1),
    PUSH_STACK_STRING(1),
    POP_STACK_INT(0),
    POP_STACK_REAL(1),
    POP_STACK_STRING(1),
    /**********/
    PUSH_STATIC_INT(1),
    PUSH_STATIC_REAL(1),
    PUSH_STATIC_STRING(1),
    POP_STATIC_INT(1),
    POP_STATIC_REAL(1),
    POP_STATIC_STRING(1),
    /**********/
    ADD_INT(0),
    ADD_REAL(0),
    ADD_STRING(0),
    SUB_INT(0),
    SUB_REAL(0),
    MUL_INT(0),
    MUL_REAL(0),
    DIV_INT(0),
    DIV_REAL(0),
    MINUS_INT(0),
    MINUS_REAL(0),
    INCREMENT(0),
    DECREMENT(0),
    CAST_INT_TO_REAL(0),
    CAST_REAL_TO_INT(0),
    CAST_BOOLEAN_TO_STRING(0),
    CAST_INT_TO_STRING(0),
    CAST_REAL_TO_STRING(0),
    EQ_INT(0),
    EQ_REAL(0),
    EQ_STRING(0),
    GT_INT(0),
    GT_REAL(0),
    GT_STRING(0),
    GE_INT(0),
    GE_REAL(0),
    GE_STRING(0),
    LT_INT(0),
    LT_REAL(0),
    LT_STRING(0),
    LE_INT(0),
    LE_REAL(0),
    LE_STRING(0),
    NE_INT(0),
    NE_REAL(0),
    NE_STRING(0),
    LOGICAL_AND(0),
    LOGICAL_OR(0),
    /**********/
    POP(0),
    DUPLICATE(0),
    JUMP(1),
    JUMP_IF_TRUE(1),
    JUMP_IF_FALSE(1),
    INVOKE(1),
    RETURN(0)
    ;

    private int operandCount;

    private SvmOpCode(final int operandCount) {
        this.operandCount = operandCount;
    }

    public int getOperandCount() {
        return this.operandCount;
    }
}
