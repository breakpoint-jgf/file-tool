package com.safe.room.file.tool.comparator.pdf.criteria;

import com.safe.room.file.tool.comparator.pdf.dto.DifferenceInfo;
import com.safe.room.file.tool.comparator.pdf.enumeration.DifferenceType;
import com.safe.room.file.tool.comparator.pdf.enumeration.Severity;

import java.util.List;

/**
 * Abstract base class for PDF comparison criteria implementations.
 * Provides common functionality for comparison criteria.
 */
public abstract class AbstractPdfComparisonCriteria implements PdfComparisonCriteria {
    
    private final DifferenceType type;
    private final Severity severity;
    
    /**
     * Creates a new comparison criteria with the specified type and severity.
     *
     * @param type the type of comparison this criteria performs
     * @param severity the severity level for differences found by this criteria
     */
    protected AbstractPdfComparisonCriteria(DifferenceType type, Severity severity) {
        this.type = type;
        this.severity = severity;
    }
    
    @Override
    public DifferenceType getType() {
        return type;
    }
    
    @Override
    public Severity getSeverity() {
        return severity;
    }
    
    /**
     * Helper method to add a difference to the differences list.
     * 
     * @param differences the list to add the difference to
     * @param message the description of the difference
     * @param location the location where the difference was found
     */
    protected void addDifference(List<DifferenceInfo> differences, String message, String location) {
        addDifference(differences, message, location, 0);
    }
    
    /**
     * Helper method to add a difference to the differences list with a specific page number.
     * 
     * @param differences the list to add the difference to
     * @param message the description of the difference
     * @param location the location where the difference was found
     * @param pageNumber the page number where the difference was found (0 if not applicable)
     */
    protected void addDifference(List<DifferenceInfo> differences, String message, String location, int pageNumber) {
        differences.add(new DifferenceInfo(
            type,
            message,
            location,
            severity,
            pageNumber
        ));
    }
}
