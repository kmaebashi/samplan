package com.kmaebashi.samplan.compiler;

class WhileStatement extends Statement {
    Expression condition;
    Block block;

    WhileStatement(Expression condition, Block block) {
        this.condition = condition;
        this.block = block;
    }
}
