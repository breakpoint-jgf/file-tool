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

### Registering Custom Criteria

To use your custom criteria, register it with the `PdfComparisonConfig`:

```java
PdfComparisonConfig config = PdfComparisonConfig.builder()
    .withCriteria(new CustomPdfCriteria("custom-value"))
    .build();

PdfComparator comparator = new PdfComparator(config);
```

## Building from Source

1. Clone the repository
2. Build with Maven:
   ```bash
   mvn clean package
   ```
3. Find the executable JAR in the `target` directory

