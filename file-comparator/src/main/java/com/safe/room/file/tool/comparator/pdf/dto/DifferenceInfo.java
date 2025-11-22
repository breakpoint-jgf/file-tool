package com.safe.room.file.tool.comparator.pdf.dto;

import com.safe.room.file.tool.comparator.pdf.enumeration.DifferenceType;
import com.safe.room.file.tool.comparator.pdf.enumeration.Severity;
import com.safe.room.file.tool.comparator.pdf.dto.DifferenceDescription;

/**
 * Represents a single difference found during file comparison.
 * @param type The type of difference (e.g., TEXT_CONTENT, SIZE, METADATA)
 * @param description Detailed description of the difference
 * @param location Location where the difference was found (e.g., line number, byte offset)
 * @param severity Severity level of the difference (e.g., INFO, WARNING, ERROR)
 * @param pageNumber The page number where the difference was found (0 if not applicable)
 */
public record DifferenceInfo(
    DifferenceType type,
    DifferenceDescription description,
    String location,
    Severity severity,
    int pageNumber
) {
    /**
     * Creates a new DifferenceInfo with a basic string description.
     * This is a convenience constructor for backward compatibility.
     */
    public DifferenceInfo(DifferenceType type, String description, String location, Severity severity) {
        this(type, new BasicDifferenceDescription(description, type), location, severity, 0);
    }

    /**
     * Creates a new DifferenceInfo with a detailed description object.
     */
    public DifferenceInfo(DifferenceType type, DifferenceDescription description, String location, Severity severity) {
        this(type, description, location, severity, 0);
    }

    /**
     * Gets the human-readable description of the difference.
     * @return The description string
     */
    public String getDescriptionText() {
        return description.getDescription();
    }

    /**
     * A basic implementation of DifferenceDescription that wraps a simple string.
     * Package-private to allow access from other classes in the same package.
     */
    public record BasicDifferenceDescription(String description, DifferenceType type) implements DifferenceDescription {
        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public DifferenceType getType() {
            return type;
        }
    }
}
