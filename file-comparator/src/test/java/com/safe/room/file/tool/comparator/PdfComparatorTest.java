package com.safe.room.file.tool.comparator;

import com.safe.room.file.tool.comparator.pdf.PdfComparator;
import com.safe.room.file.tool.comparator.pdf.PdfComparisonConfig;
import com.safe.room.file.tool.comparator.pdf.dto.ComparisonResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

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
    void compare_withSizeComparisonOnly_detectsSizeDifferences() throws Exception {
        PdfComparisonConfig config = PdfComparisonConfig.builder()
                .withSizeComparison()
                .build();
        
        PdfComparator comparator = new PdfComparator(config);
        ComparisonResult result = comparator.compare(testPdf1, testPdf2);
        
        assertEquals(1, result.differences().size());
        assertEquals("SIZE", result.differences().get(0).type());
    }
    
    @Test
    void compare_withCustomCriteria_usesProvidedCriteria() throws Exception {
        // Create a test file with a different page count using PDFBox
        File testPdf4 = tempDir.resolve("test4.pdf").toFile();
        
        // Create a new PDF with 2 pages using PDFBox
        try (PDDocument document = new PDDocument()) {
            // First page
            document.addPage(new PDPage());
            // Second page
            document.addPage(new PDPage());
            document.save(testPdf4);
        }
        
        PdfComparisonConfig config = PdfComparisonConfig.builder()
                .withCriteria(new PageCountComparisonCriteria())
                .withCriteria(new SizeComparisonCriteria())
                .build();
        
        PdfComparator comparator = new PdfComparator(config);
        
        // Compare with a file that has a different page count and size
        ComparisonResult result = comparator.compare(testPdf1, testPdf4);
        
        // Should have differences for both size and page count
        assertEquals(2, result.differences().size(), 
            "Expected 2 differences (size and page count), but got: " + result.differences());
            
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
