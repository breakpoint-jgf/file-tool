package com.safe.room.file.tool.comparator.pdf;

import com.safe.room.file.tool.comparator.pdf.criteria.PdfComparisonCriteria;
import com.safe.room.file.tool.comparator.pdf.criteria.TextContentComparisonCriteria;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Configuration class for PDF comparison options.
 * Uses the Builder pattern to create immutable configuration instances.
 */
public final class PdfComparisonConfig {
    private final Map<Class<? extends PdfComparisonCriteria>, PdfComparisonCriteria> criteriaMap;

    private PdfComparisonConfig(Builder builder) {
        this.criteriaMap = Map.copyOf(builder.criteriaMap);
    }

    /**
     * @return an immutable list of all enabled comparison criteria
     */
    public List<PdfComparisonCriteria> getCriteria() {
        return new ArrayList<>(criteriaMap.values());
    }

    /**
     * Gets a specific criteria instance by its type.
     *
     * @param criteriaClass the class of the criteria to retrieve
     * @param <T> the type of the criteria
     * @return an Optional containing the criteria if found, empty otherwise
     */
    @SuppressWarnings("unchecked")
    public <T extends PdfComparisonCriteria> Optional<T> getCriteria(Class<T> criteriaClass) {
        return Optional.ofNullable((T) criteriaMap.get(criteriaClass));
    }

    /**
     * @return a new Builder instance for creating PdfComparisonConfig
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return a new instance with all comparison features enabled with default settings
     */
    public static PdfComparisonConfig allEnabled() {
        return builder()
            .withCriteria(new TextContentComparisonCriteria(false))
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
        private final Map<Class<? extends PdfComparisonCriteria>, PdfComparisonCriteria> criteriaMap = new HashMap<>();
        private final Map<Class<? extends PdfComparisonCriteria>, Supplier<? extends PdfComparisonCriteria>> criteriaSuppliers = new HashMap<>();

        private Builder() {
            // Register default criteria suppliers
            registerCriteria(TextContentComparisonCriteria.class, () -> new TextContentComparisonCriteria(false));
        }

        /**
         * Register a criteria supplier for a specific criteria type.
         * This allows for custom criteria to be used with the builder's fluent API.
         *
         * @param criteriaClass the criteria class
         * @param supplier the supplier that creates a new instance of the criteria
         * @param <T> the type of the criteria
         * @return this builder instance for method chaining
         */
        public <T extends PdfComparisonCriteria> Builder registerCriteria(
                Class<T> criteriaClass,
                Supplier<T> supplier) {
            criteriaSuppliers.put(criteriaClass, supplier);
            return this;
        }

        /**
         * Configures an existing criteria instance if it exists, or creates a new one using the registered supplier.
         *
         * @param criteriaClass the criteria class to configure
         * @param configurator the configuration to apply
         * @param <T> the type of the criteria
         * @return this builder instance for method chaining
         */
        @SuppressWarnings("unchecked")
        public <T extends PdfComparisonCriteria> Builder configureCriteria(
                Class<T> criteriaClass,
                Consumer<T> configurator) {
            T criteria = (T) criteriaMap.computeIfAbsent(
                criteriaClass,
                k -> Optional.ofNullable(criteriaSuppliers.get(k))
                           .map(Supplier::get)
                           .orElseThrow(() -> new IllegalArgumentException("No supplier registered for criteria: " + criteriaClass.getName()))
            );
            configurator.accept(criteria);
            return this;
        }


        /**
         * Adds a custom comparison criteria to the configuration.
         * Replaces any existing criteria of the same type.
         *
         * @param criteria the criteria to add
         * @return this builder instance for method chaining
         * @throws NullPointerException if criteria is null
         */
        public Builder withCriteria(PdfComparisonCriteria criteria) {
            Objects.requireNonNull(criteria, "Criteria cannot be null");
            criteriaMap.put(criteria.getClass(), criteria);
            return this;
        }

        /**
         * Removes a comparison criteria by its class.
         *
         * @param criteriaClass the class of the criteria to remove
         * @return this builder instance for method chaining
         */
        public Builder withoutCriteria(Class<? extends PdfComparisonCriteria> criteriaClass) {
            criteriaMap.remove(criteriaClass);
            return this;
        }
        
        /**
         * Removes all criteria from the configuration.
         *
         * @return this builder instance for method chaining
         */
        public Builder clearCriteria() {
            criteriaMap.clear();
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
