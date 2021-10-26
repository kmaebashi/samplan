package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

class UnaryExpression extends Expression {
    UnaryExpressionKind kind;
    Expression operand;

    UnaryExpression(int lineNumber, UnaryExpressionKind kind, Expression operand) {
        super(lineNumber);
        this.kind = kind;
        this.operand = operand;

        if (kind == UnaryExpressionKind.CAST_INT_TO_REAL) {
            this.type = SvmType.REAL;
        } else if (kind == UnaryExpressionKind.CAST_REAL_TO_INT) {
            this.type = SvmType.INT;
        } else if (kind == UnaryExpressionKind.CAST_BOOLEAN_TO_STRING
                   || kind == UnaryExpressionKind.CAST_INT_TO_STRING
                   || kind == UnaryExpressionKind.CAST_REAL_TO_STRING) {
            this.type = SvmType.STRING;
        }
    }
}
