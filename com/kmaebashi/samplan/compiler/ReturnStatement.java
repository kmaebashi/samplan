package com.kmaebashi.samplan.compiler;

class ReturnStatement extends Statement {
    Expression returnValue;

    ReturnStatement(int lineNumber, Expression returnValue) {
        super(lineNumber);
        this.returnValue = returnValue;
    }
}
