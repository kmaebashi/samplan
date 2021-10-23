package com.kmaebashi.samplan.compiler;
import java.util.*;

class FunctionCallExpression extends Expression {
    String name;
    ArrayList<Expression> argumentList;

    FunctionCallExpression(int lineNumber, String name, ArrayList<Expression> arugmentList) {
        super(lineNumber);
        this.name = name;
        this.argumentList = argumentList;
    }
}
