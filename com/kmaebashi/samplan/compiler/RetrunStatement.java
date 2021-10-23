package com.kmaebashi.samplan.compiler;

class ReturnStatement extends Statement {
    Expression returnValue;

    ReturnStatement(Expression returnValue) {
        this.returnValue = returnValue;
    }
}
