package com.safe.room.file.tool.comparator.pdf.criteria;

import com.safe.room.file.tool.comparator.pdf.dto.DifferenceInfo;
import com.safe.room.file.tool.comparator.pdf.enumeration.DifferenceType;
import com.safe.room.file.tool.comparator.pdf.enumeration.Severity;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.util.List;

/**
 * Interface for defining PDF comparison criteria.
 * Implementations of this interface define specific comparison logic.
 */
public interface PdfComparisonCriteria {
    
    /**
     * Compares two PDF documents based on specific criteria.
     *
     * @param file1 the first PDF file
     * @param file2 the second PDF file
     * @param doc1 the loaded first PDF document
     * @param doc2 the loaded second PDF document
     * @param differences list to add any differences found
     * @return true if the documents are equal according to this criteria, false otherwise
     */
    boolean compare(File file1, File file2, PDDocument doc1, PDDocument doc2, List<DifferenceInfo> differences);
    
    /**
     * @return the type of comparison this criteria performs
     */
    DifferenceType getType();
    
    /**
     * @return the severity level of differences found by this criteria
     */
    Severity getSeverity();
    
}
