package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;
import com.kmaebashi.samplan.util.*;
import java.util.*;

class OpCodeBuffer {
    ArrayList<Integer> buf = new ArrayList<Integer>();
    ArrayList<Integer> labelTable = new ArrayList<Integer>();

    int getLabel() {
        int label = this.labelTable.size();
        this.labelTable.add(-1);

        return label;
    }

    void setLabel(int label) {
        this.labelTable.set(label, this.buf.size());
    }

    void generateCode(SvmOpCode code, int... args) {
        //System.err.print("" + buf.size() + ":" + code + "\t");
        this.buf.add(code.ordinal());
        for (int arg: args) {
            this.buf.add(arg);
            // System.err.print("" + arg + ", ");
        }
        //System.err.println("");
    }

    int[] getOpCode() {
        return Util.arrayListToArrayInt(this.buf);
    }
}
