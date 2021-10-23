package com.kmaebashi.samplan.util;

public class ErrorWriter {
    public static void write(ErrorMessage err, Object... args) {
        System.err.println(err.toString());
        System.exit(1);
    }
}
