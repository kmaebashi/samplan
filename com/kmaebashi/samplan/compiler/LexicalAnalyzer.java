package com.kmaebashi.samplan.compiler;
import java.io.*;
import java.util.*;
import com.kmaebashi.samplan.util.*;

public class LexicalAnalyzer {
    private int currentLineNumber = 1;
    private int currentColumn = 1;
    private BufferedReader reader;

    private enum Status {
        INITIAL,
        COMMENT,
        INT_PART,
        DECIMAL_POINT,
        AFTER_DECIMAL_POINT,
        ALNUM,
        STRING,
        STRING_ESCAPE,
        OPERATOR,
    };
    private int lookAheadCharacter;
    private boolean lookingAhead = false;

    private HashMap<String, TokenType> operatorTable = new HashMap<String, TokenType>() {
        {
            put("+", TokenType.PLUS);
            put("-", TokenType.MINUS);
            put("*", TokenType.ASTERISK);
            put("/", TokenType.SLASH);
            put("{", TokenType.LEFT_BRACE);
            put("}", TokenType.RIGHT_BRACE);
            put("(", TokenType.LEFT_PAREN);
            put(")", TokenType.RIGHT_PAREN);
            put("=", TokenType.EQUAL);
            put(":=", TokenType.ASSIGNMENT);
            put("<", TokenType.GREATER_THAN);
            put("<=", TokenType.GREATER_EQUAL);
            put(">", TokenType.LESS_THAN);
            put(">=", TokenType.LESS_EQUAL);
            put("&&", TokenType.LOGICAL_AND);
            put("||", TokenType.LOGICAL_OR);
            put("++", TokenType.INCREMENT);
            put("--", TokenType.DECLREMENT);
            put(",", TokenType.COMMA);
            put(";", TokenType.SEMICOLON);
        }
    };

    private HashMap<String, TokenType> keywordTable = new HashMap<String,TokenType>() {
        {
            put("var", TokenType.VAR);
            put("boolean", TokenType.BOOLEAN);
            put("int", TokenType.INT);
            put("real", TokenType.REAL);
            put("string", TokenType.STRING);
            put("function", TokenType.FUNCTION);
            put("true", TokenType.TRUE);
            put("false", TokenType.FALSE);
            put("if", TokenType.IF);
            put("elsif", TokenType.ELSIF);
            put("else", TokenType.ELSE);
            put("while", TokenType.WHILE);
            put("return", TokenType.RETURN);
        }
    };
    
    public LexicalAnalyzer(String srcPath) {
        try {
            this.reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(srcPath), "UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    Token getToken() throws Exception {
        int ch;
        Status currentStatus = Status.INITIAL;
        TokenType tokenType;
        String currentToken = "";
        
        getCFor: for (;;) {
            ch = getc();

            switch (currentStatus) {
            case INITIAL:
                if (ch == '#') {
                    currentStatus = Status.COMMENT;
                } else if (Character.isDigit(ch)) {
                    currentToken = addLetterToToken(currentToken, ch);
                    currentStatus = Status.INT_PART;
                } else if (Character.isJavaIdentifierStart(ch)) {
                    currentToken = addLetterToToken(currentToken, ch);
                    currentStatus = Status.ALNUM;
                } else if (ch == '\"') {
                    currentStatus = Status.STRING;
                } else if (isOperatorStart(ch)) {
                    currentToken = addLetterToToken(currentToken, ch);
                    currentStatus = Status.OPERATOR;
                } else if (Character.isWhitespace(ch)) {
                    if (ch == '\n') {
                        this.currentLineNumber++;
                    }
                } else if (ch == -1) {
                    tokenType = TokenType.END_OF_FILE;
                    break getCFor;
                } else {
                    ErrorWriter.write(ErrorMessage.INVALID_CHARACTER);
                }
                break;
            case COMMENT:
                if (ch == '\n') {
                    this.currentLineNumber++;
                    currentStatus = Status.INITIAL;
                } else if (ch == -1) {
                    tokenType = TokenType.END_OF_FILE;
                    break getCFor;
                }
                break;
            case INT_PART:
                if (Character.isDigit(ch)) {
                    currentToken = addLetterToToken(currentToken, ch);
                } else if (ch == '.') {
                    currentToken = addLetterToToken(currentToken, ch);
                    currentStatus = Status.DECIMAL_POINT;
                } else {
                    ungetc(ch);
                    tokenType = TokenType.INT_VALUE;
                    break getCFor;
                }
                break;
            case DECIMAL_POINT:
                if (Character.isDigit(ch)) {
                    currentToken = addLetterToToken(currentToken, ch);
                    currentStatus = Status.AFTER_DECIMAL_POINT;
                } else {
                    ErrorWriter.write(ErrorMessage.INVALID_CHARACTER);
                }
                break;
            case AFTER_DECIMAL_POINT:
                if (Character.isDigit(ch)) {
                    currentToken = addLetterToToken(currentToken, ch);
                } else {
                    ungetc(ch);
                    tokenType = TokenType.REAL_VALUE;
                    break getCFor;
                }
                break;
            case ALNUM:
                if (Character.isJavaIdentifierPart(ch)) {
                    currentToken = addLetterToToken(currentToken, ch);
                } else {
                    ungetc(ch);
                    if (this.keywordTable.containsKey(currentToken)) {
                        tokenType = this.keywordTable.get(currentToken);
                    } else {
                        tokenType = TokenType.IDENTIFIER;
                    }
                    break getCFor;
                }
                break;
            case STRING:
                if (ch == '\\') {
                    currentStatus = Status.STRING_ESCAPE;
                } else if (ch == '\"') {
                    tokenType = TokenType.STRING_VALUE;
                    break getCFor;
                } else {
                    currentToken = addLetterToToken(currentToken, ch);
                }
                break;
            case STRING_ESCAPE:
                if (ch == 'n') {
                    currentToken = addLetterToToken(currentToken, '\n');
                } else if (ch == '\"' || ch == '\\'){
                    currentToken = addLetterToToken(currentToken, ch);
                } else {
                    ErrorWriter.write(ErrorMessage.INVALID_ESCAPE_CHARACTER_IN_STRING);
                }
                break;
            case OPERATOR:
                if (inOperator(currentToken + new String(Character.toChars(ch)))) {
                    currentToken = addLetterToToken(currentToken, ch);
                } else {
                    ungetc(ch);
                    tokenType = this.operatorTable.get(currentToken);
                    break getCFor;
                }
                break;
            default:
                ErrorWriter.write(ErrorMessage.INVALID_CHARACTER);
            }
        }
        Token ret = null;
        if (tokenType == TokenType.INT_VALUE) {
            try {
                int intValue = Integer.parseInt(currentToken);
                ret = new Token(tokenType, currentToken, intValue,
                                this.currentLineNumber, this.currentColumn);
            } catch (NumberFormatException ex) {
                ErrorWriter.write(ErrorMessage.INTEGER_PARSE, currentToken);
            }
        } else if (tokenType == TokenType.REAL_VALUE) {
            try {
                double doubleValue = Double.parseDouble(currentToken);
                ret = new Token(tokenType, currentToken, doubleValue,
                                this.currentLineNumber, this.currentColumn);
            } catch (NumberFormatException ex) {
                ErrorWriter.write(ErrorMessage.DOUBLE_PARSE, currentToken);
            }
        } else if (tokenType == TokenType.STRING_VALUE) {
            ret = new Token(tokenType, currentToken, currentToken,
                            this.currentLineNumber, this.currentColumn);
        } else {
            ret = new Token(tokenType, currentToken,
                            this.currentLineNumber, this.currentColumn);
        }
        
        return ret;
    }

    private int getc() throws Exception {
        if (this.lookingAhead) {
            this.lookingAhead = false;
            return this.lookAheadCharacter;
        } else {
            return reader.read();
        }
    }

    private void ungetc(int ch) {
        this.lookAheadCharacter = ch;
        this.lookingAhead = true;
    }

    private String addLetterToToken(String currentToken, int ch) {
        return currentToken += new String(Character.toChars(ch));
    }
    
    private boolean isOperatorStart(int ch) {
        for (String op : this.operatorTable.keySet()) {
            if (op.charAt(0) == ch) {
                return true;
            }
        }
        return false;
    }

    private boolean inOperator(String str) {
        for (String op : this.operatorTable.keySet()) {
            if (op.startsWith(str)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("ソースファイル名を指定してください。");
            return;
        }
        LexicalAnalyzer lexer = new LexicalAnalyzer(args[0]);

        for (;;) {
            Token token = lexer.getToken();
            if (token.type == TokenType.END_OF_FILE) {
                break;
            }
            System.out.println("" + token.type + ", " + token.tokenString + ", "
                               + token.intValue + ", " + token.realValue + ", "
                               + token.stringValue + ", " + token.lineNumber);
        }
    }
}
