package com.kmaebashi.samplan.compiler;

class BinaryExpression extends Expression {
    BinaryExpressionKind kind;
    Expression left;
    Expression right;

    BinaryExpression(BinaryExpressionKind kind, Expression left, Expression right) {
        this.kind = kind;
        this.left = left;
        this.right = right;
    }
}
