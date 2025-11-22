package com.safe.room.file.tool.app;

import com.safe.room.file.tool.comparator.FileComparator;
import com.safe.room.file.tool.comparator.FileComparatorFactory;
import com.safe.room.file.tool.comparator.pdf.dto.ComparisonResult;
import com.safe.room.file.tool.comparator.report.FileCompareReportGenerator;

import java.io.File;
import java.io.IOException;

/**
 * Command-line application for comparing two files using the appropriate comparator.
 * The application determines the correct comparator based on file types and displays
 * detailed comparison results.
 * 
 * <p>Usage: {@code java -jar file-comparator.jar <file1> <file2>}</p>
 * 
 * <p>Example: {@code java -jar file-comparator.jar document1.pdf document2.pdf}</p>
 */
public class FileComparatorApp {
    
    // Exit codes
    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_INVALID_ARGS = 1;
    private static final int EXIT_FILE1_INVALID = 2;
    private static final int EXIT_FILE2_INVALID = 3;
    private static final int EXIT_IO_ERROR = 5;
    private static final int EXIT_UNEXPECTED_ERROR = 6;
    private static final String DEFAULT_REPORT_DIR = ".";
    
    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments: file1 file2 [outputDir]
     *             file1: First file to compare
     *             file2: Second file to compare
     *             outputDir: (Optional) Directory where to save the report (default: current directory)
     */
    public static void main(String[] args) {
        try {
            run(args);
            System.exit(EXIT_SUCCESS);
        } catch (ApplicationException e) {
            System.err.println("Error: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            System.exit(e.getExitCode());
        }
    }
    
    /**
     * Main application logic.
     * 
     * @param args Command line arguments
     * @throws ApplicationException If an error occurs during execution
     */
    private static void run(String[] args) throws ApplicationException {
        validateArguments(args);
        
        File file1 = validateAndGetFile(args[0], "First", EXIT_FILE1_INVALID);
        File file2 = validateAndGetFile(args[1], "Second", EXIT_FILE2_INVALID);
        
        // Get output directory (default to current directory if not specified)
        String outputDir = args.length > 2 ? args[2] : DEFAULT_REPORT_DIR;
        File outputDirFile = new File(outputDir);
        
        // Create output directory if it doesn't exist
        if (!outputDirFile.exists() && !outputDirFile.mkdirs()) {
            throw new ApplicationException(
                "Failed to create output directory: " + outputDirFile.getAbsolutePath(),
                EXIT_IO_ERROR);
        }
        
        if (!outputDirFile.isDirectory()) {
            throw new ApplicationException(
                "Specified output path is not a directory: " + outputDirFile.getAbsolutePath(),
                EXIT_IO_ERROR);
        }
        
        try {
            FileComparator comparator = getFileComparator(file1, file2);
            printFileInfo(file1, file2, comparator);
            
            ComparisonResult result = compareFiles(comparator, file1, file2);
            printComparisonResults(result, file1, file2, outputDirFile);
            
        } catch (IOException e) {
            throw new ApplicationException("Error reading files: " + e.getMessage(), e, EXIT_IO_ERROR);
        } catch (Exception e) {
            throw new ApplicationException("An unexpected error occurred: " + e.getMessage(), e, EXIT_UNEXPECTED_ERROR);
        }
    }
    
    /**
     * Validates command line arguments.
     * 
     * @param args Command line arguments
     * @throws ApplicationException If arguments are invalid
     */
    private static void validateArguments(String[] args) throws ApplicationException {
        if (args.length < 2 || args.length > 3) {
            printUsage();
            throw new ApplicationException("Invalid number of arguments", EXIT_INVALID_ARGS);
        }
    }
    
    /**
     * Validates that a file exists and is readable.
     * 
     * @param filePath Path to the file
     * @param fileLabel Label for the file (used in error messages)
     * @param errorExitCode Exit code to use if validation fails
     * @return The validated File object
     * @throws ApplicationException If validation fails
     */
    private static File validateAndGetFile(String filePath, String fileLabel, int errorExitCode) 
            throws ApplicationException {
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new ApplicationException(
                String.format("%s file does not exist: %s", fileLabel, filePath), 
                errorExitCode);
        }
        
        if (!file.isFile()) {
            throw new ApplicationException(
                String.format("%s path is not a file: %s", fileLabel, filePath), 
                errorExitCode);
        }
        
        if (!file.canRead()) {
            throw new ApplicationException(
                String.format("Cannot read %s file: %s", fileLabel.toLowerCase(), filePath), 
                errorExitCode);
        }
        
        return file;
    }
    
    /**
     * Gets the appropriate comparator for the given files.
     * 
     * @param file1 First file to compare
     * @param file2 Second file to compare
     * @return The appropriate FileComparator
     * @throws ApplicationException If no suitable comparator is found
     */
    private static FileComparator getFileComparator(File file1, File file2) throws ApplicationException {
        try {
            return FileComparatorFactory.getComparator(file1, file2);
        } catch (Exception e) {
            throw new ApplicationException(
                String.format("Cannot compare files: %s", e.getMessage()), 
                e, 
                EXIT_UNEXPECTED_ERROR);
        }
    }
    
    /**
     * Prints information about the files being compared and the comparator being used.
     * 
     * @param file1 First file
     * @param file2 Second file
     * @param comparator Comparator being used
     */
    private static void printFileInfo(File file1, File file2, FileComparator comparator) {
        System.out.println("Using comparator: " + comparator.getName());
        System.out.println(comparator.getDescription());
        
        System.out.println("\nComparing files:");
        System.out.println("File 1: " + file1.getAbsolutePath());
        System.out.println("File 2: " + file2.getAbsolutePath() + "\n");
    }
    
    /**
     * Performs the file comparison.
     * 
     * @param comparator Comparator to use
     * @param file1 First file
     * @param file2 Second file
     * @return Comparison result
     * @throws IOException If an I/O error occurs
     */
    private static ComparisonResult compareFiles(FileComparator comparator, File file1, File file2) 
            throws IOException {
        long startTime = System.currentTimeMillis();
        ComparisonResult result = comparator.compare(file1, file2);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Comparison completed in " + (endTime - startTime) + " ms");
        return result;
    }
    
    /**
     * Prints the comparison results.
     * 
     * @param result Comparison result to display
     */
    /**
     * Prints the comparison results to console and generates a report file.
     * 
     * @param result Comparison result to display
     * Prints the comparison results to the console and provides a clickable link to the report.
     * The link will work in most modern terminals that support ANSI escape codes.
     * 
     * @param result Comparison result to display
     * @param file1 First file that was compared
     * @param file2 Second file that was compared
     */
    private static final FileCompareReportGenerator FILE_COMPARE_REPORT_GENERATOR = new FileCompareReportGenerator();
    
    /**
     * Prints the comparison results to the console and generates a report file.
     * The link will work in most modern terminals that support ANSI escape codes.
     * 
     * @param result Comparison result to display
     * @param file1 First file that was compared
     * @param file2 Second file that was compared
     * @param outputDir Directory where to save the report
     */
    private static void printComparisonResults(ComparisonResult result, File file1, File file2, File outputDir) {
        String resultStatus = result.areEqual() ? "EQUAL" : "DIFFERENT";
        System.out.println("Files are " + resultStatus);
        
        if (!result.areEqual() && !result.differences().isEmpty()) {
            int diffCount = result.differences().size();
            System.out.println("\nDifferences found (" + diffCount + "):");
            for (int i = 0; i < diffCount; i++) {
                System.out.printf("%d. %s%n", (i + 1), result.differences().get(i).description());
            }
        }
        
        // Generate and save the report
        try {
            // Create a timestamped report filename
            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String reportFilename = String.format("comparison_report_%s.html", timestamp);
            File reportFile = new File(outputDir, reportFilename);
            
            // Generate the report in the specified directory
            FILE_COMPARE_REPORT_GENERATOR.generateReport(result, file1, file2, reportFile);
            String reportPath = reportFile.getAbsolutePath();
            
            // Create a clickable link that works in most terminals
            String os = System.getProperty("os.name").toLowerCase();
            String clickableText;
            
            if (os.contains("win")) {
                // For Windows, use a format that works with Windows Terminal and CMD
                String winPath = reportPath.replace("\\", "\\\\");
                clickableText = String.format("file://%s", winPath);
                System.out.println("\nReport generated: " + clickableText);
                System.out.println("  Right-click the link above and select 'Open Link' or copy the path below:");
            } else {
                // For Unix-like systems
                clickableText = String.format("file://%s", reportPath);
                System.out.println("\nReport generated: " + clickableText);
                System.out.println("  Click the link above or use this path in your browser:");
            }
            
            System.out.println("  " + reportPath);
            
            // For Windows, try to open the file directly
            if (os.contains("win")) {
                try {
                    Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "", reportPath});
                } catch (IOException e) {
                    // If direct open fails, just continue
                }
            }
            
        } catch (IOException e) {
            System.err.println("Warning: Failed to generate report file: " + e.getMessage());
        }
    }
    
    /**
     * Prints usage instructions to standard output.
     */
    private static void printUsage() {
        System.out.println("File Comparator Tool");
        System.out.println("-------------------");
        System.out.println("Compares two files using the appropriate comparator based on file types.");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar file-comparator.jar <file1> <file2> [outputDir]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  file1      First file to compare");
        System.out.println("  file2      Second file to compare");
        System.out.println("  outputDir  (Optional) Directory where to save the report (default: current directory)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar file-comparator.jar document1.pdf document2.pdf");
        System.out.println("  java -jar file-comparator.jar doc1.pdf doc2.pdf ./reports");
    }
    
    /**
     * Custom exception for application-specific errors.
     */
    private static class ApplicationException extends Exception {
        private final int exitCode;
        
        public ApplicationException(String message, int exitCode) {
            super(message);
            this.exitCode = exitCode;
        }
        
        public ApplicationException(String message, Throwable cause, int exitCode) {
            super(message, cause);
            this.exitCode = exitCode;
        }
        
        public int getExitCode() {
            return exitCode;
        }
    }
}
