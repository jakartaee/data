package jakarta.data;

import java.util.List;

/**
 * A composite restriction representing a collection of individual {@link Restriction}
 * and {@link LogicalOperator} instances, combined under a single logical operation.
 *
 * <p>This record allows multiple restrictions to be treated as a single entity, making
 * it easy to pass complex conditions to repository methods.</p>
 *
 * @param <T> the entity type that the restrictions apply to.
 */
public record CompositeRestriction<T>(LogicalOperator operator, List<Restriction<T>> restrictions) {

    /**
     * Constructs a composite restriction with the specified operator and list of restrictions.
     *
     * @param operator     the logical operator (AND or OR) to apply between the restrictions.
     * @param restrictions the list of restrictions to combine.
     */
    public CompositeRestriction {
        restrictions = List.copyOf(restrictions); // Ensure immutability of the list
    }

    // Factory methods for creating composite restrictions with AND or OR logic

    /**
     * Creates a composite restriction where all specified restrictions must be true (AND logic).
     *
     * @param restrictions the individual restrictions to combine.
     * @param <T>          the entity type that the restrictions apply to.
     * @return a CompositeRestriction representing the AND combination of the provided restrictions.
     */
    @SafeVarargs
    public static <T> CompositeRestriction<T> all(Restriction<T>... restrictions) {
        return new CompositeRestriction<>(LogicalOperator.AND, List.of(restrictions));
    }

    /**
     * Creates a composite restriction where any of the specified restrictions may be true (OR logic).
     *
     * @param restrictions the individual restrictions to combine.
     * @param <T>          the entity type that the restrictions apply to.
     * @return a CompositeRestriction representing the OR combination of the provided restrictions.
     */
    @SafeVarargs
    public static <T> CompositeRestriction<T> any(Restriction<T>... restrictions) {
        return new CompositeRestriction<>(LogicalOperator.OR, List.of(restrictions));
    }
}
