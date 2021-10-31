package com.kmaebashi.samplan.main;
import com.kmaebashi.samplan.compiler.*;
import com.kmaebashi.samplan.svm.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("ソースファイル名を指定してください。");
            System.exit(1);
        }
        SvmExecutable executable = SamplanCompiler.compile(args[0]);
        SvmVirtualMachine svm = new SvmVirtualMachine(executable);
        svm.execute();
    }
}
