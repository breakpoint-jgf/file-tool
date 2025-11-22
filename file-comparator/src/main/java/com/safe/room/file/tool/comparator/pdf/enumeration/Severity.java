package com.safe.room.file.tool.comparator.pdf.enumeration;

/**
 * Enumerates the severity levels for differences found during comparison.
 */
public enum Severity {
    /**
     * Informational message, lowest severity.
     */
    INFO("INFO"),
    
    /**
     * Warning message, medium severity.
     */
    WARNING("WARNING"),
    
    /**
     * Error message, highest severity.
     */
    ERROR("ERROR");
    
    private final String value;
    
    Severity(String value) {
        this.value = value;
    }
    
    /**
     * @return the string representation of this severity level
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
