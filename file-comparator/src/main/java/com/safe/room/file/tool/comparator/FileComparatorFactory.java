package com.safe.room.file.tool.comparator;

import com.safe.room.file.tool.comparator.exception.UnsupportedFileTypeException;
import com.safe.room.file.tool.comparator.pdf.PdfComparator;
import com.safe.room.file.tool.comparator.pdf.PdfComparisonConfig;
import com.safe.room.file.tool.comparator.pdf.criteria.TextContentComparisonCriteria;

import java.io.File;
import java.util.*;

/**
 * Factory class for creating file comparators based on file types.
 * Supports finding appropriate comparators for pairs of files.
 */
public class FileComparatorFactory {

    private static final Map<FileCompareType, FileComparator> COMPARATOR_REGISTRY = new HashMap<>();
    private static final Set<FileComparator> ALL_COMPARATORS = new LinkedHashSet<>();
    
    static {
        // Register default comparators
        registerComparatorPair("pdf", "pdf",
                new PdfComparator(PdfComparisonConfig.builder()
                        .withCriteria(new TextContentComparisonCriteria(true))
                        .build())
        );
    }
    
    private FileComparatorFactory() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Registers a comparator for a specific pair of file extensions.
     * 
     * @param ext1 the first file extension (without dot, case-insensitive)
     * @param ext2 the second file extension (without dot, case-insensitive)
     * @param comparator the comparator to use for this file type combination
     * @throws IllegalArgumentException if any parameter is null/empty
     */
    public static void registerComparatorPair(String ext1, String ext2, FileComparator comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null");
        }
        
        FileCompareType type = new FileCompareType(ext1, ext2);
        COMPARATOR_REGISTRY.put(type, comparator);
        ALL_COMPARATORS.add(comparator);
    }
    
    /**
     * Gets all registered comparators.
     * 
     * @return an unmodifiable set of all registered comparators
     */
    public static Set<FileComparator> getAllComparators() {
        return Collections.unmodifiableSet(ALL_COMPARATORS);
    }
    
    /**
     * Gets the appropriate comparator for the given files based on their types.
     * 
     * @param file1 the first file to compare
     * @param file2 the second file to compare
     * @return the appropriate comparator that supports both file types
     * @throws UnsupportedFileTypeException if no suitable comparator is found for the file types
     * @throws IllegalArgumentException if either file is null, doesn't exist, or has no extension
     */
    public static FileComparator getComparator(File file1, File file2) throws UnsupportedFileTypeException {
        validateFile(file1, "First");
        validateFile(file2, "Second");
        
        // First try to find a comparator that explicitly supports both files
        for (FileComparator comparator : ALL_COMPARATORS) {
            if (comparator.supports(file1, file2)) {
                return comparator;
            }
        }
        
        // If no direct match, try to find a comparator that handles the file extensions
        String ext1 = getFileExtension(file1);
        String ext2 = getFileExtension(file2);
        
        if (ext1 != null && ext2 != null) {
            FileCompareType type = new FileCompareType(ext1, ext2);
            FileComparator comparator = COMPARATOR_REGISTRY.get(type);
            if (comparator != null) {
                return comparator;
            }
        }
        
        throw new UnsupportedFileTypeException(
            String.format("No suitable comparator found for file types: %s and %s", 
                ext1, ext2));
    }
    
    /**
     * Validates that a file is not null, exists, and has an extension.
     * 
     * @param file the file to validate
     * @param fileLabel label to use in error messages (e.g., "First", "Second")
     * @throws IllegalArgumentException if the file is invalid
     */
    private static void validateFile(File file, String fileLabel) {
        if (file == null) {
            throw new IllegalArgumentException(fileLabel + " file cannot be null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException(fileLabel + " file does not exist: " + file.getAbsolutePath());
        }
        if (getFileExtension(file) == null) {
            throw new IllegalArgumentException(fileLabel + " file has no extension: " + file.getAbsolutePath());
        }
    }
    
    /**
     * Gets the file extension in lowercase without the dot.
     * 
     * @param file the file to get the extension from
     * @return the file extension in lowercase, or null if no extension
     */
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }
    
    /**
     * Creates a new instance of the default PDF comparator.
     * 
     * @return a new PDF comparator instance
     */
    public static PdfComparator createPdfComparator() {
        return new PdfComparator();
    }
}
