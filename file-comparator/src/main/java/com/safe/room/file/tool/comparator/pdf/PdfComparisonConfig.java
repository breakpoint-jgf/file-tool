package com.safe.room.file.tool.comparator.pdf;

import com.safe.room.file.tool.comparator.pdf.criteria.PdfComparisonCriteria;
import com.safe.room.file.tool.comparator.pdf.criteria.TextContentComparisonCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Configuration class for PDF comparison options.
 * Uses the Builder pattern to create immutable configuration instances.
 */
public final class PdfComparisonConfig {
    private final List<PdfComparisonCriteria> criteriaList;

    private PdfComparisonConfig(Builder builder) {
        this.criteriaList = List.copyOf(builder.criteriaList);
    }

    /**
     * @return an immutable list of all enabled comparison criteria
     */
    public List<PdfComparisonCriteria> getCriteria() {
        return criteriaList;
    }

    /**
     * @return a new Builder instance for creating PdfComparisonConfig
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return a new instance with all comparison features enabled
     */
    public static PdfComparisonConfig allEnabled() {
        return builder()
            .withTextContentComparison()
            .build();
    }

    /**
     * @return a new instance with all comparison features disabled
     */
    public static PdfComparisonConfig allDisabled() {
        return builder().build();
    }

    /**
     * Builder for creating immutable PdfComparisonConfig instances.
     */
    public static final class Builder {
        private final List<PdfComparisonCriteria> criteriaList = new ArrayList<>();
        private boolean textContentIgnoreSpacing = false;

        private Builder() {
            // Private constructor
        }

        /**
         * Adds all default comparison criteria to the configuration.
         * @return this builder instance for method chaining
         */
        public Builder withAllDefaultCriteria() {
            return this
                .withTextContentComparison();
        }

        /**
         * Enables text content comparison with default settings (spacing differences are considered).
         * 
         * @return this builder instance for method chaining
         */
        public Builder withTextContentComparison() {
            return withTextContentComparison(false);
        }
        
        /**
         * Enables text content comparison with the option to ignore spacing differences.
         *
         * @param ignoreSpacingDifferences if true, differences in whitespace will be ignored
         * @return this builder instance for method chaining
         */
        public Builder withTextContentComparison(boolean ignoreSpacingDifferences) {
            this.textContentIgnoreSpacing = ignoreSpacingDifferences;
            return withCriteria(new TextContentComparisonCriteria(ignoreSpacingDifferences));
        }

        /**
         * Adds a custom comparison criteria to the configuration.
         *
         * @param criteria the criteria to add
         * @return this builder instance for method chaining
         * @throws NullPointerException if criteria is null
         */
        public Builder withCriteria(PdfComparisonCriteria criteria) {
            Objects.requireNonNull(criteria, "Criteria cannot be null");
            // Special handling for TextContentComparisonCriteria to maintain the ignoreSpacing flag
            if (criteria instanceof TextContentComparisonCriteria) {
                this.textContentIgnoreSpacing = ((TextContentComparisonCriteria) criteria).isIgnoreSpacingDifferences();
            }
            this.criteriaList.removeIf(c -> c.getType().equals(criteria.getType()));
            this.criteriaList.add(criteria);
            return this;
        }

        /**
         * Removes a comparison criteria by type.
         *
         * @param type the type of criteria to remove
         * @return this builder instance for method chaining
         */
        public Builder withoutCriteria(String type) {
            this.criteriaList.removeIf(c -> c.getType().equals(type));
            return this;
        }

        /**
         * Builds and returns a new immutable PdfComparisonConfig instance.
         *
         * @return a new PdfComparisonConfig instance
         */
        public PdfComparisonConfig build() {
            return new PdfComparisonConfig(this);
        }
    }
}
