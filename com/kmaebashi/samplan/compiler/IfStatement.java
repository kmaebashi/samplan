package com.kmaebashi.samplan.compiler;
import java.util.*;

class IfStatement extends Statement {
    Expression condition;
    Block block;
    ArrayList<ElsIfClause> elsIfClause;
    Block elseBlock;

    IfStatement(int lineNumber, Expression condition, Block block,
                ArrayList<ElsIfClause> elsIfClause,
                Block elseBlock) {
        super(lineNumber);
        this.condition = condition;
        this.block = block;
        this.elsIfClause = elsIfClause;
        this.elseBlock = elseBlock;
    }
}
