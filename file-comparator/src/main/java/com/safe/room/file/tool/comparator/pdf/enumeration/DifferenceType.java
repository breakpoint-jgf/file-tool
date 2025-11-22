package com.safe.room.file.tool.comparator.pdf.enumeration;

/**
 * Enumerates the types of differences that can be found during file comparison.
 */
public enum DifferenceType {
    /**
     * Difference in file size.
     */
    SIZE("SIZE"),
    
    /**
     * Difference in the number of pages.
     */
    PAGE_COUNT("PAGE_COUNT"),
    
    /**
     * Difference in document metadata.
     */
    METADATA("METADATA"),
    
    /**
     * Difference in text content.
     */
    TEXT_CONTENT("TEXT_CONTENT");
    
    private final String value;
    
    DifferenceType(String value) {
        this.value = value;
    }
    
    /**
     * @return the string representation of this difference type
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
