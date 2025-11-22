package com.safe.room.file.tool.comparator;

import java.util.Objects;

/**
 * Represents a pair of file extensions to be compared.
 * This record is used as a key in the comparator registry.
 * The comparison is case-insensitive and order-independent.
 *
 * @param fileExtension1 the first file extension (without dot)
 * @param fileExtension2 the second file extension (without dot)
 * @throws IllegalArgumentException if either extension is null or empty
 */
public record FileCompareType(String fileExtension1, String fileExtension2) {
    public FileCompareType {
        if (fileExtension1 == null || fileExtension1.trim().isEmpty()) {
            throw new IllegalArgumentException("First file extension cannot be null or empty");
        }
        if (fileExtension2 == null || fileExtension2.trim().isEmpty()) {
            throw new IllegalArgumentException("Second file extension cannot be null or empty");
        }
        fileExtension1 = fileExtension1.trim().toLowerCase();
        fileExtension2 = fileExtension2.trim().toLowerCase();
    }

    /**
     * Checks if this FileCompareType matches the given file extensions in any order.
     *
     * @param ext1 the first file extension to check
     * @param ext2 the second file extension to check
     * @return true if the extensions match this type in any order, false otherwise
     */
    public boolean matches(String ext1, String ext2) {
        String ext1Lower = ext1.toLowerCase();
        String ext2Lower = ext2.toLowerCase();
        
        return (fileExtension1.equals(ext1Lower) && fileExtension2.equals(ext2Lower)) ||
               (fileExtension1.equals(ext2Lower) && fileExtension2.equals(ext1Lower));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileCompareType that = (FileCompareType) o;
        // Compare in both orders since (A,B) should be equal to (B,A)
        return (fileExtension1.equals(that.fileExtension1) && fileExtension2.equals(that.fileExtension2)) ||
               (fileExtension1.equals(that.fileExtension2) && fileExtension2.equals(that.fileExtension1));
    }

    @Override
    public int hashCode() {
        // Ensure that (A,B) and (B,A) generate the same hash code
        return fileExtension1.compareTo(fileExtension2) < 0 
            ? Objects.hash(fileExtension1, fileExtension2) 
            : Objects.hash(fileExtension2, fileExtension1);
    }

    @Override
    public String toString() {
        return fileExtension1 + "_" + fileExtension2;
    }
}
