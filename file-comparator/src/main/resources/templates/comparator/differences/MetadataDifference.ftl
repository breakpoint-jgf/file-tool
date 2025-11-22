<#-- Metadata Difference Template -->
<#assign metaDiff = diff.description()>
<div class="diff-metrics">
    <h4>Metadata Differences</h4>
    <#if metaDiff.differingFields?? && metaDiff.differingFields?size gt 0>
        <table>
            <tr>
                <th>Field</th>
                <th>Value 1</th>
                <th>Value 2</th>
            </tr>
            <#list metaDiff.differingFields as field, values>
                <tr>
                    <td><strong>${field}</strong></td>
                    <td>${values[0]!"N/A"}</td>
                    <td>${values[1]!"N/A"}</td>
                </tr>
            </#list>
        </table>
    </#if>
</div>
