package com.kmaebashi.samplan.compiler;

class BinaryExpression extends Expression {
    BinaryExpressionKind kind;
    Expression left;
    Expression right;

    BinaryExpression(int lineNumber, BinaryExpressionKind kind, Expression left, Expression right) {
        super(lineNumber);
        this.kind = kind;
        this.left = left;
        this.right = right;
    }
}
