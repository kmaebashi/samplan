package com.kmaebashi.samplan.compiler;

abstract class Statement {
    int lineNumber;

    Statement(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
