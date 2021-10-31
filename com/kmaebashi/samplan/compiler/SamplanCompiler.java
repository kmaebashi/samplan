package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.svm.*;

public class SamplanCompiler {
    public static SvmExecutable compile(String filename) throws Exception {
        LexicalAnalyzer lexer = new LexicalAnalyzer(filename);
        Parser parser = new Parser(lexer);
        var declarationList = parser.parse();
        var fixer = new TreeFixer();
        fixer.fix(declarationList);
        Generator generator = new Generator();
        SvmExecutable executable = generator.generate(declarationList);

        return executable;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("ソースファイル名を指定してください。");
            System.exit(1);
        }

        SvmExecutable executable = SamplanCompiler.compile(args[0]);
        executable.dump();
    }
}
