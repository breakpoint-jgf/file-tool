package com.safe.room.file.tool.comparator.pdf.dto.descriptions;

import com.safe.room.file.tool.comparator.pdf.dto.DifferenceDescription;
import com.safe.room.file.tool.comparator.pdf.enumeration.DifferenceType;

import java.util.Map;

/**
 * Describes a difference in document metadata.
 */
public class MetadataDifference implements DifferenceDescription {
    private final Map<String, Object> differingFields;

    public MetadataDifference(Map<String, Object> differingFields) {
        this.differingFields = Map.copyOf(differingFields);
    }

    @Override
    public String getDescription() {
        if (differingFields.isEmpty()) {
            return "Metadata differs (no specific fields identified)";
        }
        return "Metadata differs in fields: " + String.join(", ", differingFields.keySet());
    }

    @Override
    public DifferenceType getType() {
        return DifferenceType.METADATA;
    }

    public Map<String, Object> getDifferingFields() {
        return Map.copyOf(differingFields);
    }
}
