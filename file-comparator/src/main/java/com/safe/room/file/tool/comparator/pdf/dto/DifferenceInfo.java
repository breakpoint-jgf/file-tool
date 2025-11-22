package com.safe.room.file.tool.comparator.pdf.dto;

import com.safe.room.file.tool.comparator.pdf.enumeration.DifferenceType;
import com.safe.room.file.tool.comparator.pdf.enumeration.Severity;

/**
 * Represents a single difference found during file comparison.
 * @param type The type of difference (e.g., TEXT_CONTENT, SIZE, METADATA)
 * @param description Human-readable description of the difference
 * @param location Location where the difference was found (e.g., line number, byte offset)
 * @param severity Severity level of the difference (e.g., INFO, WARNING, ERROR)
 */
/**
 * Represents a single difference found during file comparison.
 * @param type The type of difference (e.g., TEXT_CONTENT, SIZE, METADATA)
 * @param description Human-readable description of the difference
 * @param location Location where the difference was found (e.g., line number, byte offset)
 * @param severity Severity level of the difference (e.g., INFO, WARNING, ERROR)
 * @param pageNumber The page number where the difference was found (0 if not applicable)
 */
public record DifferenceInfo(
    DifferenceType type,
    String description,
    String location,
    Severity severity,
    int pageNumber
) {
    public DifferenceInfo(DifferenceType type, String description, String location, Severity severity) {
        this(type, description, location, severity, 0);
    }
}
