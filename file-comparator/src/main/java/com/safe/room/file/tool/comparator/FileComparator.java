package com.safe.room.file.tool.comparator;

import com.safe.room.file.tool.comparator.pdf.dto.ComparisonResult;

import java.io.File;
import java.io.IOException;

/**
 * Interface for comparing files.
 * Implementations of this interface should provide specific comparison logic
 * for different types of files or comparison strategies.
 */
public interface FileComparator {

    /**
     * Compares two files and returns a result indicating their similarity or differences.
     *
     * @param file1 The first file to compare
     * @param file2 The second file to compare
     * @return A {@link ComparisonResult} object containing the comparison details
     * @throws IOException If an I/O error occurs while reading the files
     * @throws IllegalArgumentException If either file is null or doesn't exist
     */
    ComparisonResult compare(File file1, File file2) throws IOException;

    /**
     * Checks if this comparator supports the given file types.
     *
     * @param file1 The first file
     * @param file2 The second file
     * @return true if this comparator can compare the given files, false otherwise
     */
    boolean supports(File file1, File file2);

    /**
     * Gets the name of this comparator for display purposes.
     *
     * @return The name of the comparator
     */
    String getName();

    /**
     * Gets a description of what this comparator does.
     *
     * @return A description of the comparator
     */
    String getDescription();
}


