package com.kmaebashi.samplan.svm;

class Value {
    int intValue;
    double realValue;
    String stringValue;

    Value() {
        clear();
    }

    void clear() {
        this.intValue = 0;
        this.realValue = 0.0;
        this.stringValue = "";
    }

    void copyTo(Value dest) {
        dest.intValue = this.intValue;
        dest.realValue = this.realValue;
        dest.stringValue = this.stringValue;
    }
}
