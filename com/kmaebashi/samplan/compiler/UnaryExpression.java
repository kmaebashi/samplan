package com.kmaebashi.samplan.compiler;

class UnaryExpression extends Expression {
    UnaryExpressionKind kind;
    Expression operand;

    UnaryExpression(UnaryExpressionKind kind, Expression operand) {
        this.kind = kind;
        this.operand = operand;
    }
}
