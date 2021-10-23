package com.kmaebashi.samplan.compiler;

class ExpressionStatement extends Statement {
    Expression expression;

    ExpressionStatement(Expression expression) {
        this.expression = expression;
    }
}
