<#-- Size Difference Template -->
<#assign sizeDiff = diff.description()>
<div class="diff-metrics">
    <h4>Size Difference</h4>
    <table>
        <tr>
            <td><strong>File 1 Size:</strong></td>
            <td>${sizeDiff.size1} ${sizeDiff.unit!"bytes"}</td>
        </tr>
        <tr>
            <td><strong>File 2 Size:</strong></td>
            <td>${sizeDiff.size2} ${sizeDiff.unit!"bytes"}</td>
        </tr>
        <tr>
            <td><strong>Difference:</strong></td>
            <td>${sizeDiff.size1 - sizeDiff.size2} ${sizeDiff.unit!"bytes"}</td>
        </tr>
    </table>
</div>
