package com.safe.room.file.tool.comparator.report;

import com.safe.room.file.tool.comparator.pdf.dto.ComparisonResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates HTML reports for file comparison results using FreeMarker templates.
 */
public class FileCompareReportGenerator {
    private static final String TEMPLATE_DIR = "/templates/comparator/";
    private static final String TEMPLATE_NAME = "comparison_report.ftl";
    private static final String REPORT_PREFIX = "comparison_report_";
    private static final String REPORT_EXTENSION = ".html";
    
    private final Configuration freeMarkerConfig;
    
    public FileCompareReportGenerator() {
        this.freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        freeMarkerConfig.setClassForTemplateLoading(getClass(), "/");
        freeMarkerConfig.setDefaultEncoding(StandardCharsets.UTF_8.name());
        freeMarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freeMarkerConfig.setLogTemplateExceptions(false);
        freeMarkerConfig.setWrapUncheckedExceptions(true);
        freeMarkerConfig.setFallbackOnNullLoopVariable(false);
    }
    
    /**
     * Generates an HTML report for the comparison results.
     *
     * @param result The comparison result
     * @param file1  First file that was compared
     * @param file2  Second file that was compared
     * @return The generated report file
     * @throws IOException If an I/O error occurs during report generation
     */
    public File generateReport(ComparisonResult result, File file1, File file2) throws IOException {
        // Prepare the template data model
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("result", result);
        dataModel.put("file1", file1);
        dataModel.put("file2", file2);
        // Format the date as a string before passing to the template
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        dataModel.put("generationTime", formattedDate);
        
        // Create the report file
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File reportFile = new File(REPORT_PREFIX + timestamp + REPORT_EXTENSION);
        
        // Process the template and write to file
        try (Writer out = new FileWriter(reportFile, StandardCharsets.UTF_8)) {
            Template template = freeMarkerConfig.getTemplate(TEMPLATE_DIR + TEMPLATE_NAME);
            template.process(dataModel, out);
        } catch (TemplateException e) {
            throw new IOException("Error processing report template: " + e.getMessage(), e);
        }
        
        return reportFile;
    }
    
    /**
     * Generates a report with a custom output file name.
     *
     * @param result   The comparison result
     * @param file1    First file that was compared
     * @param file2    Second file that was compared
     * @param outputFile The output file to write the report to
     * @return The generated report file
     * @throws IOException If an I/O error occurs during report generation
     */
    public File generateReport(ComparisonResult result, File file1, File file2, File outputFile) throws IOException {
        // Ensure the output file has the correct extension
        if (!outputFile.getName().toLowerCase().endsWith(REPORT_EXTENSION)) {
            outputFile = new File(outputFile.getParent(), outputFile.getName() + REPORT_EXTENSION);
        }
        
        // Prepare the template data model
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("result", result);
        dataModel.put("file1", file1);
        dataModel.put("file2", file2);
        // Format the date as a string before passing to the template
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        dataModel.put("generationTime", formattedDate);
        
        // Process the template and write to file
        try (Writer out = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
            Template template = freeMarkerConfig.getTemplate(TEMPLATE_DIR + TEMPLATE_NAME);
            template.process(dataModel, out);
        } catch (TemplateException e) {
            throw new IOException("Error processing report template: " + e.getMessage(), e);
        }
        
        return outputFile;
    }
}
