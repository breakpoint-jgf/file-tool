<#-- Basic Difference Description Template -->
<#assign description = diff.description().getDescription()>
<div class="basic-diff">
    <p>${description}</p>
</div>

<style>
.basic-diff {
    margin: 10px 0;
    padding: 10px;
    border: 1px solid #e0e0e0;
    border-radius: 4px;
    background-color: #f9f9f9;
}

.basic-diff p {
    margin: 0;
    white-space: pre-wrap;
    word-break: break-word;
}
</style>
