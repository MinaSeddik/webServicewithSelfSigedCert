package com.example.springbootproject.repository;

public class MysqlCharEscaper {

//    https://dev.mysql.com/doc/refman/5.7/en/string-literals.html
    public static String escapeStringForMySQL(String s) {
        return s.replaceAll("\\", "\\\\")
                .replaceAll("\b","\\b")
                .replaceAll("\n","\\n")
                .replaceAll("\r", "\\r")
                .replaceAll("\t", "\\t")
                .replaceAll("\\x1A", "\\Z")
                .replaceAll("\\x00", "\\0")
                .replaceAll("'", "\\'")
                .replaceAll("\"", "\\\"");
    }

    public static String escapeWildcardsForMySQL(String s) {
        return escapeStringForMySQL(s)
                .replaceAll("%", "\\%")
                .replaceAll("_","\\_");
    }

}
