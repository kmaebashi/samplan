package com.kmaebashi.samplan.compiler;

class ExpressionStatement extends Statement {
    Expression expression;

    ExpressionStatement(int lineNumber, Expression expression) {
        super(lineNumber);
        this.expression = expression;
    }
}
