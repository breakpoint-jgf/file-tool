package com.safe.room.file.tool.comparator.pdf;

import com.safe.room.file.tool.comparator.FileComparator;
import com.safe.room.file.tool.comparator.pdf.criteria.PdfComparisonCriteria;
import com.safe.room.file.tool.comparator.pdf.dto.ComparisonResult;
import com.safe.room.file.tool.comparator.pdf.dto.DifferenceInfo;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of FileComparator for comparing PDF files.
 * Uses a flexible criteria-based system for comparing different aspects of PDF files.
 * <p>
 * The comparison is performed by executing a series of comparison criteria
 * that can be configured using {@link PdfComparisonConfig}.
 */
public class PdfComparator implements FileComparator {

    private static final String PDF_EXTENSION = ".pdf";
    private static final String COMPARATOR_NAME = "PDF Comparator";
    private static final String COMPARATOR_DESCRIPTION = 
        "Compares PDF files using a flexible criteria-based comparison system";
    
    private final PdfComparisonConfig config;
    
    /**
     * Creates a new PdfComparator with default configuration (all comparisons enabled).
     */
    public PdfComparator() {
        this(PdfComparisonConfig.allEnabled());
    }
    
    /**
     * Creates a new PdfComparator with the specified configuration.
     *
     * @param config the configuration for this comparator, must not be null
     * @throws NullPointerException if config is null
     */
    public PdfComparator(PdfComparisonConfig config) {
        this.config = Objects.requireNonNull(config, "Config cannot be null");
    }
    
    /**
     * Creates a new PdfComparator with the specified configuration builder.
     *
     * @param builder the builder for the configuration, must not be null
     * @throws NullPointerException if builder is null
     */
    public PdfComparator(PdfComparisonConfig.Builder builder) {
        this(Objects.requireNonNull(builder, "Builder cannot be null").build());
    }

    @Override
    public ComparisonResult compare(File file1, File file2) throws IOException {
        validateFiles(file1, file2);
        final List<DifferenceInfo> differences = new ArrayList<>();
        boolean areEqual = true;

        try (PDDocument doc1 = Loader.loadPDF(file1);
             PDDocument doc2 = Loader.loadPDF(file2)) {
            
            for (PdfComparisonCriteria criteria : config.getCriteria()) {
                boolean criteriaResult = criteria.compare(file1, file2, doc1, doc2, differences);
                areEqual = areEqual && criteriaResult;
            }
        } catch (Exception e) {
            throw new IOException("Error comparing PDF files: " + e.getMessage(), e);
        }

        return new ComparisonResult(areEqual, differences);
    }

    @Override
    public boolean supports(File file1, File file2) {
        return file1 != null && file2 != null &&
               file1.getName().toLowerCase().endsWith(PDF_EXTENSION) &&
               file2.getName().toLowerCase().endsWith(PDF_EXTENSION);
    }

    @Override
    public String getName() {
        return COMPARATOR_NAME;
    }

    @Override
    public String getDescription() {
        return COMPARATOR_DESCRIPTION;
    }

    /**
     * Validates that the input files are not null and exist.
     *
     * @param file1 the first file to validate
     * @param file2 the second file to validate
     * @throws IllegalArgumentException if either file is null
     * @throws IOException if either file does not exist
     */
    private void validateFiles(File file1, File file2) throws IOException {
        if (file1 == null || file2 == null) {
            throw new IllegalArgumentException("Both files must not be null");
        }
        if (!file1.exists()) {
            throw new IOException("File not found: " + file1.getAbsolutePath());
        }
        if (!file2.exists()) {
            throw new IOException("File not found: " + file2.getAbsolutePath());
        }
    }
}
