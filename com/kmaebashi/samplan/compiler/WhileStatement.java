package com.kmaebashi.samplan.compiler;

class WhileStatement extends Statement {
    Expression condition;
    Block block;

    WhileStatement(int lineNumber, Expression condition, Block block) {
        super(lineNumber);
        this.condition = condition;
        this.block = block;
    }
}
