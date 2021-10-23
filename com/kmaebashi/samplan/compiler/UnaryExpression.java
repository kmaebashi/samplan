package com.kmaebashi.samplan.compiler;

class UnaryExpression extends Expression {
    UnaryExpressionKind kind;
    Expression operand;

    UnaryExpression(int lineNumber, UnaryExpressionKind kind, Expression operand) {
        super(lineNumber);
        this.kind = kind;
        this.operand = operand;
    }
}
