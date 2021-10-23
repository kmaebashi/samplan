package com.kmaebashi.samplan.compiler;
import java.util.*;

class IfStatement extends Statement {
    Expression condition;
    Block block;
    ArrayList<ElsIfClause> elsIfClause;
    Block elseBlock;

    IfStatement(Expression condition, Block block, ArrayList<ElsIfClause> elsIfClause,
                Block elseBlock) {
        this.condition = condition;
        this.block = block;
        this.elsIfClause = elsIfClause;
        this.elseBlock = elseBlock;
    }
}
