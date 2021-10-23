package com.kmaebashi.samplan.compiler;

abstract class Statement extends Declaration {
    int lineNumber;

    Statement(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
