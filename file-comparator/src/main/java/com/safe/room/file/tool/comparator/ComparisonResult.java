package com.safe.room.file.tool.comparator;

/**
 * Represents the result of a file comparison operation.
 */
public class ComparisonResult {
    private final boolean identical;
    private final String differences;
    private final String message;

    /**
     * Creates a new ComparisonResult.
     *
     * @param identical   whether the files are identical
     * @param differences a description of the differences found (can be empty if identical)
     * @param message     additional information about the comparison
     */
    public ComparisonResult(boolean identical, String differences, String message) {
        this.identical = identical;
        this.differences = differences != null ? differences : "";
        this.message = message != null ? message : "";
    }

    /**
     * Creates a new ComparisonResult for identical files.
     *
     * @return a ComparisonResult indicating the files are identical
     */
    public static ComparisonResult identical() {
        return new ComparisonResult(true, "", "Files are identical");
    }

    /**
     * Creates a new ComparisonResult for different files.
     *
     * @param differences a description of the differences found
     * @return a ComparisonResult indicating the files are different
     */
    public static ComparisonResult different(String differences) {
        return new ComparisonResult(false, differences, "Files are different");
    }

    /**
     * @return true if the files are identical, false otherwise
     */
    public boolean areIdentical() {
        return identical;
    }

    /**
     * @return a description of the differences between the files
     */
    public String getDifferences() {
        return differences;
    }

    /**
     * @return additional information about the comparison
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        if (identical) {
            return "ComparisonResult{identical=true, message='" + message + "'}";
        } else {
            return "ComparisonResult{identical=false, differences='" + differences + "', message='" + message + "'}";
        }
    }
}
