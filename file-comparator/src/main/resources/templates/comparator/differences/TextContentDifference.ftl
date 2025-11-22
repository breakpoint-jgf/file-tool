<#-- Text Content Difference Template -->
<#assign textDiff = diff.description()>
<#assign diffDetails = textDiff.getDiffDetails()!''>
<#assign pageNumber = textDiff.getPageNumber()!0>

<div class="diff-content">
    <h4>Text Difference</h4>
    <#if diffDetails?has_content>
        <p>${diffDetails?replace("\n", "<br>")?replace("\\n", "<br>")?replace("\\r", "")?replace("\\t", "    ")}</p>
    <#else>
        <p>${textDiff.getDescription()}</p>
    </#if>
    
    <#if pageNumber gt 0>
        <div class="diff-meta">
            <strong>Page:</strong> ${pageNumber}
        </div>
    </#if>
</div>
