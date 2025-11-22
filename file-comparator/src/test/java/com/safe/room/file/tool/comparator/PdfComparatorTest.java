package com.safe.room.file.tool.comparator;

import com.safe.room.file.tool.comparator.pdf.PdfComparator;
import com.safe.room.file.tool.comparator.pdf.PdfComparisonConfig;
import com.safe.room.file.tool.comparator.pdf.criteria.TextContentComparisonCriteria;
import com.safe.room.file.tool.comparator.pdf.dto.ComparisonResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfComparatorTest {

    @TempDir
    Path tempDir;
    
    private File testPdf1;
    private File testPdf2;
    private File testPdf3;
    
    @BeforeEach
    void setUp() throws IOException {
        // Create test PDF files
        testPdf1 = createTempPdfFile("test1.pdf", "This is test PDF 1");
        testPdf2 = createTempPdfFile("test2.pdf", "This is test PDF 2");
        testPdf3 = createTempPdfFile("test3.pdf", "This is test PDF 1"); // Same content as testPdf1
    }
    
    @Test
    void compare_identicalFiles_returnsTrue() throws Exception {
        PdfComparator comparator = new PdfComparator();
        ComparisonResult result = comparator.compare(testPdf1, testPdf1);
        
        assertTrue(result.areEqual());
        assertTrue(result.differences().isEmpty());
    }
    
    @Test
    void compare_differentContent_returnsFalse() throws Exception {
        PdfComparator comparator = new PdfComparator();
        ComparisonResult result = comparator.compare(testPdf1, testPdf2);
        
        assertFalse(result.areEqual());
        assertFalse(result.differences().isEmpty());
    }
    
    @Test
    void compare_sameContentDifferentFiles_returnsTrue() throws Exception {
        PdfComparator comparator = new PdfComparator();
        ComparisonResult result = comparator.compare(testPdf1, testPdf3);
        
        assertTrue(result.areEqual());
        assertTrue(result.differences().isEmpty());
    }
    
    @Test
    void compare_withTextContentComparison_detectsContentDifferences() throws Exception {
        PdfComparisonConfig config = PdfComparisonConfig.builder()
                .withCriteria(new TextContentComparisonCriteria(false))
                .build();
        
        PdfComparator comparator = new PdfComparator(config);
        ComparisonResult result = comparator.compare(testPdf1, testPdf2);
        
        assertFalse(result.differences().isEmpty(), "Expected differences but found none");
        
        // Debug output to help diagnose the issue
        System.out.println("Found differences:");
        result.differences().forEach(diff -> 
            System.out.printf("- Type: %s, Description: %s%n", diff.type(), diff.description())
        );
        
        // Check for TEXT_CONTENT difference
        boolean hasTextContentDiff = result.differences().stream()
            .anyMatch(diff -> "TEXT_CONTENT".equals(diff.type().getValue()));
            
        assertTrue(hasTextContentDiff, 
            "Expected a difference of type TEXT_CONTENT but found: " + 
            result.differences().stream()
                .map(d -> String.format("%s (%s)", d.type(), d.description()))
                .collect(java.util.stream.Collectors.joining(", ")));
    }
    
    @Test
    void compare_withTextContentComparisonIgnoreSpacing_ignoresSpacingDifferences() throws Exception {
        // Create a test file with the same content but different spacing
        File testPdf4 = tempDir.resolve("test4.pdf").toFile();
        createTempPdfFile("test4.pdf", "This   is   test   PDF   1"); // Same content as testPdf1 but with extra spaces
        
        // Test with spacing differences not ignored
        PdfComparisonConfig config1 = PdfComparisonConfig.builder()
                .withCriteria(new TextContentComparisonCriteria(false)) // Don't ignore spacing
                .build();
        
        PdfComparator comparator1 = new PdfComparator(config1);
        ComparisonResult result1 = comparator1.compare(testPdf1, testPdf4);
        assertFalse(result1.areEqual());
        
        // Test with spacing differences ignored
        PdfComparisonConfig config2 = PdfComparisonConfig.builder()
                .withCriteria(new TextContentComparisonCriteria(true)) // Ignore spacing
                .build();
        
        PdfComparator comparator2 = new PdfComparator(config2);
        ComparisonResult result2 = comparator2.compare(testPdf1, testPdf4);
        assertTrue(result2.areEqual());
        
        // Clean up
        if (testPdf4.exists()) {
            testPdf4.delete();
        }
    }
    
    @Test
    void supports_pdfFiles_returnsTrue() {
        PdfComparator comparator = new PdfComparator();
        assertTrue(comparator.supports(new File("test.pdf"), new File("test2.pdf")));
    }
    
    @Test
    void supports_nonPdfFiles_returnsFalse() {
        PdfComparator comparator = new PdfComparator();
        assertFalse(comparator.supports(new File("test.txt"), new File("test2.txt")));
    }
    
    private File createTempPdfFile(String name, String content) throws IOException {
        File file = tempDir.resolve(name).toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("%PDF-1.4\n");
            writer.write("1 0 obj\n");
            writer.write("<< /Type /Catalog /Pages 2 0 R >>\n");
            writer.write("endobj\n");
            writer.write("2 0 obj\n");
            writer.write("<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n");
            writer.write("endobj\n");
            writer.write("3 0 obj\n");
            writer.write("<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\n");
            writer.write("endobj\n");
            writer.write("4 0 obj\n");
            writer.write("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n");
            writer.write("endobj\n");
            writer.write("5 0 obj\n");
            writer.write("<< /Length 44 >>\n");
            writer.write("stream\n");
            writer.write("BT\n");
            writer.write("/F1 24 Tf\n");
            writer.write("100 700 Td\n");
            writer.write("(" + content + ") Tj\n");
            writer.write("ET\n");
            writer.write("endstream\n");
            writer.write("endobj\n");
            
            // Add some additional content to make files have different sizes
            if (name.contains("test2.pdf")) {
                writer.write("6 0 obj\n");
                writer.write("<</Type/Page/Contents 7 0 R>>\n");
                writer.write("endobj\n");
                writer.write("7 0 obj\n");
                writer.write("<</Length 44>>\n");
                writer.write("stream\n");
                writer.write("BT\n");
                writer.write("/F1 24 Tf\n");
                writer.write("100 650 Td\n");
                writer.write("(Additional content for size difference) Tj\n");
                writer.write("ET\n");
                writer.write("endstream\n");
                writer.write("endobj\n");
                
                // Update the xref and trailer for the additional objects
                writer.write("xref\n");
                writer.write("0 8\n");
                writer.write("0000000000 65535 f \n");
                writer.write("0000000010 00000 n \n");
                writer.write("0000000079 00000 n \n");
                writer.write("0000000178 00000 n \n");
                writer.write("0000000306 00000 n \n");
                writer.write("0000000360 00000 n \n");
                writer.write("0000000414 00000 n \n");
                writer.write("0000000468 00000 n \n");
                writer.write("trailer\n");
                writer.write("<< /Size 8 /Root 1 0 R >>\n");
                writer.write("startxref\n");
                writer.write("600\n");
            } else {
                writer.write("xref\n");
                writer.write("0 6\n");
                writer.write("0000000000 65535 f \n");
                writer.write("0000000010 00000 n \n");
                writer.write("0000000079 00000 n \n");
                writer.write("0000000178 00000 n \n");
                writer.write("0000000306 00000 n \n");
                writer.write("0000000360 00000 n \n");
                writer.write("trailer\n");
                writer.write("<< /Size 6 /Root 1 0 R >>\n");
                writer.write("startxref\n");
                writer.write("472\n");
            }
            writer.write("%%EOF\n");
        }
        return file;
    }
}
