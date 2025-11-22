package com.safe.room.file.tool.comparator.pdf.dto.descriptions;

import com.safe.room.file.tool.comparator.pdf.dto.DifferenceDescription;
import com.safe.room.file.tool.comparator.pdf.enumeration.DifferenceType;

/**
 * Describes a difference in file sizes.
 */
public class SizeDifference implements DifferenceDescription {
    private final long size1;
    private final long size2;
    private final String unit;

    public SizeDifference(long size1, long size2, String unit) {
        this.size1 = size1;
        this.size2 = size2;
        this.unit = unit;
    }

    @Override
    public String getDescription() {
        return String.format("File size differs: %d %s vs %d %s", size1, unit, size2, unit);
    }

    @Override
    public DifferenceType getType() {
        return DifferenceType.SIZE;
    }

    public long getSize1() {
        return size1;
    }

    public long getSize2() {
        return size2;
    }

    public String getUnit() {
        return unit;
    }
}
