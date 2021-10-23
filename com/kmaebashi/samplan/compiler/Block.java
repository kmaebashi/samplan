package com.kmaebashi.samplan.compiler;
import java.util.*;

class Block {
    ArrayList<Statement> statementList;
    Block outerBlock;

    Block(Block outerBlock) {
        this.outerBlock = outerBlock;
    }

    void setStatementList(ArrayList<Statement> statementList) {
        this.statementList = statementList;
    }
}