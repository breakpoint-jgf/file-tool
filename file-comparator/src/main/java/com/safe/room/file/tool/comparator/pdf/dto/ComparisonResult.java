package com.safe.room.file.tool.comparator.pdf.dto;

import java.util.List;

/**
 * Represents the result of a file comparison.
 */
public record ComparisonResult(
        boolean areEqual,
        List<DifferenceInfo> differences
) {
    /**
     * Creates a new ComparisonResult.
     *
     * @param areEqual Whether the files are considered equal
     * @param differences List of differences found during comparison
     */
    public ComparisonResult {
        if (differences == null) {
            throw new IllegalArgumentException("Differences list cannot be null");
        }
    }
}