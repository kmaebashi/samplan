package com.kmaebashi.samplan.compiler;
import java.util.*;
import com.kmaebashi.samplan.util.*;
import com.kmaebashi.samplan.svm.*;

public class Parser {
    private Token lookAheadToken;
    private boolean lookingAhead = false;
    private LexicalAnalyzer lexer;

    Block currentBlock = null;

    public ArrayList<Declaration> parse() throws Exception {
        var declarationList = new ArrayList<Declaration>();

        for (;;) {
            Token token = getToken();
            if (token.type == TokenType.END_OF_FILE) {
                break;
            } else if (token.type == TokenType.FUNCTION) {
                FunctionDefinition fd = parseFunctionDefinition(token.lineNumber);
                declarationList.add(fd);
            } else {
                ungetToken(token);
                Statement statement = parseStatement();
                declarationList.add(statement);
            }
        }
        return declarationList;
    }

    private Token getToken() throws Exception {
        if (this.lookingAhead) {
            this.lookingAhead = false;
            return lookAheadToken;
        } else {
            return lexer.getToken();
        }
    }

    private void ungetToken(Token token) {
        lookAheadToken = token;
        lookingAhead = true;
    }

    private FunctionDefinition parseFunctionDefinition(int lineNumber) throws Exception {
        String name = parseIdentifier();
        ArrayList<Parameter> parameterList = parseParameterList();

        SvmType type = SvmType.VOID; // make compiler happy
        Token afterParameterToken = getToken();
        if (afterParameterToken.type == TokenType.LEFT_BRACE) {
            type = SvmType.VOID;
            ungetToken(afterParameterToken);
        } else if (isTypeSpecifierToken(afterParameterToken.type)){
            type = convertType(afterParameterToken.type);
        } else {
            ErrorWriter.write(afterParameterToken.lineNumber, ErrorMessage.UNEXPECTED_TOKEN,
                              "{", afterParameterToken.tokenString);
        }
        Block block = parseBlock();

        return new FunctionDefinition(lineNumber, name, parameterList, type, block);
    }

    private void checkToken(Token token, TokenType expected) {
        if (token.type != expected) {
            ErrorWriter.write(token.lineNumber, ErrorMessage.UNEXPECTED_TOKEN,
                              expected.toString(), token.tokenString);
            throw new RuntimeException();
        }
    }

    private String parseIdentifier() throws Exception {
        Token token = getToken();

        checkToken(token, TokenType.IDENTIFIER);

        return token.tokenString;
    }

    private boolean isTypeSpecifierToken(TokenType tokenType) throws Exception {
        if (tokenType == TokenType.BOOLEAN
            || tokenType == TokenType.INT
            || tokenType == TokenType.REAL
            || tokenType == TokenType.STRING) {
            return true;
        }
        return false;
    }

    private SvmType convertType(TokenType tokenType) throws Exception {
        if (tokenType == TokenType.BOOLEAN) {
            return SvmType.BOOLEAN;
        } if (tokenType == TokenType.INT) {
            return SvmType.INT;
        } if (tokenType == TokenType.REAL) {
            return SvmType.REAL;
        } if (tokenType == TokenType.STRING) {
            return SvmType.STRING;
        } else {
            assert false;
        }
        return SvmType.VOID; // make compiler happy
    }

    private ArrayList<Parameter> parseParameterList() throws Exception {
        Token token = getToken();
        checkToken(token, TokenType.LEFT_PAREN);
        var paramList = new ArrayList<Parameter>();

        for (;;) {
            token = getToken();
            if (token.type == TokenType.RIGHT_PAREN) {
                break;
            }
            ungetToken(token);
            String name = parseIdentifier();
            SvmType type = parseType();
            Parameter param = new Parameter(token.lineNumber, name, type);
            paramList.add(param);

            token = getToken();
            if (token.type != TokenType.COMMA) {
                break;
            }
        }
        checkToken(token, TokenType.RIGHT_PAREN);

        return paramList;
    }

    private SvmType parseType() throws Exception {
        Token token = getToken();

        if (!isTypeSpecifierToken(token.type)) {
            System.err.println("parseType:" + token.type + ", " + token.lineNumber);
            ErrorWriter.write(token.lineNumber, ErrorMessage.TYPE_EXPECTED,
                              token.tokenString);
        }
        return convertType(token.type);
    }
    
    private Block parseBlock() throws Exception {
        Token token = getToken();
        var statementList = new ArrayList<Statement>();

        checkToken(token, TokenType.LEFT_BRACE);

        Block newBlock = new Block(this.currentBlock);
        this.currentBlock = newBlock;
        for (;;) {
            token = getToken();
            if (token.type == TokenType.RIGHT_BRACE) {
                break;
            }
            ungetToken(token);
            Statement statement = parseStatement();
            statementList.add(statement);
        }
        newBlock.setStatementList(statementList);
        this.currentBlock = newBlock.outerBlock;

        return newBlock;
    }

    private Statement parseStatement() throws Exception {
        Token token = getToken();
        Statement statement;

        if (token.type == TokenType.VAR) {
            statement = parseVariableDeclaration(token.lineNumber);
        } else if (token.type == TokenType.IF) {
            statement = parseIfStatement(token.lineNumber);
        } else if (token.type == TokenType.WHILE) {
            statement = parseWhileStatement(token.lineNumber);
        } else if (token.type == TokenType.RETURN) {
            statement = parseReturnStatement(token.lineNumber);
        } else {
            ungetToken(token);
            statement = parseExpressionStatement(token.lineNumber);
        }
        return statement;
    }

    private VariableDeclaration parseVariableDeclaration(int lineNumber) throws Exception {
        String name = parseIdentifier();
        SvmType type = parseType();
        Expression initializer = null;

        Token token = getToken();
        if (token.type == TokenType.ASSIGNMENT) {
            initializer = parseExpression();
            token = getToken();
        }
        checkToken(token, TokenType.SEMICOLON);

        return new VariableDeclaration(lineNumber, name, type, initializer);
    }
    
    private IfStatement parseIfStatement(int lineNumber) throws Exception {
        Expression condition = parseExpression();
        Block block = parseBlock();
        Token token;

        var elsIfClauseList = new ArrayList<ElsIfClause>();
        for (;;) {
            token = getToken();
            if (token.type != TokenType.ELSIF) {
                break;
            }
            Expression elsIfCondition = parseExpression();
            Block elsIfBlock = parseBlock();

            elsIfClauseList.add(new ElsIfClause(elsIfCondition, elsIfBlock));
        }
        Block elseBlock = null;
        if (token.type == TokenType.ELSE) {
            elseBlock = parseBlock();
        } else {
            ungetToken(token);
        }
        return new IfStatement(lineNumber, condition, block, elsIfClauseList, elseBlock);
    }

    private WhileStatement parseWhileStatement(int lineNumber) throws Exception {
        Expression condition = parseExpression();
        Block block = parseBlock();

        return new WhileStatement(lineNumber, condition, block);
    }

    private ReturnStatement parseReturnStatement(int lineNumber) throws Exception {
        Token token = getToken();
        Expression returnValue = null;

        if (token.type != TokenType.SEMICOLON) {
            ungetToken(token);
            returnValue = parseExpression();
            token = getToken();
            checkToken(token, TokenType.SEMICOLON);
        }

        return new ReturnStatement(lineNumber, returnValue);
    }

    
    private ExpressionStatement parseExpressionStatement(int lineNumber) throws Exception {
        Expression expr = parseExpression();
        Token token = getToken();
        checkToken(token, TokenType.SEMICOLON);

        return new ExpressionStatement(lineNumber, expr);
    }

    private Expression parseExpression() throws Exception {
        return parseAssignmentExpression();
    }

    private Expression parseAssignmentExpression() throws Exception {
        Expression left = parseLogicalExpression();

        Token token = getToken();
        if (token.type == TokenType.ASSIGNMENT) {
            Expression right = parseAssignmentExpression();
            return new BinaryExpression(token.lineNumber,
                                        BinaryExpressionKind.ASSIGNMENT,
                                        left, right);
        } else {
            ungetToken(token);
            return left;
        }
    }

    private Expression parseLogicalExpression() throws Exception {
        Expression left = parseCompareExpression();
        BinaryExpressionKind kind;

        for (;;) {
            Token token = getToken();
            if (token.type == TokenType.LOGICAL_AND) {
                kind = BinaryExpressionKind.LOGICAL_AND;
            } else if (token.type == TokenType.LOGICAL_OR) {
                kind = BinaryExpressionKind.LOGICAL_OR;
            } else {
                ungetToken(token);
                break;
            }
            Expression right = parseCompareExpression();
            BinaryExpression expr = new BinaryExpression(token.lineNumber, kind, left, right);
            left = expr;
        }

        return left;
    }

    private Expression parseCompareExpression() throws Exception {
        Expression left = parseAdditiveExpression();
        BinaryExpressionKind kind;

        for (;;) {
            Token token = getToken();
            if (token.type == TokenType.EQUAL) {
                kind = BinaryExpressionKind.EQUAL;
            } else if (token.type == TokenType.NOT_EQUAL) {
                kind = BinaryExpressionKind.NOT_EQUAL;
            } else if (token.type == TokenType.GREATER_THAN) {
                kind = BinaryExpressionKind.GREATER_THAN;
            } else if (token.type == TokenType.GREATER_EQUAL) {
                kind = BinaryExpressionKind.GREATER_EQUAL;
            } else if (token.type == TokenType.LESS_THAN) {
                kind = BinaryExpressionKind.LESS_THAN;
            } else if (token.type == TokenType.LESS_EQUAL) {
                kind = BinaryExpressionKind.LESS_EQUAL;
            } else {
                ungetToken(token);
                break;
            }
            Expression right = parseAdditiveExpression();
            BinaryExpression expr = new BinaryExpression(token.lineNumber, kind, left, right);
            left = expr;
        }

        return left;
    }

    private Expression parseAdditiveExpression() throws Exception {
        Expression left = parseMultiplicativeExpression();
        BinaryExpressionKind kind;

        for (;;) {
            Token token = getToken();
            if (token.type == TokenType.PLUS) {
                kind = BinaryExpressionKind.ADD;
            } else if (token.type == TokenType.MINUS) {
                kind = BinaryExpressionKind.SUB;
            } else {
                ungetToken(token);
                break;
            }
            Expression right = parseMultiplicativeExpression();
            BinaryExpression expr = new BinaryExpression(token.lineNumber, kind, left, right);
            left = expr;
        }

        return left;
    }

    private Expression parseMultiplicativeExpression() throws Exception {
        Expression left = parseUnaryExpression();
        BinaryExpressionKind kind;

        for (;;) {
            Token token = getToken();
            if (token.type == TokenType.ASTERISK) {
                kind = BinaryExpressionKind.MUL;
            } else if (token.type == TokenType.SLASH) {
                kind = BinaryExpressionKind.DIV;
            } else {
                ungetToken(token);
                break;
            }
            Expression right = parseUnaryExpression();
            BinaryExpression expr = new BinaryExpression(token.lineNumber, kind, left, right);
            left = expr;
        }

        return left;
    }
    
    private Expression parseUnaryExpression() throws Exception {
        UnaryExpressionKind kind;
        Token token = getToken();
        Expression expr;

        if (token.type == TokenType.MINUS) {
            Expression operand = parseUnaryExpression();
            expr = new UnaryExpression(token.lineNumber, UnaryExpressionKind.MINUS, operand);

        } else if (token.type == TokenType.INCREMENT) {
            Expression operand = parsePrimaryExpression();
            expr = new UnaryExpression(token.lineNumber, UnaryExpressionKind.INCREMENT, operand);

        } else if (token.type == TokenType.DECREMENT) {
            Expression operand = parsePrimaryExpression();
            expr = new UnaryExpression(token.lineNumber, UnaryExpressionKind.DECREMENT, operand);

        } else {
            ungetToken(token);
            expr = parsePrimaryExpression();
        }
        return expr;
    }

    private Expression parsePrimaryExpression() throws Exception {
        Token token = getToken();
        Expression expr = null;
        
        if (token.type == TokenType.TRUE) {
            expr = new BooleanLiteral(token.lineNumber, true);
        } else if (token.type == TokenType.FALSE) {
            expr = new BooleanLiteral(token.lineNumber, false);
        } else if (token.type == TokenType.INT_VALUE) {
            expr = new IntLiteral(token.lineNumber, token.intValue);
        } else if (token.type == TokenType.REAL_VALUE) {
            expr = new RealLiteral(token.lineNumber, token.realValue);
        } else if (token.type == TokenType.STRING_VALUE) {
            expr = new StringLiteral(token.lineNumber, token.tokenString);
        } else if (token.type == TokenType.LEFT_PAREN) {
            expr = parseExpression();
            token = getToken();
            checkToken(token, TokenType.RIGHT_PAREN);
        } else if (token.type == TokenType.IDENTIFIER) {
            expr = parseStartsWithIdentifierExpression(token);
        } else {
            ErrorWriter.write(token.lineNumber, ErrorMessage.UNEXPECTED_TOKEN,
                              "primary expression", token.tokenString);
        }

        return expr;
    }

    private Expression parseStartsWithIdentifierExpression(Token identifierToken) throws Exception {
        Token token = getToken();
        if (token.type == TokenType.LEFT_PAREN) {
            ArrayList<Expression> argumentList = parseArgumentList();
            return new FunctionCallExpression(identifierToken.lineNumber,
                                              identifierToken.tokenString, argumentList);
        } else {
            ungetToken(token);
            return new IdentifierExpression(identifierToken.lineNumber,
                                            identifierToken.tokenString);
        }
    }

    private ArrayList<Expression> parseArgumentList() throws Exception {
        ArrayList<Expression> ret = new ArrayList<Expression>();
        Token token;
        for (;;) {
            Expression expr = parseExpression();
            ret.add(expr);
            
            token = getToken();
            if (token.type != TokenType.COMMA) {
                break;
            }
        }
        checkToken(token, TokenType.RIGHT_PAREN);

        return ret;
    }

    Parser(LexicalAnalyzer lexer) {
        this.lexer = lexer;
    }

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
    }
}
