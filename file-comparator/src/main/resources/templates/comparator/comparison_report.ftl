<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File Comparison Report</title>
    <style>
        :root {
            --color-primary: #2c3e50;
            --color-success: #28a745;
            --color-danger: #dc3545;
            --color-warning: #ffc107;
            --color-info: #17a2b8;
            --color-light: #f8f9fa;
            --color-dark: #343a40;
            --color-gray: #6c757d;
            --font-sans: 'Segoe UI', system-ui, -apple-system, sans-serif;
            --font-mono: 'Courier New', Courier, monospace;
            --border-radius: 4px;
            --box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        * { box-sizing: border-box; }
        
        body {
            font-family: var(--font-sans);
            line-height: 1.6;
            margin: 0;
            padding: 20px;
            color: #333;
            background-color: #f9f9f9;
        }
        
        .container { 
            max-width: 1200px; 
            margin: 0 auto; 
            background: white;
            padding: 20px 30px;
            border-radius: var(--border-radius);
            box-shadow: var(--box-shadow);
        }
        
        .header { 
            border-bottom: 2px solid var(--color-primary);
            padding-bottom: 15px;
            margin-bottom: 25px;
        }
        
        .header h1 {
            color: var(--color-primary);
            margin: 0 0 10px 0;
        }
        
        .result { 
            font-size: 1.1em; 
            margin: 25px 0; 
            padding: 15px; 
            border-radius: var(--border-radius);
            font-weight: 500;
        }
        
        .equal { 
            background-color: #e8f5e9; 
            color: #1b5e20; 
            border-left: 4px solid var(--color-success);
        }
        
        .different { 
            background-color: #ffebee; 
            color: #b71c1c; 
            border-left: 4px solid var(--color-danger);
        }
        
        .file-info { 
            margin: 20px 0; 
            padding: 15px; 
            background-color: var(--color-light); 
            border-radius: var(--border-radius);
            border-left: 4px solid var(--color-info);
        }
        
        .file-info h3 {
            margin-top: 0;
            color: var(--color-primary);
        }
        
        .differences { 
            margin: 30px 0;
        }
        
        .differences h3 {
            color: var(--color-primary);
            border-bottom: 1px solid #eee;
            padding-bottom: 8px;
        }
        
        .diff-container {
            margin: 15px 0 25px;
            border: 1px solid #e0e0e0;
            border-radius: var(--border-radius);
            overflow: hidden;
        }
        
        .diff-header {
            background-color: var(--color-primary);
            color: white;
            padding: 10px 15px;
            font-weight: 500;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .diff-section {
            display: flex;
            flex-wrap: wrap;
        }
        
        .diff-before, .diff-after {
            flex: 1;
            min-width: 300px;
            padding: 15px;
        }
        
        .diff-before {
            background-color: #fff5f5;
            border-right: 1px solid #e0e0e0;
        }
        
        .diff-after {
            background-color: #f5fff5;
        }
        
        .diff-before h4, .diff-after h4 {
            margin: 0 0 10px 0;
            padding-bottom: 5px;
            border-bottom: 1px solid #e0e0e0;
        }
        
        .diff-before h4 { color: var(--color-danger); }
        .diff-after h4 { color: var(--color-success); }
        
        .diff-content {
            font-family: var(--font-mono);
            white-space: pre-wrap;
            background: white;
            padding: 10px;
            border-radius: var(--border-radius);
            border: 1px solid #e0e0e0;
            overflow-x: auto;
            line-height: 1.5;
        }
        
        .diff-removed {
            background-color: #ffebee;
            text-decoration: line-through;
            color: #c62828;
            padding: 0 2px;
            border-radius: 2px;
        }
        
        .diff-added {
            background-color: #e8f5e9;
            color: #2e7d32;
            padding: 0 2px;
            border-radius: 2px;
        }
        
        .diff-details, .diff-metrics {
            background-color: #f8f9fa;
            padding: 15px;
            border-top: 1px solid #e0e0e0;
        }
        
        .diff-details h4, .diff-metrics h4 {
            margin-top: 0;
            color: var(--color-primary);
            font-size: 1.1em;
        }
        
        .diff-details ul {
            margin: 10px 0 0 0;
            padding-left: 20px;
        }
        
        .diff-details li {
            margin-bottom: 5px;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        
        th, td {
            padding: 8px 12px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }
        
        th {
            background-color: #f2f2f2;
            font-weight: 600;
        }
        
        .metadata {
            font-size: 0.9em;
            color: var(--color-gray);
            margin-top: 30px;
            padding-top: 15px;
            border-top: 1px solid #eee;
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
        }
        
        @media (max-width: 768px) {
            .diff-section {
                flex-direction: column;
            }
            
            .diff-before, .diff-after {
                width: 100%;
            }
            
            .diff-before {
                border-right: none;
                border-bottom: 1px solid #e0e0e0;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>File Comparison Report</h1>
        <p>Generated on: ${generationTime}</p>
    </div>

    <div class="result ${result.areEqual()?string('equal', 'different')}">
        <strong>Comparison Result:</strong> 
        <#if result.areEqual()>
            <span style="color: #155724;">IDENTICAL</span>
        <#else>
            <span style="color: #721c24;">DIFFERENT</span>
            <#if !result.differences()?has_content>
                <div>No differences found, but files are marked as different</div>
            <#else>
                <div>Differences found: ${result.differences()?size}</div>
            </#if>
        </#if>
    </div>

    <div class="file-info">
        <h3>Files Compared:</h3>
        <div><strong>File 1:</strong> ${file1.absolutePath} (${file1.length()} bytes, last modified: ${file1.lastModified()?number_to_datetime?string("yyyy-MM-dd HH:mm:ss")})</div>
        <div><strong>File 2:</strong> ${file2.absolutePath} (${file2.length()} bytes, last modified: ${file2.lastModified()?number_to_datetime?string("yyyy-MM-dd HH:mm:ss")})</div>
    </div>

    <#if !result.areEqual() && result.differences()?has_content>
        <div class="differences">
            <h3>Differences Found (${result.differences()?size}):</h3>
            <#list result.differences() as diff>
                    <#-- Parse the difference description into structured data -->
                    <#assign diffText = diff.description()>
                    <#if diffText?contains("BEFORE (PDF 1):")>
                    
                    <div class="diff-container">
                        <div class="diff-header">
                            <div>
                                <strong>Difference #${diff?counter}</strong>
                                <#if diff.pageNumber?? && diff.pageNumber?is_number && diff.pageNumber gt 0>
                                    <span style="margin-left: 10px;">Page ${diff.pageNumber}</span>
                                <#elseif diff.pageNumber?is_string && diff.pageNumber?has_content>
                                    <span style="margin-left: 10px;">Page ${diff.pageNumber}</span>
                                </#if>
                            </div>
                            <#if diff.timestamp??>
                                <span style="font-size: 0.9em; opacity: 0.9;">${diff.timestamp?datetime?string["yyyy-MM-dd HH:mm:ss"]}</span>
                            </#if>
                        </div>
                        
                        <#if diff.originalText?? || diff.modifiedText??>
                        <div class="diff-section">
                            <#if diff.originalText??>
                            <div class="diff-before">
                                <h4>Original Text</h4>
                                <div class="diff-content">
                                    ${diff.originalText?replace("\n", "<br>")?replace("\\n", "<br>")?replace("\\r", "")?replace("\\t", "    ")}
                                </div>
                            </div>
                            </#if>
                            
                            <#if diff.modifiedText??>
                            <div class="diff-after">
                                <h4>Modified Text</h4>
                                <div class="diff-content">
                                    ${diff.modifiedText?replace("\n", "<br>")?replace("\\n", "<br>")?replace("\\r", "")?replace("\\t", "    ")}
                                </div>
                            </div>
                            </#if>
                        </div>
                        </#if>
                        
                        <#if diff.details??>
                        <div class="diff-details">
                            <h4>Difference Details</h4>
                            <#if diff.details?is_string>
                                <div>${diff.details}</div>
                            <#elseif diff.details?is_sequence>
                                <#list diff.details as detail>
                                    <#if detail?is_hash>
                                        <div><strong>${detail.type!"Detail"}:</strong> ${detail.description!""}</div>
                                    <#else>
                                        <div>${detail?string}</div>
                                    </#if>
                                </#list>
                            <#else>
                                <div>${diff.details?string}</div>
                            </#if>
                        </div>
                        </#if>
                        
                        <#if diff.metrics??>
                        <div class="diff-metrics">
                            <h4>Document Metrics</h4>
                            <table>
                                <#if diff.metrics?is_hash>
                                    <#list diff.metrics as key, value>
                                        <tr>
                                            <td><strong>${key?capitalize}</strong></td>
                                            <td>${value?string}</td>
                                        </tr>
                                    </#list>
                                <#elseif diff.metrics?is_sequence>
                                    <#list diff.metrics as item>
                                        <tr>
                                            <td colspan="2">${item?string}</td>
                                        </tr>
                                    </#list>
                                <#else>
                                    <tr><td>${diff.metrics?string}</td></tr>
                                </#if>
                            </table>
                        </div>
                        </#if>
                    </div>
                <#else>
                    <div class="difference">
                        <strong>${diff?counter}.</strong> ${diff.description()}
                    </div>
                </#if>
            </#list>
        </div>
    </#if>

    <div class="metadata">
        <p>Generated by File Comparator Tool</p>
        <p>Version: 1.0</p>
    </div>
</div>
</body>
</html>
