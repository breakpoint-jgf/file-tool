<#-- Text Content Difference Template -->
<#assign textDiff = diff.description()>
<#assign diffDetails = textDiff.getDiffDetails()!''>
<#assign pageNumber = textDiff.getPageNumber()!0>

<div class="text-diff">
    <div class="diff-header">
        <h4>Text Difference <#if pageNumber gt 0>- Page ${pageNumber}</#if></h4>
        <span class="criteria">Spacing differences: ${textDiff.isIgnoreSpacingDifferences()?string('ignored', 'included')}</span>
    </div>
    
    <div class="diff-content">
        <#if diffDetails?has_content>
            <pre>${diffDetails?html}</pre>
        <#else>
            <p>${textDiff.getDescription()?html}</p>
        </#if>
    </div>
    
    <#if pageNumber gt 0>
        <div class="diff-meta">
            <span>Page: ${pageNumber}</span>
        </div>
    </#if>
</div>

<style>
.text-diff {
    margin: 15px 0;
    border: 1px solid #e0e0e0;
    border-radius: 4px;
    overflow: hidden;
}

.diff-header {
    padding: 10px 15px;
    background-color: #f5f5f5;
    border-bottom: 1px solid #e0e0e0;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.diff-header h4 {
    margin: 0;
    font-size: 14px;
    font-weight: 600;
    color: #333;
}

.criteria {
    font-size: 12px;
    color: #666;
    background: #f0f0f0;
    padding: 2px 6px;
    border-radius: 3px;
}

.diff-content {
    padding: 15px;
    background: white;
    overflow-x: auto;
}

.diff-content pre {
    margin: 0;
    padding: 0;
    font-family: 'Courier New', Courier, monospace;
    font-size: 12px;
    line-height: 1.5;
    white-space: pre-wrap;
    word-wrap: break-word;
}

.diff-meta {
    padding: 8px 15px;
    background-color: #f9f9f9;
    border-top: 1px solid #eee;
    font-size: 12px;
    margin-top: 10px;
    font-size: 0.9em;
    color: #666;
}
</style>
