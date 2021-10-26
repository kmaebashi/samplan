package com.kmaebashi.samplan.compiler;
import com.kmaebashi.samplan.util.*;
import com.kmaebashi.samplan.svm.*;
import java.util.*;

public class Generator {
    private ArrayList<Double> realConstantPool = new ArrayList<Double>();
    private ArrayList<String> stringConstantPool = new ArrayList<String>();
    private ArrayList<SvmFunction> functionList = new ArrayList<SvmFunction>();

    public Generator() {
    }

    public SvmExecutable generate(ArrayList<Declaration> declarationList) {
        var opCodeBuf = new OpCodeBuffer();

        for (var decl : declarationList) {
            if (decl instanceof FunctionDefinition fd) {
                generateFunctionDefinition(fd);
            } else {
                Statement statement = (Statement)decl;
                generateStatement(opCodeBuf, statement);
            }
        }
        SvmExecutable executable = new SvmExecutable();

        return executable;
    }

    private void generateFunctionDefinition(FunctionDefinition fd) {
        var opCodeBuf = new OpCodeBuffer();

        generateBlock(opCodeBuf, fd.block);
    }

    private void generateBlock(OpCodeBuffer buf, Block block) {
        for (var statement : block.statementList) {
            generateStatement(buf, statement);
        }
    }

    private void generateStatement(OpCodeBuffer buf, Statement statement) {
        if (statement instanceof VariableDeclaration vd) {
            if (vd.initializer != null) {
                generateExpression(buf, vd.initializer);
                generatePopToVariable(buf, vd);
            }
        } else if (statement instanceof IfStatement is) {
            generateIfStatement(buf, is);

        } else if (statement instanceof WhileStatement ws) {

        } else if (statement instanceof ReturnStatement rs) {

        } else if (statement instanceof ExpressionStatement es) {
            generateExpressionStatement(buf, es);
        } else {
            assert false;
        }
    }

    private void generateExpressionStatement(OpCodeBuffer buf, ExpressionStatement es) {
        if (es.expression instanceof BinaryExpression be) {
            if (be.kind == BinaryExpressionKind.ASSIGNMENT) {
                generateAssignmentExpression(buf, be, false);
            }
        } else if (es.expression instanceof UnaryExpression ue) {
            if (ue.kind == UnaryExpressionKind.INCREMENT
                || ue.kind == UnaryExpressionKind.DECREMENT) {
                generateIncDecExpression(buf, ue, false);
            }
        } else {
            generateExpression(buf, es.expression);
            buf.generateCode(SvmOpCode.POP);
        }
    }

    private void generateExpression(OpCodeBuffer buf, Expression expr) {
        if (expr instanceof BooleanLiteral bl) {
            buf.generateCode(SvmOpCode.PUSH_INT, bl.value ? 1: 0);

        } else if (expr instanceof IntLiteral il) {
            buf.generateCode(SvmOpCode.PUSH_INT, il.value);

        } else if (expr instanceof RealLiteral rl) {
            buf.generateCode(SvmOpCode.PUSH_REAL, this.realConstantPool.size());
            this.realConstantPool.add(rl.value);

        } else if (expr instanceof StringLiteral sl) {
            buf.generateCode(SvmOpCode.PUSH_STRING, this.stringConstantPool.size());
            this.stringConstantPool.add(sl.value);

        } else if (expr instanceof IdentifierExpression ie) {
            generateIdentifierExpression(buf, ie);

        } else if (expr instanceof UnaryExpression ue) {
            generateUnaryExpression(buf, ue);

        } else if (expr instanceof BinaryExpression be) {
            generateBinaryExpression(buf, be);

        } else if (expr instanceof FunctionCallExpression fce) {
            generateFunctionCallExpression(buf, fce);
        } else {
            assert false;
        }
    }

    private void generateAssignmentExpression(OpCodeBuffer buf, BinaryExpression be,
                                              boolean inExpression) {
        generateExpression(buf, be.right);

        if (inExpression) {
            buf.generateCode(SvmOpCode.DUPLICATE);
        }

        var ie = (IdentifierExpression)be.left;

        generatePopToVariable(buf, ie.declaration);
    }

    private void generateIncDecExpression(OpCodeBuffer buf, UnaryExpression ue,
                                          boolean inExpression) {
        generateExpression(buf, ue.operand);

        if (ue.kind == UnaryExpressionKind.INCREMENT) {
            buf.generateCode(SvmOpCode.INCREMENT);
        } else {
            assert(ue.kind == UnaryExpressionKind.DECREMENT);
            buf.generateCode(SvmOpCode.DECREMENT);
        }
        if (inExpression) {
            buf.generateCode(SvmOpCode.DUPLICATE);
        }

        var ie = (IdentifierExpression)ue.operand;

        generatePopToVariable(buf, ie.declaration);
    }

    private void generatePopToVariable(OpCodeBuffer buf, VariableDeclaration declaration) {
        if (declaration.isGlobal) {
            if (declaration.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.POP_STATIC_INT, declaration.id);
            } else if (declaration.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.POP_STATIC_REAL, declaration.id);
            } else {
                assert declaration.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.POP_STATIC_STRING, declaration.id);
            }
        } else {
            int id;
            if (declaration.isParameter) {
                id = declaration.id;
            } else {
                id = declaration.id + SvmConstant.RETURN_ADDRESS_SIZE;
            }
            if (declaration.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.POP_STACK_INT, id);
            } else if (declaration.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.POP_STACK_REAL, id);
            } else {
                assert declaration.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.POP_STACK_STRING, id);
            }
        }
    }

    private void generateIdentifierExpression(OpCodeBuffer buf, IdentifierExpression ie) {
        if (ie.declaration.isGlobal) {
            if (ie.declaration.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.PUSH_STATIC_INT, ie.declaration.id);
            } else if (ie.declaration.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.PUSH_STATIC_REAL, ie.declaration.id);
            } else {
                assert ie.declaration.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.PUSH_STATIC_STRING, ie.declaration.id);
            }
        } else {
            int id;
            if (ie.declaration.isParameter) {
                id = ie.declaration.id;
            } else {
                id = ie.declaration.id + SvmConstant.RETURN_ADDRESS_SIZE;
            }
            if (ie.declaration.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.PUSH_STATIC_INT, id);
            } else if (ie.declaration.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.PUSH_STATIC_REAL, id);
            } else {
                assert ie.declaration.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.PUSH_STATIC_STRING, id);
            }
        }
    }

    private void generateUnaryExpression(OpCodeBuffer buf, UnaryExpression ue) {
        switch (ue.kind) {
        case MINUS:
            generateExpression(buf, ue.operand);
            if (ue.operand.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.MINUS_INT);
            } else {
                assert ue.operand.type == SvmType.REAL;
                buf.generateCode(SvmOpCode.MINUS_REAL);
            }
            break;
        case INCREMENT: // fall through
        case DECREMENT:
            generateIncDecExpression(buf, ue, true);
            break;
        case CAST_INT_TO_REAL:
            generateExpression(buf, ue.operand);
            buf.generateCode(SvmOpCode.CAST_INT_TO_REAL);
            break;
        case CAST_REAL_TO_INT:
            generateExpression(buf, ue.operand);
            buf.generateCode(SvmOpCode.CAST_REAL_TO_INT);
            break;
        case CAST_BOOLEAN_TO_STRING:
            generateExpression(buf, ue.operand);
            buf.generateCode(SvmOpCode.CAST_BOOLEAN_TO_STRING);
            break;
        case CAST_INT_TO_STRING:
            generateExpression(buf, ue.operand);
            buf.generateCode(SvmOpCode.CAST_INT_TO_STRING);
            break;
        case CAST_REAL_TO_STRING:
            generateExpression(buf, ue.operand);
            buf.generateCode(SvmOpCode.CAST_REAL_TO_STRING);
            break;
        default:
            assert false;
        }
    }
    private void generateBinaryExpression(OpCodeBuffer buf, BinaryExpression be) {
        generateExpression(buf,be.left);
        generateExpression(buf,be.right);

        switch (be.kind) {
        case ADD:
            if (be.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.ADD_INT);
            } else if (be.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.ADD_REAL);
            } else {
                assert be.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.ADD_STRING);
            }
            break;
        case SUB:
            if (be.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.SUB_INT);
            } else {
                assert be.type == SvmType.REAL;
                buf.generateCode(SvmOpCode.SUB_REAL);
            }
            break;
        case MUL:
            if (be.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.MUL_INT);
            } else {
                assert be.type == SvmType.REAL;
                buf.generateCode(SvmOpCode.MUL_REAL);
            }
            break;
        case DIV:
            if (be.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.DIV_INT);
            } else {
                assert be.type == SvmType.REAL;
                buf.generateCode(SvmOpCode.DIV_REAL);
            }
            break;
        case ASSIGNMENT:
            generateAssignmentExpression(buf, be, true);
            break;
        case EQUAL:
            if (be.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.EQ_INT);
            } else if (be.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.EQ_REAL);
            } else {
                assert be.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.EQ_STRING);
            }
            break;
        case NOT_EQUAL:
            if (be.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.NE_INT);
            } else if (be.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.NE_REAL);
            } else {
                assert be.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.NE_STRING);
            }
            break;
        case GREATER_THAN:
            if (be.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.GT_INT);
            } else if (be.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.GT_REAL);
            } else {
                assert be.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.GT_STRING);
            }
            break;
        case GREATER_EQUAL:
            if (be.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.GE_INT);
            } else if (be.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.GE_REAL);
            } else {
                assert be.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.GE_STRING);
            }
            break;
        case LESS_THAN:
            if (be.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.LT_INT);
            } else if (be.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.LT_REAL);
            } else {
                assert be.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.LT_STRING);
            }
            break;
        case LESS_EQUAL:
            if (be.type == SvmType.INT) {
                buf.generateCode(SvmOpCode.LE_INT);
            } else if (be.type == SvmType.REAL) {
                buf.generateCode(SvmOpCode.LE_REAL);
            } else {
                assert be.type == SvmType.STRING;
                buf.generateCode(SvmOpCode.LE_STRING);
            }
            break;
        case LOGICAL_AND:
            buf.generateCode(SvmOpCode.LOGICAL_AND);
            break;
        case LOGICAL_OR:
            buf.generateCode(SvmOpCode.LOGICAL_OR);
            break;
        }
    }

    private void generateFunctionCallExpression(OpCodeBuffer buf, FunctionCallExpression fce) {
        for (var arg: fce.argumentList) {
            generateExpression(buf, arg);
        }
        buf.generateCode(SvmOpCode.INVOKE, fce.functionDefinition.functionId);
    }

    private void generateIfStatement(OpCodeBuffer buf, IfStatement is) {
        generateExpression(buf, is.condition);
        int ifFalseLabel = buf.getLabel();
        buf.generateCode(SvmOpCode.JUMP_IF_FALSE, ifFalseLabel);
        generateBlock(buf, is.block);
        int endLabel = buf.getLabel();
        buf.generateCode(SvmOpCode.JUMP, endLabel);
        buf.setLabel(ifFalseLabel);

        for (ElsIfClause eic : is.elsIfClause) {
            generateExpression(buf, eic.condition);
            ifFalseLabel = buf.getLabel();
            buf.generateCode(SvmOpCode.JUMP_IF_FALSE, ifFalseLabel);
            generateBlock(buf, eic.block);
            buf.generateCode(SvmOpCode.JUMP, endLabel);
            buf.setLabel(ifFalseLabel);
        }
        if (is.elseBlock != null) {
            generateBlock(buf, is.elseBlock);
        }
        buf.setLabel(endLabel);
    }

    private void generateWhileStatement(OpCodeBuffer buf, WhileStatement ws) {
        int loopLabel = buf.getLabel();
        buf.setLabel(loopLabel);

        generateExpression(buf, ws.condition);

        int endLabel = buf.getLabel();
        buf.generateCode(SvmOpCode.JUMP_IF_FALSE, endLabel);

        generateBlock(buf, ws.block);

        buf.setLabel(endLabel);
    }
}
