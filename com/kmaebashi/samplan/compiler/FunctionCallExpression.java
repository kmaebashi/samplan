package com.kmaebashi.samplan.compiler;
import java.util.*;

class FunctionCallExpression extends Expression {
    String name;
    ArrayList<Expression> argumentList;
    FunctionDefinition functionDefinition;

    FunctionCallExpression(int lineNumber, String name, ArrayList<Expression> argumentList) {
        super(lineNumber);
        this.name = name;
        this.argumentList = argumentList;
    }
}
