package com.kmaebashi.samplan.compiler;
import java.util.*;

class FunctionCallExpression extends Expression {
    String name;
    ArrayList<Expression> argumentList;

    FunctionCallExpression(String name, ArrayList<Expression> arugmentList) {
        this.name = name;
        this.argumentList = argumentList;
    }
}
