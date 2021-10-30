package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.util.*;
import com.kmaebashi.samplan.svm.*;
import java.util.*;

public class TreeFixer {
    private HashMap<String, FunctionDefinition> functionMap
                              = new HashMap<String, FunctionDefinition>();
    private HashMap<String, VariableDeclaration> globalVariableMap
                              = new HashMap<String, VariableDeclaration>();
    private FunctionDefinition currentFunction;

    public TreeFixer() {
        var paramList = new ArrayList<Parameter>();
        paramList.add(new Parameter(0, "arg", SvmType.STRING));
        FunctionDefinition fd = new FunctionDefinition(0, "print", paramList,
                                                       SvmType.VOID, null);
        fd.isNative = true;
        this.functionMap.put(fd.name, fd);
    }

    public void fix(ArrayList<Declaration> declarationList) {
        registerFunctions(declarationList);

        for (var decl : declarationList) {
            if (decl instanceof FunctionDefinition fd) {
                fixFunctionDefinition(fd);
            } else {
                Statement statement = (Statement)decl;
                fixStatement(null, statement);
            }
        }
    }

    private void registerFunctions(ArrayList<Declaration> declarationList) {
        for (var decl : declarationList) {
            if (decl instanceof FunctionDefinition fd) {
                if (this.functionMap.containsKey(fd.name)) {
                    ErrorWriter.write(fd.lineNumber, ErrorMessage.FUNCTION_DUPLICATION,
                                      fd.name);
                }
                fd.functionId = this.functionMap.size();
                functionMap.put(fd.name, fd);
            }
        }
    }

    private void fixFunctionDefinition(FunctionDefinition fd) {
        this.currentFunction = fd;
        for (var param: fd.parameterList) {
            var vd = new VariableDeclaration(param.lineNumber, param.name, param.type, null);
            vd.isParameter = true;
            addLocalVariable(fd.block, vd);
        }
        Statement lastStatement
                   = fd.block.statementList.get(fd.block.statementList.size() - 1);
        Expression returnValue = null;
        if (!(lastStatement instanceof ReturnStatement)) {
            if (fd.type == SvmType.VOID) {
                returnValue = null;
            } else if ( fd.type == SvmType.INT) {
                returnValue = new IntLiteral(lastStatement.lineNumber, 0);

            } else if (fd.type == SvmType.BOOLEAN) {
                returnValue = new BooleanLiteral(lastStatement.lineNumber, false);

            } else if (fd.type == SvmType.REAL) {
                returnValue = new RealLiteral(lastStatement.lineNumber, 0);
            } else {
                assert fd.type == SvmType.STRING : fd.type;
                returnValue = new StringLiteral(lastStatement.lineNumber, "");
            }
            fd.block.statementList.add(new ReturnStatement(lastStatement.lineNumber,
                                                           returnValue));
        }
        fixBlock(fd.block);

        this.currentFunction = null;
    }

    private void fixStatement(Block currentBlock, Statement statement) {
        if (statement instanceof VariableDeclaration vd) {
            fixVariableDeclaration(currentBlock, vd);
        } else if (statement instanceof IfStatement is) {
            fixIfStatement(currentBlock, is);
        } else if (statement instanceof WhileStatement ws) {
            fixWhileStatement(currentBlock, ws);
        } else if (statement instanceof ReturnStatement rs) {
            fixReturnStatement(currentBlock, rs);
        } else if (statement instanceof ExpressionStatement es) {
            es.expression = fixExpression(currentBlock, es.expression);
        } else {
            assert false;
        }
    }

    private void fixVariableDeclaration(Block currentBlock, VariableDeclaration vd) {
        vd.initializer = fixExpression(currentBlock, vd.initializer);
        vd.initializer = castAssignment(vd.initializer, vd.type);

        if (currentBlock == null) {
            if (this.globalVariableMap.containsKey(vd.name)) {
                ErrorWriter.write(vd.lineNumber, ErrorMessage.VARIABLE_DUPLICATION,
                                  vd.name);
            }
            vd.isGlobal = true;
            vd.id = this.globalVariableMap.size();
            this.globalVariableMap.put(vd.name, vd);

        } else {
            vd.isGlobal = false;
            addLocalVariable(currentBlock, vd);
        }
    }

    private void fixIfStatement(Block currentBlock, IfStatement is) {
        is.condition = fixExpression(currentBlock, is.condition);
        if (is.condition.type != SvmType.BOOLEAN) {
            ErrorWriter.write(is.lineNumber, ErrorMessage.TYPE_MISMATCH_BOOLEAN);
        }
        fixBlock(is.block);
        for (var eic : is.elsIfClause) {
            eic.condition = fixExpression(currentBlock, eic.condition);
            if (eic.condition.type != SvmType.BOOLEAN) {
                ErrorWriter.write(is.lineNumber, ErrorMessage.TYPE_MISMATCH_BOOLEAN);
            }
            fixBlock(eic.block);
        }
        if (is.elseBlock != null) {
            fixBlock(is.elseBlock);
        }
    }

    private void fixWhileStatement(Block currentBlock, WhileStatement ws) {
        ws.condition = fixExpression(currentBlock, ws.condition);

        if (ws.condition.type != SvmType.BOOLEAN) {
            ErrorWriter.write(ws.lineNumber, ErrorMessage.TYPE_MISMATCH_BOOLEAN);
        }
        fixBlock(ws.block);
    }

    private void fixReturnStatement(Block currentBlock, ReturnStatement rs) {
        if (this.currentFunction == null) {
            ErrorWriter.write(rs.lineNumber, ErrorMessage.RETURN_OUT_OF_FUNCTION);
        }
        if (rs.returnValue != null) {
            rs.returnValue = fixExpression(currentBlock, rs.returnValue);
            rs.returnValue = castAssignment(rs.returnValue, this.currentFunction.type);
            if (rs.returnValue.type != this.currentFunction.type) {
                ErrorWriter.write(rs.lineNumber, ErrorMessage.TYPE_MISMATCH);
            }
        } else {
            rs.returnValue = new IntLiteral(rs.lineNumber, 0);
        }
    }

    private void addLocalVariable(Block currentBlock, VariableDeclaration vd) {
        for (Block pos = currentBlock; pos != null; pos = pos.outerBlock) {
            if (pos.variableMap.containsKey(vd.name)) {
                ErrorWriter.write(vd.lineNumber, ErrorMessage.VARIABLE_DUPLICATION,
                                  vd.name);
            }
        }
        if (this.globalVariableMap.containsKey(vd.name)) {
            ErrorWriter.write(vd.lineNumber, ErrorMessage.VARIABLE_DUPLICATION,
                              vd.name);
        }
        vd.id = this.currentFunction.localVariableList.size();
        this.currentFunction.localVariableList.add(vd);
        currentBlock.variableMap.put(vd.name, vd);
    }

    private void fixBlock(Block block) {
        for (var statement : block.statementList) {
            fixStatement(block, statement);
        }
    }

    private Expression fixExpression(Block currentBlock, Expression expr) {
        if (expr == null) {
            return null;
        }

        if (expr instanceof BooleanLiteral bl) {
            bl.type = SvmType.BOOLEAN;
        } else if (expr instanceof IntLiteral il) {
            il.type = SvmType.INT;
        } else if (expr instanceof RealLiteral rl) {
            rl.type = SvmType.REAL;
        } else if (expr instanceof StringLiteral sl) {
            sl.type = SvmType.STRING;
        } else if (expr instanceof IdentifierExpression ie) {
            expr = fixIdentifierExpression(currentBlock, ie);
        } else if (expr instanceof UnaryExpression ue) {
            expr = fixUnaryExpression(currentBlock, ue);
        } else if (expr instanceof BinaryExpression be) {
            expr = fixBinaryExpression(currentBlock, be);
        } else if (expr instanceof FunctionCallExpression fce) {
            expr = fixFunctionCallExpression(currentBlock, fce);
        } else {
            assert false;
        }
        return expr;
    }

    private Expression fixIdentifierExpression(Block currentBlock, IdentifierExpression ie) {
        var localVd = searchLocalVariable(currentBlock, ie.name);
        if (localVd != null) {
            ie.declaration = localVd;
        } else {
            var globalVd = this.globalVariableMap.get(ie.name);
            if (globalVd == null) {
                ErrorWriter.write(ie.lineNumber, ErrorMessage.VARIABLE_NOT_FOUND,
                                  ie.name);
            }
            ie.declaration = globalVd;
        }
        ie.type = ie.declaration.type;

        return ie;
    }

    private VariableDeclaration searchLocalVariable(Block currentBlock, String name) {
        VariableDeclaration ret = null;

        for (Block pos = currentBlock; pos != null; pos = pos.outerBlock) {
            ret = pos.variableMap.get(name);
            if (ret != null) {
                break;
            }
        }
        return ret;
    }

    private Expression fixUnaryExpression(Block currentBlock, UnaryExpression ue) {
        Expression ret = ue;

        switch (ue.kind) {
        case MINUS:
            ret = fixMinusExpression(currentBlock, ue);
            break;
        case INCREMENT: // fall through
        case DECREMENT:
            ret = fixIncDecExpression(currentBlock, ue);
            break;
        case CAST_INT_TO_REAL: // fall through
        case CAST_BOOLEAN_TO_STRING: // fall through
        case CAST_INT_TO_STRING: // fall through
        case CAST_REAL_TO_STRING: // fall through
            assert false : ue.kind;
        }
        return ret;
    }

    private Expression fixMinusExpression(Block currentBlock, UnaryExpression ue) {
        ue.operand = fixExpression(currentBlock, ue.operand);

        if (ue.operand.type != SvmType.INT
            && ue.operand.type != SvmType.REAL) {
            ErrorWriter.write(ue.lineNumber, ErrorMessage.TYPE_MISMATCH_MATH);
        }
        ue.type = ue.operand.type;

        Expression ret = ue;
        if (ue.operand instanceof IntLiteral il) {
            ret = new IntLiteral(ue.lineNumber, -il.value);
        } else if (ue.operand instanceof RealLiteral rl) {
            ret = new RealLiteral(ue.lineNumber, -rl.value);
        }
        return ret;
    }

    private Expression fixIncDecExpression(Block currentBlock, UnaryExpression ue) {
        if (!(ue.operand instanceof IdentifierExpression)) {
            ErrorWriter.write(ue.lineNumber, ErrorMessage.INC_DEC_NOT_LVALUE);
        }
        ue.operand = fixExpression(currentBlock, ue.operand);
        if (ue.operand.type != SvmType.INT) {
            ErrorWriter.write(ue.lineNumber, ErrorMessage.INC_DEC_NOT_INT);
        }
        return ue;
    }
    
    private Expression fixBinaryExpression(Block currentBlock, BinaryExpression be) {
        Expression ret = be;

        switch (be.kind) {
        case ADD: // fall through
        case SUB: // fall through
        case MUL: // fall through
        case DIV: // fall through
            ret = fixMathExpression(currentBlock, be);
            break;
        case EQUAL: // fall through
        case NOT_EQUAL: // fall through
        case GREATER_THAN: // fall through
        case GREATER_EQUAL: // fall through
        case LESS_THAN: // fall through
        case LESS_EQUAL: // fall through
            ret = fixCompareExpression(currentBlock, be);
            break;
        case LOGICAL_AND:
        case LOGICAL_OR:
            ret = fixLogicalAndOrExpression(currentBlock, be);
            break;
        case ASSIGNMENT:
            ret = fixAssignmentExpression(currentBlock, be);
        }
        return ret;
    }

    private Expression fixMathExpression(Block currentBlock, BinaryExpression be) {
        be.left = fixExpression(currentBlock, be.left);
        be.right = fixExpression(currentBlock, be.right);
        Expression ret;

        ret = evalMathExpression(be);
        if (ret instanceof IntLiteral || ret instanceof IntLiteral
            || ret instanceof StringLiteral) {
            return ret;
        }
        assert be == ret;

        castBinaryExpression(be);

        if (be.left.type == SvmType.INT
            && be.right.type == SvmType.INT) {
            be.type = SvmType.INT;
        } else if (be.left.type == SvmType.REAL
                   && be.right.type == SvmType.REAL) {
            be.type = SvmType.REAL;
        } else if (be.left.type == SvmType.STRING
                   && be.right.type == SvmType.STRING) {
            be.type = SvmType.STRING;
        } else {
            System.err.println("" + be.kind + ", " + be.left.type + ", " + be.right.type);
            ErrorWriter.write(be.lineNumber, ErrorMessage.TYPE_MISMATCH_MATH);
        }
        return ret;
    }

    private Expression evalMathExpression(BinaryExpression be) {
        Expression ret = be;

        if (be.left instanceof IntLiteral left
            && be.right instanceof IntLiteral right) {
            ret = evalMathExpressionInt(be, left.value, right.value);
        } else if (be.left instanceof RealLiteral left
                   && be.right instanceof RealLiteral right) {
            ret = evalMathExpressionReal(be, left.value, right.value);
        } else if (be.left instanceof IntLiteral left
                   && be.right instanceof RealLiteral right) {
            ret = evalMathExpressionReal(be, left.value, right.value);
        } else if (be.left instanceof RealLiteral left
                   && be.right instanceof IntLiteral right) {
            ret = evalMathExpressionReal(be, left.value, right.value);
        } else if (be.kind == BinaryExpressionKind.ADD
                   && be.left instanceof StringLiteral left
                   && be.right instanceof IntLiteral right) {
            ret = new StringLiteral(be.lineNumber, left.value + right.value);
        } else if (be.kind == BinaryExpressionKind.ADD
                   && be.left instanceof StringLiteral left
                   && be.right instanceof RealLiteral right) {
            ret = new StringLiteral(be.lineNumber, left.value + right.value);
        } else if (be.kind == BinaryExpressionKind.ADD
                   && be.left instanceof StringLiteral left
                   && be.right instanceof StringLiteral right) {
            ret = new StringLiteral(be.lineNumber, left.value + right.value);
        }

        return ret;
    }

    private Expression evalMathExpressionInt(BinaryExpression be, int left, int right)
    {
        int value = 0;

        switch (be.kind) {
        case ADD:
            value = left + right;
            break;
        case SUB:
            value = left - right;
            break;
        case MUL:
            value = left * right;
            break;
        case DIV:
            value = left / right;
            break;
        default:
            assert false;
        }

        return new IntLiteral(be.lineNumber, value);
    }

    private Expression evalMathExpressionReal(BinaryExpression be, double left, double right)
    {
        double value = 0;

        switch (be.kind) {
        case ADD:
            value = left + right;
            break;
        case SUB:
            value = left - right;
            break;
        case MUL:
            value = left * right;
            break;
        case DIV:
            value = left / right;
            break;
        default:
            assert false;
        }

        return new RealLiteral(be.lineNumber, value);
    }

    private void castBinaryExpression(BinaryExpression be) {
        Expression cast;

        if (be.left.type == SvmType.INT && be.right.type == SvmType.REAL) {
            cast = new UnaryExpression(be.lineNumber, UnaryExpressionKind.CAST_INT_TO_REAL,
                                       be.left);
            be.left = cast;
        } else if (be.left.type == SvmType.REAL && be.right.type == SvmType.INT) {
            cast = new UnaryExpression(be.lineNumber, UnaryExpressionKind.CAST_INT_TO_REAL,
                                       be.right);
            be.right = cast;
        } else if (be.left.type == SvmType.STRING && be.right.type == SvmType.BOOLEAN) {
            cast = new UnaryExpression(be.lineNumber, UnaryExpressionKind.CAST_BOOLEAN_TO_STRING,
                                       be.right);
            be.right = cast;
        } else if (be.left.type == SvmType.STRING && be.right.type == SvmType.INT) {
            cast = new UnaryExpression(be.lineNumber, UnaryExpressionKind.CAST_INT_TO_STRING,
                                       be.right);
            be.right = cast;
        } else if (be.left.type == SvmType.STRING && be.right.type == SvmType.REAL) {
            cast = new UnaryExpression(be.lineNumber, UnaryExpressionKind.CAST_REAL_TO_STRING,
                                       be.right);
            be.right = cast;
        }
    }

    private Expression fixCompareExpression(Block currentBlock, BinaryExpression be) {
        be.left = fixExpression(currentBlock, be.left);
        be.right = fixExpression(currentBlock, be.right);

        castBinaryExpression(be);
        if (be.left.type != be.right.type) {
            ErrorWriter.write(be.lineNumber, ErrorMessage.TYPE_MISMATCH);
        }
        be.type = SvmType.BOOLEAN;

        return be;
    }

    private Expression fixLogicalAndOrExpression(Block currentBlock, BinaryExpression be) {
        be.left = fixExpression(currentBlock, be.left);
        be.right = fixExpression(currentBlock, be.right);

        if (be.left.type != SvmType.BOOLEAN
            || be.right.type != SvmType.BOOLEAN) {
            ErrorWriter.write(be.lineNumber, ErrorMessage.TYPE_MISMATCH);
        }
        be.type = SvmType.BOOLEAN;

        return be;
    }

    private Expression fixAssignmentExpression(Block currentBlock, BinaryExpression be) {
        if (!(be.left instanceof IdentifierExpression)) {
            ErrorWriter.write(be.lineNumber, ErrorMessage.ASSIGN_NOT_LVALUE);
        }
        be.left = fixExpression(currentBlock, be.left);
        be.right = fixExpression(currentBlock, be.right);

        be.right = castAssignment(be.right, be.left.type);
        if (be.left.type != be.right.type) {
            ErrorWriter.write(be.lineNumber, ErrorMessage.TYPE_MISMATCH);
        }
        be.type = be.left.type;

        return be;
    }

    private Expression castAssignment(Expression src, SvmType destType) {
        if (src == null) {
            return null;
        }
        if (destType == SvmType.REAL && src.type == SvmType.INT) {
            return new UnaryExpression(src.lineNumber, UnaryExpressionKind.CAST_INT_TO_REAL,
                                       src);
        }
        if (destType == SvmType.INT && src.type == SvmType.REAL) {
            return new UnaryExpression(src.lineNumber, UnaryExpressionKind.CAST_REAL_TO_INT,
                                       src);
        }
        return src;
    }

    private Expression fixFunctionCallExpression(Block currentBlock, FunctionCallExpression fce) {
        FunctionDefinition fd = this.functionMap.get(fce.name);
        if (fd == null) {
            ErrorWriter.write(fce.lineNumber, ErrorMessage.FUNCTION_NOT_FOUND,
                              fce.name);
        }
        if (fd.parameterList.size() != fce.argumentList.size()) {
            ErrorWriter.write(fce.lineNumber, ErrorMessage.FUNCTION_ARG_COUNT_MISMATCH);
        }

        for (int i = 0; i < fd.parameterList.size(); i++) {
            Parameter param =  fd.parameterList.get(i);
            Expression arg = fixExpression(currentBlock, fce.argumentList.get(i));
            arg = castAssignment(arg, param.type);
            if (arg.type != param.type) {
                ErrorWriter.write(fce.lineNumber, ErrorMessage.FUNCTION_ARG_TYPE_MISMATCH);
            }
            fce.argumentList.set(i, arg);
        }
        fce.type = fd.type;
        fce.functionDefinition = fd;

        return fce;
    }
}

