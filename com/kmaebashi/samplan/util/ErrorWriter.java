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
        }
    };

    public static void write(int lineNumber, ErrorMessage err, Object... args) {
        String message = MessageFormat.format(messageTable.get(err), args);
        System.err.println("" + lineNumber + ":" + message);
        System.exit(1);
    }
}
