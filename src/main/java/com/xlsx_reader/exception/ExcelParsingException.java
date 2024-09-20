package com.xlsx_reader.exception;

public class ExcelParsingException extends Exception {
    public ExcelParsingException(String message) {
        super(message);
    }

    public ExcelParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
