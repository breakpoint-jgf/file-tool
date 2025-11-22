# File Comparator Tool

A powerful Java-based tool for comparing files with support for various comparison criteria, with a special focus on PDF comparison.

## Features

- **PDF Comparison**
  - Text content comparison with whitespace handling
  - Metadata comparison
  - Page count verification
  - File size comparison

- **Flexible Comparison Criteria**
  - Configurable comparison settings
  - Support for custom comparison strategies
  - Detailed difference reporting

- **User-Friendly Reports**
  - HTML-based comparison reports
  - Clear visualization of differences
  - Detailed difference information

## Getting Started

### Prerequisites

- Java 21 (JDK 21) or higher
- Maven 3.6 or higher
- (For development) PDFBox 2.0 or higher

### Installation

1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd file-tool/file-comparator
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

## Usage

### Command Line Interface

The easiest way to use the file comparator is through the command line interface:

```bash
# Basic usage
java -jar file-comparator.jar <file1> <file2> [outputDir]

# Example: Compare two PDF files
java -jar file-comparator.jar document1.pdf document2.pdf

# Example: Compare files and save report to specific directory
java -jar file-comparator.jar doc1.pdf doc2.pdf ./reports
```

### Programmatic Usage

You can also use the file comparator programmatically in your Java code:

```java
import com.safe.room.file.tool.app.FileComparatorApp;

public class MyApp {
    public static void main(String[] args) {
        // The main method handles all the logic
        FileComparatorApp.main(new String[]{
            "path/to/first.pdf",
            "path/to/second.pdf",
            "./output"  // Optional output directory
        });
    }
}
```

### Using the FileComparator Directly

For more control, you can use the FileComparator directly:

```java
import com.safe.room.file.tool.comparator.FileComparatorFactory;
import com.safe.room.file.tool.comparator.pdf.PdfComparisonConfig;
import com.safe.room.file.tool.comparator.pdf.criteria.TextContentComparisonCriteria;

// Get the appropriate comparator for your files
FileComparator comparator = FileComparatorFactory.getComparator(file1, file2);

// Or create a specific comparator with custom configuration
PdfComparisonConfig config = PdfComparisonConfig.builder()
    .withCriteria(new TextContentComparisonCriteria(true)) // Enable whitespace ignoring
    .build();

// Compare files
ComparisonResult result = comparator.compare(file1, file2);

// Check if files are equal
boolean areEqual = result.areEqual();

// Get list of differences
List<DifferenceInfo> differences = result.differences();
```

## Report Generation

The tool automatically generates detailed HTML reports showing:

- File information (paths, sizes, last modified dates)
- Comparison criteria used
- List of differences found with severity levels
- Visual representation of text differences

Reports are saved as HTML files with timestamps in the specified output directory (or current directory if not specified).

## Configuration Options

### Text Content Comparison

```java
TextContentComparisonCriteria textCriteria = new TextContentComparisonCriteria()
    .setIgnoreSpacingDifferences(true);  // Ignore whitespace differences
```

## Creating Custom Comparison Criteria

You can create custom comparison criteria by implementing the `PdfComparisonCriteria` interface or extending the `AbstractPdfComparisonCriteria` class.

### Example: Custom Criteria Implementation

Here's how to create a custom comparison criteria that checks for specific conditions:

```java
import com.safe.room.file.tool.comparator.pdf.criteria.AbstractPdfComparisonCriteria;
import com.safe.room.file.tool.comparator.pdf.dto.DifferenceInfo;
import com.safe.room.file.tool.comparator.pdf.enumeration.DifferenceType;
import com.safe.room.file.tool.comparator.pdf.enumeration.Severity;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.util.List;

public class CustomPdfCriteria extends AbstractPdfComparisonCriteria {
    
    private final String customParameter;
    
    public CustomPdfCriteria(String customParameter) {
        super(DifferenceType.CUSTOM, Severity.WARNING);
        this.customParameter = customParameter;
    }
    
    @Override
    public boolean compare(File file1, File file2, PDDocument doc1, PDDocument doc2, 
                         List<DifferenceInfo> differences) {
        boolean areEqual = true;
        
        try {
            // Your custom comparison logic here
            // Example: Check if both PDFs have the same number of pages
            if (doc1.getNumberOfPages() != doc2.getNumberOfPages()) {
                // Add difference if condition is not met
                differences.add(new DifferenceInfo(
                    "Page count differs: " + 
                    doc1.getNumberOfPages() + " vs " + doc2.getNumberOfPages(),
                    getType(),
                    getSeverity()
                ));
                areEqual = false;
            }
            
            // Add more custom checks as needed
            
        } catch (Exception e) {
            // Handle exceptions and add them as differences
            differences.add(new DifferenceInfo(
                "Error during custom comparison: " + e.getMessage(),
                getType(),
                Severity.ERROR
            ));
            return false;
        }
        
        return areEqual;
    }
}
```

### Creating a Custom Difference Template

For each custom criteria, you should create a corresponding FTL template to render its differences in the report. Here's how to create a custom template:

1. Create a new FTL file in `src/main/resources/templates/comparator/differences/` with a descriptive name (e.g., `CustomDifference.ftl`)

```html
<#-- CustomDifference.ftl -->
<#-- @ftlvariable name="description" type="com.safe.room.file.tool.comparator.pdf.dto.descriptions.CustomDifference" -->

<div class="custom-difference">
    <h4>Custom Difference Detected</h4>
    
    <#-- Display custom parameter value -->
    <div class="custom-param">
        <strong>Custom Parameter:</strong> ${description.customParameter}
    </div>
    
    <#-- Example of conditional display -->
    <#if description.someCondition()>
        <div class="custom-condition">
            Custom condition is met
        </div>
    </#if>
    
    <#-- Example of iterating through a list of items -->
    <#if description.items?? && description.items?has_content>
        <div class="custom-items">
            <strong>Items:</strong>
            <ul>
                <#list description.items as item>
                    <li>${item}</li>
                </#list>
            </ul>
        </div>
    </#if>
</div>

<style>
    .custom-difference {
        padding: 10px;
        border-left: 3px solid #6c5ce7;
        background-color: #f8f9fa;
        margin: 10px 0;
    }
    
    .custom-param {
        margin: 8px 0;
        padding: 5px;
        background-color: #f1f2f6;
        border-radius: 4px;
    }
    
    .custom-items {
        margin-top: 10px;
    }
    
    .custom-items ul {
        margin: 5px 0 0 20px;
        padding: 0;
    }
</style>
```

### Registering Custom Criteria

To use your custom criteria, register it with the `PdfComparisonConfig`:

```java
// Create configuration with custom criteria
PdfComparisonConfig config = PdfComparisonConfig.builder()
    .withCriteria(new CustomPdfCriteria("custom-value"))
    .build();

// Create comparator with the configuration
PdfComparator comparator = new PdfComparator(config);

// Compare files
ComparisonResult result = comparator.compare(file1, file2);

// The template will be automatically used based on the DifferenceType
// and the description's templateName() method
```

### Required Methods in Your Difference Description Class

Your custom difference description class should implement `DifferenceDescription` and include:

```java
public class CustomDifference implements DifferenceDescription {
    // Your fields and methods
    
    @Override
    public String templateName() {
        // This should match the FTL filename without the .ftl extension
        return "CustomDifference";
    }
    
    @Override
    public String getDescription() {
        // Return a brief description of the difference
        return "Custom difference found: " + customParameter;
    }
}

## Building from Source

1. Clone the repository
2. Build with Maven:
   ```bash
   mvn clean package
   ```
3. Find the executable JAR in the `target` directory

