package com.safe.room.file.tool.comparator.pdf.dto;

import com.safe.room.file.tool.comparator.pdf.criteria.PdfComparisonCriteria;
import com.safe.room.file.tool.comparator.pdf.criteria.TextContentComparisonCriteria;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the result of a file comparison.
 */
public record ComparisonResult(
        boolean areEqual,
        List<DifferenceInfo> differences,
        List<String> criteriaUsed
) {
    /**
     * Creates a new ComparisonResult.
     *
     * @param areEqual Whether the files are considered equal
     * @param differences List of differences found during comparison
     */
    public ComparisonResult(boolean areEqual, List<DifferenceInfo> differences) {
        this(areEqual, differences, List.of());
    }

    /**
     * Creates a new ComparisonResult with criteria information.
     *
     * @param areEqual Whether the files are considered equal
     * @param differences List of differences found during comparison
     * @param criteriaUsed List of criteria class names used for comparison
     */
    public ComparisonResult {
        if (differences == null) {
            throw new IllegalArgumentException("Differences list cannot be null");
        }
        if (criteriaUsed == null) {
            throw new IllegalArgumentException("Criteria used list cannot be null");
        }
    }
    
    /**
     * Creates a new ComparisonResult with criteria information.
     *
     * @param areEqual Whether the files are considered equal
     * @param differences List of differences found during comparison
     * @param criteria List of criteria used for comparison
     * @return A new ComparisonResult instance
     */
    public static ComparisonResult withCriteria(boolean areEqual, List<DifferenceInfo> differences, List<? extends PdfComparisonCriteria> criteria) {
        List<String> criteriaInfo = criteria.stream()
            .map(c -> {
                if (c instanceof TextContentComparisonCriteria) {
                    boolean ignoreSpacing = ((TextContentComparisonCriteria) c).isIgnoreSpacingDifferences();
                    return String.format("%s (ignoreSpacing: %b)", 
                        c.getClass().getSimpleName(), 
                        ignoreSpacing);
                }
                return c.getClass().getSimpleName();
            })
            .collect(Collectors.toList());
        return new ComparisonResult(areEqual, differences, criteriaInfo);
    }
}