package com.kmaebashi.samplan.util;
import java.text.*;
import java.util.*;

public class ErrorWriter {
    private static HashMap<ErrorMessage, String> messageTable= new HashMap<ErrorMessage, String>() {
        {
            put(ErrorMessage.INVALID_CHARACTER, "不正な文字:{0}");
            put(ErrorMessage.INVALID_ESCAPE_CHARACTER_IN_STRING, "エスケープ文字中の不正な文字:{0}");
            put(ErrorMessage.UNEXPECTED_TOKEN, "{0}が期待されているところに{1}が来ています。");
            put(ErrorMessage.TYPE_EXPECTED, "型が期待されているところに{0}が来ています。");
            put(ErrorMessage.VARIABLE_DUPLICATION, "変数名が重複しています:{0}");
            put(ErrorMessage.VARIABLE_NOT_FOUND, "変数が見つかりません:{0}");
            put(ErrorMessage.TYPE_MISMATCH_MATH, "数値型が期待されています。");
            put(ErrorMessage.TYPE_MISMATCH_BOOLEAN, "boolean型が期待されています。");
            put(ErrorMessage.INC_DEC_NOT_LVALUE, "インクリメント・デクリメント演算子の対象は左辺値でなければいけません。");
            put(ErrorMessage.INC_DEC_NOT_INT, "インクリメント・デクリメント演算子の対象は整数型でなければいけません。");
            put(ErrorMessage.TYPE_MISMATCH, "型の不一致エラー。");
            put(ErrorMessage.ASSIGN_NOT_LVALUE, "代入の対象は左辺値でなければいけません。");
            put(ErrorMessage.RETURN_OUT_OF_FUNCTION, "関数の外にreturnは書けません。");
            put(ErrorMessage.FUNCTION_DUPLICATION, "関数名が重複しています:{0}");
            put(ErrorMessage.FUNCTION_NOT_FOUND, "関数が見つかりません:{0}");
            put(ErrorMessage.FUNCTION_ARG_COUNT_MISMATCH, "関数の引数の数が異なります。");
            put(ErrorMessage.FUNCTION_ARG_TYPE_MISMATCH, "関数の引数の型が異なります。");
        }
    };

    public static void write(int lineNumber, ErrorMessage err, Object... args) {
        String message = MessageFormat.format(messageTable.get(err), args);
        System.err.println("" + lineNumber + ":" + message);
        throw new RuntimeException();
        //System.exit(1);
    }
}
