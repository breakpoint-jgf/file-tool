package com.safe.room.file.tool.comparator.pdf.dto.descriptions;

import com.safe.room.file.tool.comparator.pdf.dto.DifferenceDescription;
import com.safe.room.file.tool.comparator.pdf.enumeration.DifferenceType;

/**
 * Describes a difference in text content between two PDFs.
 */
public class TextContentDifference implements DifferenceDescription {
    private final String diffDetails;
    private final int pageNumber;
    private final boolean ignoreSpacingDifferences;

    public TextContentDifference(String diffDetails, int pageNumber, boolean ignoreSpacingDifferences) {
        this.diffDetails = diffDetails;
        this.pageNumber = pageNumber;
        this.ignoreSpacingDifferences = ignoreSpacingDifferences;
    }

    @Override
    public String getDescription() {
        return String.format("Text content differs on page %d. %s", pageNumber, diffDetails);
    }

    @Override
    public DifferenceType getType() {
        return DifferenceType.TEXT_CONTENT;
    }

    public String getDiffDetails() {
        return diffDetails;
    }

    public int getPageNumber() {
        return pageNumber;
    }
    
    public boolean isIgnoreSpacingDifferences() {
        return ignoreSpacingDifferences;
    }
}
