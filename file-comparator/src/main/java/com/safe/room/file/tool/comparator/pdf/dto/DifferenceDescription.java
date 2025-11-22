package com.safe.room.file.tool.comparator.pdf.dto;

import com.safe.room.file.tool.comparator.pdf.enumeration.DifferenceType;

/**
 * Base interface for all difference descriptions.
 * Implementations should provide specific details about the difference.
 */
public interface DifferenceDescription {
    /**
     * @return A human-readable description of the difference
     */
    String getDescription();
    
    /**
     * @return The type of difference this description represents
     */
    DifferenceType getType();
    
    /**
     * @return The name of the template to use for rendering this difference
     */
    default String getTemplateName() {
        return getClass().getSimpleName();
    }
}
