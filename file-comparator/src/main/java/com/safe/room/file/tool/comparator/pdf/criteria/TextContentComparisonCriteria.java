package com.safe.room.file.tool.comparator.pdf.criteria;

import com.safe.room.file.tool.comparator.pdf.dto.DifferenceInfo;
import com.safe.room.file.tool.comparator.pdf.dto.descriptions.TextContentDifference;
import com.safe.room.file.tool.comparator.pdf.enumeration.DifferenceType;
import com.safe.room.file.tool.comparator.pdf.enumeration.Severity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.StringsComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Compares PDF files based on their text content.
 * <p>
 * This criteria can be configured to ignore spacing differences between texts.
 */
public class TextContentComparisonCriteria extends AbstractPdfComparisonCriteria {
    
    private final boolean ignoreSpacingDifferences;
    
    /**
     * @return true if spacing differences are being ignored in text comparison
     */
    public boolean isIgnoreSpacingDifferences() {
        return ignoreSpacingDifferences;
    }
    
    /**
     * Creates a new TextContentComparisonCriteria that considers all differences (including spacing).
     */
    public TextContentComparisonCriteria() {
        this(false);
    }
    
    /**
     * Creates a new TextContentComparisonCriteria with the specified spacing difference handling.
     *
     * @param ignoreSpacingDifferences if true, differences in whitespace will be ignored
     */
    public TextContentComparisonCriteria(boolean ignoreSpacingDifferences) {
        super(DifferenceType.TEXT_CONTENT, Severity.ERROR);
        this.ignoreSpacingDifferences = ignoreSpacingDifferences;
    }
    
    @Override
    public boolean compare(File file1, File file2, PDDocument doc1, PDDocument doc2, List<DifferenceInfo> differences) {
        try {
            boolean hasDifferences = false;
            int pageCount = Math.min(doc1.getNumberOfPages(), doc2.getNumberOfPages());
            
            // Compare each page
            for (int i = 0; i < pageCount; i++) {
                final int currentPage = i + 1;
                String text1 = extractTextFromPage(doc1, currentPage);
                String text2 = extractTextFromPage(doc2, currentPage);
                
                if (!areTextsEqual(text1, text2)) {
                    String diffDetails = findTextDifferences(text1, text2, currentPage);
                    TextContentDifference textDiff = new TextContentDifference(diffDetails, currentPage);
                    addDifference(differences, textDiff, "Page Content", currentPage);
                    hasDifferences = true;
                }
            }
            
            // Check if one PDF has more pages than the other
            if (doc1.getNumberOfPages() != doc2.getNumberOfPages()) {
                String description = String.format("Page count differs: %d vs %d pages", 
                        doc1.getNumberOfPages(), doc2.getNumberOfPages());
                addDifference(differences, description, "Document", 0);
                hasDifferences = true;
            }
            
            return !hasDifferences;
            
        } catch (IOException e) {
            addDifference(differences, "Error extracting text: " + e.getMessage(), "Document");
            return false;
        }
    }
    
    /**
     * Compares two text strings, optionally ignoring spacing differences.
     *
     * @param text1 the first text to compare
     * @param text2 the second text to compare
     * @return true if the texts are considered equal according to the current configuration
     */
    private boolean areTextsEqual(String text1, String text2) {
        if (ignoreSpacingDifferences) {
            return normalizeWhitespace(text1).equals(normalizeWhitespace(text2));
        }
        return text1.equals(text2);
    }
    
    /**
     * Normalizes whitespace in the given text by replacing all sequences of whitespace characters
     * with a single space and trimming the result.
     *
     * @param text the text to normalize
     * @return the normalized text
     */
    private String normalizeWhitespace(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }
    
    private String extractTextFromPage(PDDocument document, int pageNumber) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);
        stripper.setWordSeparator(" ");
        stripper.setStartPage(pageNumber);
        stripper.setEndPage(pageNumber);
        return stripper.getText(document);
    }
    
    /**
     * Finds and formats the differences between two text strings in a tester-friendly way.
     * 
     * @param text1 the first text to compare (from PDF 1)
     * @param text2 the second text to compare (from PDF 2)
     * @param pageNumber the page number where the difference was found
     * @return a formatted string describing the differences in a clear, actionable way
     */
    private String findTextDifferences(String text1, String text2, int pageNumber) {
        if (ignoreSpacingDifferences) {
            String normalized1 = normalizeWhitespace(text1);
            String normalized2 = normalizeWhitespace(text2);
            if (normalized1.equals(normalized2)) {
                return "\n=== WHITESPACE DIFFERENCE ONLY ===\n\n" +
                       "The documents only differ in whitespace characters.\n" +
                       "Enable 'ignoreSpacingDifferences' to treat these as equal.\n";
            }
        }
        
        // Format the page number for display
        String pageInfo = " (Page " + pageNumber + ")";
        
        // Find the first difference
        int diffPos = 0;
        int minLength = Math.min(text1.length(), text2.length());
        while (diffPos < minLength && text1.charAt(diffPos) == text2.charAt(diffPos)) {
            diffPos++;
        }
        
        // Get context around the difference
        int contextStart = Math.max(0, diffPos - 15);
        int contextEnd1 = Math.min(text1.length(), diffPos + 25);
        int contextEnd2 = Math.min(text2.length(), diffPos + 25);
        
        // Extract the context with proper escaping
        String beforeContext = diffPos > 0 ? escapeForDisplay(text1.substring(contextStart, diffPos)) : "";
        String afterContext1 = escapeForDisplay(text1.substring(diffPos, Math.min(diffPos + 30, text1.length())));
        String afterContext2 = escapeForDisplay(text2.substring(diffPos, Math.min(diffPos + 30, text2.length())));
        
        // Build the diff output
        StringBuilder result = new StringBuilder();
        
        // Header with double newline after section
        result.append("\n=== TEXT DIFFERENCE DETECTED ===\n");
        result.append(String.format("Page: %d\n", pageNumber));
        result.append(String.format("Position: Character %d\n\n", diffPos));
        
        // Before/After sections with proper spacing
        result.append("BEFORE (PDF 1):\n");
        if (diffPos > 15) result.append("...");
        result.append(beforeContext);
        if (!afterContext1.isEmpty()) {
            result.append("[").append(afterContext1);
            if (diffPos + 30 < text1.length()) result.append("...");
            result.append("]");
        }
        result.append("\n\n");
        
        result.append("AFTER (PDF 2):\n");
        if (diffPos > 15) result.append("...");
        result.append(beforeContext);
        if (!afterContext2.isEmpty()) {
            result.append("[").append(afterContext2);
            if (diffPos + 30 < text2.length()) result.append("...");
            result.append("]");
        }
        
        // Add marker for difference position with spacing
        int markerPos = beforeContext.length() + (diffPos > 15 ? 3 : 0);
        result.append("\n").append(" ".repeat(markerPos)).append("^\n\n");
        
        // Diff details with section spacing
        result.append("DIFF DETAILS:\n");
        if (diffPos >= minLength) {
            // One string is a prefix of the other
            result.append("• Difference type: ");
            if (text1.length() > text2.length()) {
                result.append("Text removed\n");
                String missingText = escapeForDisplay(text1.substring(minLength, Math.min(minLength + 50, text1.length())));
                result.append("• Removed text: ").append(missingText);
                if (minLength + 50 < text1.length()) result.append("...");
            } else {
                result.append("Text added\n");
                String addedText = escapeForDisplay(text2.substring(minLength, Math.min(minLength + 50, text2.length())));
                result.append("• Added text: ").append(addedText);
                if (minLength + 50 < text2.length()) result.append("...");
            }
        } else {
            // Character difference
            char char1 = text1.charAt(diffPos);
            char char2 = text2.charAt(diffPos);
            result.append("• Difference type: ").append(
                Character.isWhitespace(char1) || Character.isWhitespace(char2) ? 
                "Whitespace difference" : "Character difference").append("\n");
            
            result.append(String.format("• At position %d (after \"%s\")\n", 
                diffPos, 
                escapeForDisplay(text1.substring(Math.max(0, diffPos - 10), diffPos))));
                
            // Show the actual differing parts
            int end1 = Math.min(text1.length(), diffPos + 20);
            int end2 = Math.min(text2.length(), diffPos + 20);
            result.append(String.format("• PDF 1: [%s]\n", escapeForDisplay(text1.substring(diffPos, end1))));
            result.append(String.format("• PDF 2: [%s]\n", escapeForDisplay(text2.substring(diffPos, end2))));
        }
        
        // Document metrics with consistent spacing
        result.append("\nDOCUMENT METRICS:\n");
        result.append(String.format("• PDF 1: %,d characters\n", text1.length()));
        result.append(String.format("• PDF 2: %,d characters\n", text2.length()));
        result.append(String.format("• Difference: %,d characters\n", 
            Math.abs(text1.length() - text2.length())));
        
        
        // Whitespace info with consistent spacing
        result.append("\nWHITESPACE DIFFERENCES: ");
        if (text1.trim().equals(text2.trim())) {
            result.append("Only in whitespace");
        } else {
            result.append("Content differs beyond just whitespace");
        }
        result.append("\n");
        
        return result.toString();
    }
    
    /**
     * Escapes special characters for better display in the console.
     */
    private String escapeChar(char c) {
        if (c == '\n') return "\\n";
        if (c == '\r') return "\\r";
        if (c == '\t') return "\\t";
        if (c < ' ' || c > '~') return String.format("\\u%04x", (int) c);
        return String.valueOf(c);
    }
    
    /**
     * Escapes special characters in the text for better display.
     */
    private String escapeSpecialChars(String text) {
        if (text == null) return "";
        return text.replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Prepares text for display in the diff output.
     * Preserves line breaks and tabs as natural whitespace.
     * Only escapes non-printable characters that aren't standard whitespace.
     * 
     * @param text The text to prepare for display
     * @return The text with only problematic characters escaped
     */
    private String escapeForDisplay(String text) {
        if (text == null) return "";
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Keep standard whitespace as-is
            if (c == '\n' || c == '\r' || c == '\t' || c == ' ') {
                result.append(c);
            } 
            // Keep standard printable ASCII
            else if (c >= ' ' && c <= '~') {
                result.append(c);
            }
            // Escape other control characters
            else if (c < ' ') {
                result.append(String.format("\\u%02x", (int) c));
            }
            // Keep other Unicode characters as-is
            else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
