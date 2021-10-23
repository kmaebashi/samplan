package com.kmaebashi.samplan.compiler;

class ElsIfClause {
    Expression condition;
    Block block;

    ElsIfClause(Expression condition, Block block) {
        this.condition = condition;
        this.block = block;
    }
}
