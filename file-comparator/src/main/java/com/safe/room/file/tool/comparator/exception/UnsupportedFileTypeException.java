package com.safe.room.file.tool.comparator.exception;

/**
 * Exception thrown when no comparator is available for a given file type.
 */
public class UnsupportedFileTypeException extends Exception {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
    
    public UnsupportedFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
