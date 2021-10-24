package com.kmaebashi.samplan.compiler;
import java.util.*;

class Block {
    ArrayList<Statement> statementList;
    Block outerBlock;
    HashMap<String, VariableDeclaration> variableMap
                         = new HashMap<String, VariableDeclaration>();

    Block(Block outerBlock) {
        this.outerBlock = outerBlock;
    }

    void setStatementList(ArrayList<Statement> statementList) {
        this.statementList = statementList;
    }
}