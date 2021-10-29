package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("ソースファイル名を指定してください。");
            System.exit(1);
        }
        LexicalAnalyzer lexer = new LexicalAnalyzer(args[0]);
        Parser parser = new Parser(lexer);
        var declarationList = parser.parse();
        var fixer = new TreeFixer();
        fixer.fix(declarationList);
        Generator generator = new Generator();
        SvmExecutable executable = generator.generate(declarationList);

        SvmVirtualMachine svm = new SvmVirtualMachine(executable);
        svm.execute();
    }
}
